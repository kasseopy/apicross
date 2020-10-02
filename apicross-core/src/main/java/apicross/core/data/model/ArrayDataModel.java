package apicross.core.data.model;

import apicross.core.data.InlineModelTypeNameResolver;
import io.swagger.v3.oas.models.media.ArraySchema;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArrayDataModel extends DataModel {
    private final String typeName;
    private final DataModel itemsDataModel;

    ArrayDataModel(DataModel itemsDataModel, ArraySchema source) {
        super(source);
        this.typeName = source.getName();
        this.itemsDataModel = Objects.requireNonNull(itemsDataModel);
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isUniqueItems() {
        return BooleanUtils.isTrue(getSource().getUniqueItems());
    }

    public boolean isArrayLengthConstrained() {
        return isMinItemsDefined() || isMaxItemsDefined();
    }

    public int getMinItems() {
        if (!isMinItemsDefined()) {
            throw new IllegalStateException("'minItems' constraint is not defined");
        }
        return getSource().getMinItems();
    }

    public boolean isMinItemsDefined() {
        return getSource().getMinItems() != null;
    }

    public int getMaxItems() {
        if (!isMaxItemsDefined()) {
            throw new IllegalStateException("'maxItems' constraint is not defined");
        }
        return getSource().getMaxItems();
    }

    public boolean isMaxItemsDefined() {
        return getSource().getMaxItems() != null;
    }

    public DataModel getItemsDataModel() {
        return itemsDataModel;
    }

    public String getArrayItemTypeName() {
        return getItemsDataModel().getTypeName();
    }

    public List<ObjectDataModel> resolveInlineModels(InlineModelTypeNameResolver resolver) {
        List<ObjectDataModel> result = new ArrayList<>();
        DataModel arrayItemType = this.getItemsDataModel();
        if (arrayItemType.isObject() && arrayItemType.getTypeName() == null) {
            ObjectDataModel objectArrayItemType = (ObjectDataModel) arrayItemType;
            objectArrayItemType.setTypeName(resolver.resolveArrayItemTypeName(this.getTypeName(), ""));
            result.add(objectArrayItemType); // TODO: really needed?
            List<ObjectDataModel> objectDataModels = objectArrayItemType.resolveInlineModels(resolver);
            result.addAll(objectDataModels);
        }
        return result;
    }

    @Override
    public String toString() {
        return "ArrayDataModelSchema{" +
                "itemsDataModelSchema=" + itemsDataModel.toString() +
                '}';
    }
}
