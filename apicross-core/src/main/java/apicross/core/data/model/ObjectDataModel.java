package apicross.core.data.model;

import apicross.core.NamedDatum;
import apicross.core.data.InlineModelTypeNameResolver;
import io.swagger.v3.oas.models.media.Schema;

import javax.annotation.Nullable;
import java.util.*;

public class ObjectDataModel extends DataModel {
    private String typeName;
    private Map<String, ObjectDataModelProperty> propertiesMap = new HashMap<>();

    private Map<String, ObjectDataModel> inheritanceChildModels;
    private String inheritanceDiscriminatorPropertyName;
    private ObjectDataModel inheritanceParent;
    private String inheritanceDiscriminatorValue;

    private Set<String> propertiesOriginSchemasNames;
    private ObjectDataModelConstraints typeLevelConstraints = new ObjectDataModelConstraints();
    private DataModel additionalPropertiesDataModel;

    ObjectDataModel(String typeName, Schema<?> source) {
        super(source);
        this.typeName = typeName;
    }

    ObjectDataModel(String typeName, Schema<?> source, Set<ObjectDataModelProperty> properties,
                    ObjectDataModelConstraints typeLevelConstraints,
                    DataModel additionalPropertiesDataModel) {
        this(typeName, source);
        this.propertiesMap = new LinkedHashMap<>();
        this.typeLevelConstraints = typeLevelConstraints;
        this.additionalPropertiesDataModel = additionalPropertiesDataModel;
        this.initPropertiesFrom(properties);
    }

    ObjectDataModel(String typeName, Schema<?> source, Map<String, ObjectDataModel> childModels, String discriminatorPropertyName, Map<String, String> mapping) {
        this(typeName, source);
        this.inheritanceDiscriminatorPropertyName = discriminatorPropertyName;
        this.inheritanceChildModels = new LinkedHashMap<>(childModels);
        for (ObjectDataModel childModel : inheritanceChildModels.values()) {
            childModel.inheritanceParent = this;
            childModel.inheritanceDiscriminatorValue = mapping != null ?
                    mapping.getOrDefault(childModel.typeName, childModel.typeName) : childModel.typeName;
        }

        // Some properties from child schemas might be declared in the same schamas.
        // For example SchemaA is a AllOf(SchemaX and something else), SchemaB is AllOf(SchemaX and something else),
        // so when SchemaA and SchemaB is child data models for this model, so their common properties can be moved to this data model

        Set<String> originSchemasNamesForCommonProperties = collectCommonPropertiesOriginSchemasNames(this.inheritanceChildModels);

        List<ObjectDataModelProperty> collectedCommonProperties = new ArrayList<>();
        for (ObjectDataModel childModel : this.inheritanceChildModels.values()) {
            List<ObjectDataModelProperty> propsToBeMovedToParent = removePropertiesForOriginSources(childModel, originSchemasNamesForCommonProperties);
            propsToBeMovedToParent.removeIf(property -> property.getName().equals(this.inheritanceDiscriminatorPropertyName));
            childModel.getTypeLevelConstraints().removeRequiredProperty(this.inheritanceDiscriminatorPropertyName);
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
        return typeName;
    }

    void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    private void initPropertiesFrom(Collection<ObjectDataModelProperty> properties) {
        Set<String> schemasNames = new HashSet<>();
        for (ObjectDataModelProperty property : properties) {
            propertiesMap.put(property.getName(), property);
            String originSchemaName = property.getOriginSchemaName();
            schemasNames.add(originSchemaName);
        }
        schemasNames.removeIf(Objects::isNull);
        this.propertiesOriginSchemasNames = Collections.unmodifiableSet(schemasNames);
    }

    public Set<ObjectDataModelProperty> getProperties() {
        return new LinkedHashSet<>(propertiesMap.values());
    }

    @Nullable
    public ObjectDataModelProperty getProperty(String name) {
        return propertiesMap.get(name);
    }

    public ObjectDataModelConstraints getTypeLevelConstraints() {
        return typeLevelConstraints;
    }

    public DataModel getAdditionalPropertiesDataModel() {
        return additionalPropertiesDataModel;
    }

    public Map<String, ObjectDataModel> getInheritanceChildModelsMap() {
        return inheritanceChildModels;
    }

    public Collection<ObjectDataModel> getInheritanceChildModels() {
        return inheritanceChildModels != null ? inheritanceChildModels.values() : null;
    }

    public String getInheritanceDiscriminatorPropertyName() {
        return inheritanceDiscriminatorPropertyName;
    }

    public ObjectDataModel getInheritanceParent() {
        return inheritanceParent;
    }

    public String getInheritanceDiscriminatorValue() {
        return inheritanceDiscriminatorValue;
    }

    public void changeTypeName(String newTypeName, boolean clear) {
        this.typeName = newTypeName;
        if (clear) {
            this.propertiesMap.clear();
            this.typeLevelConstraints = new ObjectDataModelConstraints();
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
            DataModel type = property.getType();
            if (type.isObject()) {
                String typeName = type.getTypeName();
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
        return inheritanceParent != null ? inheritanceParent.getTypeName() : null;
    }

    public List<ObjectDataModel> resolveInlineModels(InlineModelTypeNameResolver resolver) {
        List<ObjectDataModel> result = new ArrayList<>();
        doResolveInlineModels(result, resolver, this);
        return result;
    }

    private static void doResolveInlineModels(List<ObjectDataModel> collectTo, InlineModelTypeNameResolver resolver,
                                              ObjectDataModel source) {
        Collection<ObjectDataModelProperty> properties = source.propertiesMap.values();
        for (ObjectDataModelProperty property : properties) {
            DataModel type = property.getType();
            if (type.isObject() && type.getTypeName() == null) {
                ObjectDataModel propertyType = (ObjectDataModel) type;
                propertyType.typeName = resolver.resolveTypeName(source.typeName, property.getResolvedName());
                collectTo.add(propertyType);
                doResolveInlineModels(collectTo, resolver, propertyType);
            } else if (type.isArray()) {
                ArrayDataModel propertyType = (ArrayDataModel) type;
                DataModel arrayItemType = propertyType.getItemsDataModel();
                if (arrayItemType.isObject() && arrayItemType.getTypeName() == null) {
                    ObjectDataModel objectArrayItemType = (ObjectDataModel) arrayItemType;
                    objectArrayItemType.setTypeName(resolver.resolveArrayItemTypeName(source.typeName, property.getResolvedName()));
                    collectTo.add(objectArrayItemType);
                    doResolveInlineModels(collectTo, resolver, objectArrayItemType);
                }
            }
        }
        DataModel additionalPropertiesType = source.getAdditionalPropertiesDataModel();
        if (additionalPropertiesType != null && additionalPropertiesType.isObject()) {
            if (additionalPropertiesType.getTypeName() == null) {
                ObjectDataModel objectType = (ObjectDataModel) additionalPropertiesType;
                objectType.typeName = resolver.resolveTypeName(source.typeName, "additionalProperties");
                collectTo.add(objectType);
                doResolveInlineModels(collectTo, resolver, objectType);
            }
        }
    }

    @Override
    public String toString() {
        return "ObjectDataModelSchema{" +
                "typeName='" + typeName + '\'' +
                '}';
    }
}
