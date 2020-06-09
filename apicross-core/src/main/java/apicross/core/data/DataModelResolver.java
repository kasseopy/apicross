package apicross.core.data;

import apicross.CodeGeneratorException;
import apicross.utils.OpenApiComponentsIndex;
import apicross.utils.SchemaHelper;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import io.swagger.v3.oas.models.media.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class DataModelResolver {
    private final PropertyNameResolver propertyNameResolver;
    private final OpenApiComponentsIndex openAPIComponentsIndex;
    private final Map<String, DataModel> by$refResolutionCache = new HashMap<>();

    public DataModelResolver(OpenApiComponentsIndex openAPIComponentsIndex, PropertyNameResolver propertyNameResolver) {
        this.openAPIComponentsIndex = openAPIComponentsIndex;
        this.propertyNameResolver = propertyNameResolver;
    }

    public DataModel resolve(Schema<?> schema) {
        String schema$ref = schema.get$ref();
        String schemaName = schema.getName();

        if (schema$ref == null && schemaName != null) {
            schema$ref = "#/components/schemas/" + schemaName;
        }

        log.info("Resolving schema, name: {}, $ref: {}, description: {}", schemaName, schema$ref, schema.getDescription());

        if (schema$ref != null && by$refResolutionCache.containsKey(schema$ref)) {
            log.info("Found cached schema (by $ref), name: {}, $ref: {}, description: {}", schemaName, schema$ref, schema.getDescription());
            return by$refResolutionCache.get(schema$ref);
        }

        log.info("No cached schema, name: {}, $ref: {}, description: {}", schemaName, schema$ref, schema.getDescription());

        DataModel dataModel = doResolve(schema);

        if (schema$ref != null) {
            by$refResolutionCache.put(schema$ref, dataModel);
        }
        return dataModel;
    }

    private DataModel doResolve(Schema<?> schema) {
        String schemaName = schema.getName();
        String schema$ref = schema.get$ref();

        log.debug("Resolving schema with name: {}, type: {}, $ref: {}", schemaName, schema.getClass().getSimpleName(), schema$ref);

        if (schema$ref != null) {
            return resolveFrom$ref(schema$ref);
        } else if (SchemaHelper.isPrimitiveTypeSchema(schema)) {
            return DataModel.newPrimitiveType(schema);
        } else if (schema instanceof ArraySchema) {
            return resolveArraySchema((ArraySchema) schema);
        } else if (schema instanceof ComposedSchema) {
            if (((ComposedSchema) schema).getAllOf() != null) {
                return resolveAllOfSchema(schema);
            } else if (((ComposedSchema) schema).getOneOf() != null) {
                return resolveOneOfSchema(schema);
            } else if ((((ComposedSchema) schema).getAnyOf() != null)) {
                return DataModel.newPrimitiveType(schema);
            }
        } else if (schema instanceof MapSchema) {
            return resolveMapSchema(schema);
        } else if (schema instanceof ObjectSchema || schema.getProperties() != null) {
            return resolveObjectSchema(schema);
        }

        throw new CodeGeneratorException("Unsupported schema: " + schema);
    }

    private DataModel resolveMapSchema(Schema<?> schema) {
        DataModel additionalPropertiesDataModel = null;

        Object additionalProperties = schema.getAdditionalProperties();

        if (additionalProperties instanceof Schema) {
            Schema<?> additionalPropertiesSchema = (Schema<?>) additionalProperties;
            additionalPropertiesDataModel = resolveMayBe$ref(additionalPropertiesSchema);
        } else if (additionalProperties instanceof Boolean) {
            additionalPropertiesDataModel = DataModel.anyType();
        }

        return resolveObjectSchema(schema, additionalPropertiesDataModel);
    }

    private DataModel resolveOneOfSchema(Schema<?> schema) {
        List<Schema> parts = ((ComposedSchema) schema).getOneOf();
        Map<String, ObjectDataModel> childTypes = new HashMap<>();
        for (Schema part : parts) {
            Preconditions.checkArgument(part.get$ref() != null, "oneOf only with $ref supported");
            DataModel childDataModel = resolveFrom$ref(part.get$ref());
            if (childDataModel instanceof ObjectDataModel) {
                childTypes.put(childDataModel.getTypeName(), (ObjectDataModel) childDataModel);
            } else {
                throw new IllegalStateException("Only object type schemas can be inheritance child");
            }
        }

        Discriminator discriminator = schema.getDiscriminator();
        Map<String, String> mappingTypesByKeys = discriminator.getMapping();
        Map<String, String> mappingKeysByTypes = new LinkedHashMap<>();
        if (mappingTypesByKeys != null) {
            for (Map.Entry<String, String> entry : mappingTypesByKeys.entrySet()) {
                mappingKeysByTypes.put(SchemaHelper.schemaNameFromRef(entry.getValue()), entry.getKey());
            }
        }

        return DataModel.newObjectType(schema, schema.getName(),
                childTypes, discriminator.getPropertyName(), mappingKeysByTypes);
    }

    private DataModel resolveAllOfSchema(Schema<?> schema) {
        List<Schema> parts = ((ComposedSchema) schema).getAllOf();
        Set<ObjectDataModelProperty> allProperties = new LinkedHashSet<>();
        Set<String> requiredProperties = new HashSet<>();
        String description = null;
        for (Schema<?> part : parts) {
            DataModel partTypeSchema = resolveMayBe$ref(part);
            description = part.getDescription();
            if (part.getRequired() != null) {
                requiredProperties.addAll(part.getRequired());
            }
            if (partTypeSchema instanceof ObjectDataModel) {
                Set<ObjectDataModelProperty> partProperties = ((ObjectDataModel) partTypeSchema).getProperties();
                allProperties.addAll(partProperties);
            }
        }
        for (ObjectDataModelProperty property : allProperties) {
            if (requiredProperties.contains(property.getName())) {
                property.markRequired();
            }
        }
        schema.setDescription(description);
        ObjectDataModelConstraints typeLevelConstraints = resolveAllOfConstraints(resolveReferenced(parts));
        return DataModel.newObjectType(schema, schema.getName(), allProperties, typeLevelConstraints, null);
    }

    private List<Schema> resolveReferenced(List<Schema> parts) {
        List<Schema> resolved = new ArrayList<>();
        for (Schema part : parts) {
            if (part.get$ref() != null) {
                resolved.add(openAPIComponentsIndex.schemaBy$ref(part.get$ref()));
            } else {
                resolved.add(part);
            }
        }
        return resolved;
    }

    private ArrayDataModel resolveArraySchema(ArraySchema schema) {
        Schema<?> arrayItemsSchema = schema.getItems();
        DataModel arrayItemsDataModel = resolveMayBe$ref(arrayItemsSchema);
        return DataModel.newArrayType(schema, arrayItemsDataModel);
    }

    private ObjectDataModel resolveObjectSchema(Schema<?> schema) {
        return resolveObjectSchema(schema, null);
    }

    private ObjectDataModel resolveObjectSchema(Schema<?> schema, DataModel additionalPropertiesDataModel) {
        ObjectDataModelConstraints constraints = resolveConstraints(schema);
        Set<ObjectDataModelProperty> properties = resolveObjectSchemaProperties(schema);
        return DataModel.newObjectType(schema, schema.getName(), properties, constraints, additionalPropertiesDataModel);
    }

    private Set<ObjectDataModelProperty> resolveObjectSchemaProperties(Schema<?> schema) {
        Map<String, Schema> properties = schema.getProperties();
        Set<ObjectDataModelProperty> result = new LinkedHashSet<>();
        if (properties != null) {
            for (String apiPropertyName : properties.keySet()) {
                Schema<?> propertySchema = properties.get(apiPropertyName);
                DataModel propertyDataModel = resolveMayBe$ref(propertySchema);
                String description = propertySchema.getDescription();

                if (description == null) {
                    description = propertyDataModel.getSource().getDescription();
                }

                boolean propertyRequired;
                if (((Schema) schema).getRequired() != null) {
                    propertyRequired = ((Schema) schema).getRequired().contains(apiPropertyName);
                } else {
                    propertyRequired = false;
                }

                result.add(new ObjectDataModelProperty(
                        apiPropertyName,
                        propertyNameResolver.resolvePropertyName(propertySchema, apiPropertyName),
                        description,
                        propertyDataModel,
                        schema, propertyRequired,
                        BooleanUtils.isTrue(propertySchema.getDeprecated())));
            }
        }
        return result;
    }

    private DataModel resolveMayBe$ref(Schema<?> schema) {
        return (schema.get$ref() == null) ?
                doResolve(schema) : resolveFrom$ref(schema.get$ref());
    }

    private DataModel resolveFrom$ref(String $ref) {
        Schema<?> targetSchema = openAPIComponentsIndex.schemaBy$ref($ref);
        return resolve(targetSchema);
    }

    private ObjectDataModelConstraints resolveConstraints(@Nonnull Schema<?> schema) {
        Preconditions.checkArgument(isObjectSchema(schema) || (schema instanceof MapSchema));
        ObjectDataModelConstraints result = new ObjectDataModelConstraints();
        Integer minProperties = schema.getMinProperties();
        Integer maxProperties = schema.getMaxProperties();
        List<String> requiredProperties = schema.getRequired();
        if (minProperties != null) {
            result.setMinProperties(minProperties);
        }
        if (maxProperties != null) {
            result.setMaxProperties(maxProperties);
        }
        if (requiredProperties != null && requiredProperties.size() > 0) {
            result.setRequiredProperties(
                    new LinkedHashSet<>(requiredProperties));
        }
        return result;
    }

    private ObjectDataModelConstraints resolveAllOfConstraints(@Nonnull List<Schema> parts) {
        // TODO: what to do with min/maxProperties? sum values for allOf ?
        ObjectDataModelConstraints result = new ObjectDataModelConstraints();
        Set<String> allRequiredProperties = new LinkedHashSet<>();
        for (Schema<?> schema : parts) {
            List<String> requiredProperties = schema.getRequired();
            if (requiredProperties != null && isObjectSchema(schema)) {
                allRequiredProperties.addAll(requiredProperties);
            }
        }
        if (allRequiredProperties.size() > 0) {
            result.setRequiredProperties(
                    new LinkedHashSet<>(allRequiredProperties));
        }
        return result;
    }

    private boolean isObjectSchema(Schema<?> schema) {
        return schema != null && ((schema instanceof ObjectSchema) || schema.getProperties() != null);
    }
}
