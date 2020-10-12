package apicross.core.data;

import apicross.core.data.model.*;
import apicross.utils.OpenApiComponentsIndex;
import apicross.utils.SchemaHelper;
import io.swagger.v3.oas.models.media.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import javax.annotation.Nonnull;
import java.util.*;

@Slf4j
public class DataModelResolver {
    private final PropertyNameResolver propertyNameResolver;
    private final OpenApiComponentsIndex openAPIComponentsIndex;
    private final Map<String, DataModel> by$refResolutionCache = new HashMap<>();
    private final Stack<String> resolvingPropertiesStack = new Stack<>();

    public DataModelResolver(OpenApiComponentsIndex openAPIComponentsIndex, PropertyNameResolver propertyNameResolver) {
        this.openAPIComponentsIndex = openAPIComponentsIndex;
        this.propertyNameResolver = propertyNameResolver;
    }

    @Nonnull
    public static List<ObjectDataModel> resolveInlineModels(InlineModelTypeNameResolver resolver, ObjectDataModel source) {
        List<ObjectDataModel> result = new ArrayList<>();
        doResolveInlineModels(result, resolver, source);
        return result;
    }

    @Nonnull
    public static List<ObjectDataModel> resolveInlineModels(InlineModelTypeNameResolver resolver, ArrayDataModel arrayDataModel) {
        List<ObjectDataModel> result = new ArrayList<>();
        DataModel arrayItemType = arrayDataModel.getItemsDataModel();
        if (arrayItemType.isObject() && arrayItemType.getTypeName() == null) {
            ObjectDataModel objectArrayItemType = (ObjectDataModel) arrayItemType;
            objectArrayItemType.setTypeName(resolver.resolveArrayItemTypeName(arrayDataModel.getTypeName(), ""));
            result.add(objectArrayItemType); // TODO: really needed?
            List<ObjectDataModel> objectDataModels = resolveInlineModels(resolver, objectArrayItemType);
            result.addAll(objectDataModels);
        }
        return result;
    }

    private static void doResolveInlineModels(List<ObjectDataModel> collectTo, InlineModelTypeNameResolver resolver,
                                              ObjectDataModel source) {
        Collection<ObjectDataModelProperty> properties = source.getProperties();
        for (ObjectDataModelProperty property : properties) {
            DataModel type = property.getType();
            if (type.isObject() && type.getTypeName() == null) {
                ObjectDataModel propertyType = (ObjectDataModel) type;
                propertyType.setTypeName(resolver.resolveTypeName(source.getTypeName(), property.getResolvedName()));
                collectTo.add(propertyType);
                doResolveInlineModels(collectTo, resolver, propertyType);
            } else if (type.isArray()) {
                ArrayDataModel propertyType = (ArrayDataModel) type;
                DataModel arrayItemType = propertyType.getItemsDataModel();
                if (arrayItemType.isObject() && arrayItemType.getTypeName() == null) {
                    ObjectDataModel objectArrayItemType = (ObjectDataModel) arrayItemType;
                    objectArrayItemType.setTypeName(resolver.resolveArrayItemTypeName(source.getTypeName(), property.getResolvedName()));
                    collectTo.add(objectArrayItemType);
                    doResolveInlineModels(collectTo, resolver, objectArrayItemType);
                }
            }
        }

        DataModel additionalPropertiesType = source.getAdditionalPropertiesDataModel();
        if (additionalPropertiesType != null && additionalPropertiesType.isObject()) {
            if (additionalPropertiesType.getTypeName() == null) {
                ObjectDataModel objectType = (ObjectDataModel) additionalPropertiesType;
                objectType.setTypeName(resolver.resolveTypeName(source.getTypeName(), "additionalProperties"));
                collectTo.add(objectType);
                doResolveInlineModels(collectTo, resolver, objectType);
            }
        }
    }

