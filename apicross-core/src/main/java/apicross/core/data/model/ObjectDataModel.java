package apicross.core.data.model;

import apicross.core.NamedDatum;
import io.swagger.v3.oas.models.media.Schema;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class ObjectDataModel extends DataModel {
    private String typeName;
    private Map<String, ObjectDataModelProperty> propertiesMap = new HashMap<>();

    private Map<String, ObjectDataModel> inheritanceChildModels;
    private String inheritanceDiscriminatorPropertyName;
    private ObjectDataModel inheritanceParent;
    private String inheritanceDiscriminatorValue;

    private Set<String> propertiesOriginSchemasNames;
    private DataModel additionalPropertiesDataModel;

    ObjectDataModel(String typeName, Schema<?> source) {
        super(source);
        this.typeName = typeName;
    }

    ObjectDataModel(String typeName, Schema<?> source, Set<ObjectDataModelProperty> properties,
                    DataModel additionalPropertiesDataModel) {
        this(typeName, source);
        this.propertiesMap = new LinkedHashMap<>();
        this.additionalPropertiesDataModel = additionalPropertiesDataModel;
        this.initPropertiesFrom(properties);
    }

    ObjectDataModel(String typeName, Schema<?> source, Map<String, ObjectDataModel> childModels, String discriminatorPropertyName, Map<String, String> mapping) {
        this(typeName, source);
        this.inheritanceDiscriminatorPropertyName = discriminatorPropertyName;
        this.inheritanceChildModels = new LinkedHashMap<>(childModels);
        for (ObjectDataModel childModel : this.inheritanceChildModels.values()) {
            childModel.inheritanceParent = this;
            childModel.inheritanceDiscriminatorValue = mapping != null ?
                    mapping.getOrDefault(childModel.typeName, childModel.typeName) : childModel.typeName;
        }

        // Some properties from child schemas might be declared in the same schemas.
        // For example SchemaA is a AllOf(SchemaX and something else), SchemaB is AllOf(SchemaX and something else),
        // so when SchemaA and SchemaB is child data models for this model, so their common properties can be moved to this data model

        Set<String> originSchemasNamesForCommonProperties = collectCommonPropertiesOriginSchemasNames(this.inheritanceChildModels);

        List<ObjectDataModelProperty> collectedCommonProperties = new ArrayList<>();
        for (ObjectDataModel childModel : this.inheritanceChildModels.values()) {
            List<ObjectDataModelProperty> propsToBeMovedToParent = removePropertiesForOriginSources(childModel, originSchemasNamesForCommonProperties);
            propsToBeMovedToParent.removeIf(property -> property.getName().equals(this.inheritanceDiscriminatorPropertyName));
            childModel.removeRequiredProperty(this.inheritanceDiscriminatorPropertyName);
            collectedCommonProperties.addAll(propsToBeMovedToParent);
        }
        this.initPropertiesFrom(collectedCommonProperties); // TODO: here in the collectedCommonProperties might be duplicates, but these are avoided in the initPropertiesFrom(), and it's not clear :(
    }

    private static Set<String> collectCommonPropertiesOriginSchemasNames(Map<String, ObjectDataModel> childSchemasMap) {
        Set<String> commonSchemasNames = new HashSet<>();

        for (ObjectDataModel childSchema : childSchemasMap.values()) {
            commonSchemasNames.addAll(childSchema.propertiesOriginSchemasNames);
        }
        for (ObjectDataModel childSchema : childSchemasMap.values()) {
            commonSchemasNames.retainAll(childSchema.propertiesOriginSchemasNames);
        }
        commonSchemasNames.removeIf(Objects::isNull);
        return commonSchemasNames;
    }

    private static List<ObjectDataModelProperty> removePropertiesForOriginSources(ObjectDataModel removeFromDataModel, Set<String> originSchemasNames) {
        List<ObjectDataModelProperty> propsToBeRemoved = new ArrayList<>();
        List<ObjectDataModelProperty> propsToBeLeft = new ArrayList<>();
        for (ObjectDataModelProperty property : removeFromDataModel.propertiesMap.values()) {
            String originSchemaName = property.getOriginSchemaName();
            if ((originSchemaName != null) && originSchemasNames.contains(originSchemaName)) {
                propsToBeRemoved.add(property);
            } else {
                propsToBeLeft.add(property);
            }
        }
        removeFromDataModel.propertiesMap.clear();
        removeFromDataModel.initPropertiesFrom(propsToBeLeft);
        return propsToBeRemoved;
    }

    @Override
    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    private void initPropertiesFrom(Collection<ObjectDataModelProperty> properties) {
        Set<String> schemasNames = new HashSet<>();
        for (ObjectDataModelProperty property : properties) {
            this.propertiesMap.put(property.getName(), property);
            String originSchemaName = property.getOriginSchemaName();
            schemasNames.add(originSchemaName);
        }
        schemasNames.removeIf(Objects::isNull);
        this.propertiesOriginSchemasNames = Collections.unmodifiableSet(schemasNames);
    }

    public Set<ObjectDataModelProperty> getProperties() {
        return new LinkedHashSet<>(this.propertiesMap.values());
    }

    @Nullable
    public ObjectDataModelProperty getProperty(String name) {
        return this.propertiesMap.get(name);
    }

    public Integer getMinProperties() {
        return getSource().getMinProperties();
    }

    public Integer getMaxProperties() {
        return getSource().getMaxProperties();
    }

    public List<String> getRequiredProperties() {
        return getSource().getRequired();
    }

    public void removeRequiredProperty(String propertyName) {
        List<String> required = getSource().getRequired();
        if (required != null) {
            required.remove(propertyName);
        }
    }

    public DataModel getAdditionalPropertiesDataModel() {
        return this.additionalPropertiesDataModel;
    }

    public Map<String, ObjectDataModel> getInheritanceChildModelsMap() {
        return this.inheritanceChildModels;
    }

    public Collection<ObjectDataModel> getInheritanceChildModels() {
        return this.inheritanceChildModels != null ? this.inheritanceChildModels.values() : null;
    }

    public String getInheritanceDiscriminatorPropertyName() {
        return this.inheritanceDiscriminatorPropertyName;
    }

    public ObjectDataModel getInheritanceParent() {
        return this.inheritanceParent;
    }

    public String getInheritanceDiscriminatorValue() {
        return this.inheritanceDiscriminatorValue;
    }

    public void changeTypeName(String newTypeName, boolean clear) {
        this.typeName = newTypeName;
        if (clear) {
            this.propertiesMap.clear();
            this.inheritanceChildModels = null;
            this.inheritanceDiscriminatorPropertyName = null;
            this.inheritanceParent = null;
            this.additionalPropertiesDataModel = null;
        }
    }

    public void changeTypeName(String newTypeName) {
        changeTypeName(newTypeName, true);
    }

    public boolean isContainingOptionalProperties() {
        return getProperties()
                .stream()
                .anyMatch(NamedDatum::isOptional);
    }

    public boolean isContainingArrayProperties() {
        Set<ObjectDataModelProperty> properties = getProperties();
        for (ObjectDataModelProperty property : properties) {
            if (property.getType().isArray()) {
                return true;
            }
        }
        return false;
    }

    public void replacePropertyTypeByExternalTypesMap(Map<String, String> externalTypesMap) {
        // TODO: make it deeper - replace for array items, for example
        Set<ObjectDataModelProperty> properties = this.getProperties();
        for (ObjectDataModelProperty property : properties) {
            DataModel dataModel = property.getType();
            if (dataModel.isObject()) {
                String typeName = dataModel.getTypeName();
                if (externalTypesMap.containsKey(typeName)) {
                    property.changeTypeToExternal(externalTypesMap.get(typeName));
                }
            }
        }
    }

    public boolean isInheritanceChild() {
        return getInheritanceParent() != null;
    }

    public String getInheritanceParentTypeName() {
        return this.inheritanceParent != null ? this.inheritanceParent.getTypeName() : null;
    }

    public void removeProperty(String apiPropertyName) {
        this.propertiesMap.remove(apiPropertyName);
    }

    @Override
    public String toString() {
        return "ObjectDataModelSchema{" +
                "typeName='" + this.typeName + '\'' +
                '}';
    }
}