    @Nonnull
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
        } else if (SchemaHelper.isPrimitiveTypeSchema(schema) || SchemaHelper.isPrimitiveLikeSchema(schema)) {
            return DataModel.newPrimitiveType(schema);
        } else if (schema instanceof ArraySchema) {
            return resolveArraySchema((ArraySchema) schema);
        } else if (schema instanceof ComposedSchema) {
            if (((ComposedSchema) schema).getAllOf() != null) {
                return resolveAllOfSchema((ComposedSchema) schema);
            } else if (((ComposedSchema) schema).getOneOf() != null) {
                return resolveOneOfSchema(schema);
            } else if ((((ComposedSchema) schema).getAnyOf() != null)) {
                // TODO: how to resolve anyOf ???
                return DataModel.newPrimitiveType(schema);
            }
        } else if (schema instanceof MapSchema) {
            return resolveMapSchema(schema);
        } else if (schema instanceof ObjectSchema || schema.getProperties() != null) {
            return resolveObjectSchema(schema);
        }

        throw new DataModelResolverException("Unsupported schema: " + schema);
    }

    private DataModel resolveMapSchema(Schema<?> schema) {
        DataModel additionalPropertiesDataModel;

        Object additionalProperties = schema.getAdditionalProperties();

        if (additionalProperties instanceof Schema) {
            Schema<?> additionalPropertiesSchema = (Schema<?>) additionalProperties;
            additionalPropertiesDataModel = resolveMayBe$ref(additionalPropertiesSchema);
        } else if (additionalProperties instanceof Boolean) {
            additionalPropertiesDataModel = DataModel.anyType();
        } else {
            throw new DataModelResolverException("Only 'additionalProperties : true' and 'additionalProperties: <schema>' supported");
        }

        return resolveObjectSchema(schema, additionalPropertiesDataModel);
    }

    private DataModel resolveOneOfSchema(Schema<?> schema) {
        List<Schema> partsSchemas = ((ComposedSchema) schema).getOneOf();
        Map<String, ObjectDataModel> childTypes = new HashMap<>();
        for (Schema<?> partSchema : partsSchemas) {
            if (partSchema.get$ref() == null) {
                throw new DataModelResolverException("oneOf only with $ref items supported");
            }
            DataModel childDataModel = resolveFrom$ref(partSchema.get$ref());
            if (childDataModel instanceof ObjectDataModel) {
                childTypes.put(childDataModel.getTypeName(), (ObjectDataModel) childDataModel);
            } else {
                throw new DataModelResolverException("Only object type schemas can be inheritance child");
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

    private DataModel resolveAllOfSchema(ComposedSchema schema) {
        List<Schema> partsSchemas = schema.getAllOf();

        Set<ObjectDataModelProperty> allProperties = new LinkedHashSet<>();
        Set<String> requiredProperties = new HashSet<>();
        Set<ObjectDataModel> objectPartsModels = new LinkedHashSet<>();
        Set<PrimitiveDataModel> primitivePartsModels = new LinkedHashSet<>();

        int anonymousPartsCount = 0;
        Schema<?> anonymousPart = null;
        String description = null;

        for (Schema<?> partSchema : partsSchemas) {
            DataModel partTypeSchema = resolveMayBe$ref(partSchema);
            if (partTypeSchema.isObject()) {
                ObjectDataModel objectDataModel = (ObjectDataModel) partTypeSchema;
                objectPartsModels.add(objectDataModel);

                if (partSchema.getRequired() != null) {
                    requiredProperties.addAll(partSchema.getRequired());
                }

                allProperties.addAll(objectDataModel.getProperties());
            }

            if (partTypeSchema.isPrimitive()) {
                primitivePartsModels.add((PrimitiveDataModel) partTypeSchema);
            }

            if (SchemaHelper.isPrimitiveLikeSchema(partSchema)) { // only schemas to mix-in constraints, e.g. 'nullable: true' or 'minLength: 10'
                anonymousPartsCount++;
                anonymousPart = partSchema;
            }

            if (partSchema.getDescription() != null) {
                description = partSchema.getDescription();
            }
        }

        if (inFieldResolutionContext() && anonymousPartsCount == 1) {
            // this magic is to handle constructions like this:
            // property1:
            //   allOf:
            //      - $ref: ...
            //      - description: ...
            //        nullable: true
            // i.e. when allOf is used to add mix-in to the referenced schema, for example - to override description or schema constraints
            if (objectPartsModels.size() == 1) {
                ObjectDataModel objectDataModel = objectPartsModels.iterator().next();
                mixInObjectDataModelConstraints(anonymousPart, objectDataModel.getSource());
                return objectDataModel;
            }

            if (primitivePartsModels.size() >= 1) {
                PrimitiveDataModel primitiveDataModel = primitivePartsModels.iterator().next();
                mixInPrimitiveDataModelConstraints(anonymousPart, primitiveDataModel.getSource());
                return primitiveDataModel;
            }
        }

        for (ObjectDataModelProperty property : allProperties) {
            if (requiredProperties.contains(property.getName())) {
                property.markRequired();
            }
        }

        if (description != null) {
            schema.setDescription(description);
        }

        return DataModel.newObjectType(schema, schema.getName(), allProperties, null);
    }

    private void mixInPrimitiveDataModelConstraints(Schema<?> source, Schema<?> target) {
        if (source.getDescription() != null) {
            target.setDescription(source.getDescription());
        }
        if (source.getMaximum() != null) {
            target.setMaximum(source.getMaximum());
        }
        if (source.getMinimum() != null) {
            target.setMinimum(source.getMinimum());
        }
        if (source.getMaxLength() != null) {
            target.setMaxLength(source.getMaxLength());
        }
        if (source.getMinLength() != null) {
            target.setMinLength(source.getMinLength());
        }
        if (source.getNullable() != null) {
            target.setNullable(source.getNullable());
        }
        if (source.getPattern() != null) {
            target.setPattern(source.getPattern());
        }
        if (source.getExclusiveMaximum() != null) {
            target.setExclusiveMaximum(source.getExclusiveMaximum());
        }
        if (source.getExclusiveMinimum() != null) {
            target.setExclusiveMinimum(source.getExclusiveMinimum());
        }
//        if (source.getEnum() != null) {
//            target.setEnum(source.getEnum());
//        }
    }

    private void mixInObjectDataModelConstraints(Schema<?> source, Schema<?> target) {
        if (source.getDescription() != null) {
            target.setDescription(source.getDescription());
        }
        if (source.getMinProperties() != null) {
            target.setMinProperties(source.getMinProperties());
        }
        if (source.getMaxProperties() != null) {
            target.setMaxProperties(source.getMaxProperties());
        }
        if (source.getNullable() != null) {
            target.setNullable(source.getNullable());
        }
    }

    private boolean inFieldResolutionContext() {
        return !resolvingPropertiesStack.isEmpty();
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
        Set<ObjectDataModelProperty> properties = resolveObjectSchemaProperties(schema);
        return DataModel.newObjectType(schema, schema.getName(), properties, additionalPropertiesDataModel);
    }

    private Set<ObjectDataModelProperty> resolveObjectSchemaProperties(Schema<?> schema) {
        Map<String, Schema> properties = schema.getProperties();
        Set<ObjectDataModelProperty> result = new LinkedHashSet<>();
        if (properties != null) {
            for (String apiPropertyName : properties.keySet()) {
                Schema<?> propertySchema = properties.get(apiPropertyName);
                resolvingPropertiesStack.push(apiPropertyName);
                try {
                    DataModel propertyDataModel = resolveMayBe$ref(propertySchema);
                    String description = propertySchema.getDescription();

                    if (description == null) {
                        description = propertyDataModel.getSource().getDescription();
                    }

                    boolean propertyRequired;
                    if (schema.getRequired() != null) {
                        propertyRequired = schema.getRequired().contains(apiPropertyName);
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
                } finally {
                    resolvingPropertiesStack.pop();
                }
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
}
