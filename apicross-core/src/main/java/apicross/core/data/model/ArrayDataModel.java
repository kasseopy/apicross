package apicross.core.data.model;

import io.swagger.v3.oas.models.media.ArraySchema;
import org.apache.commons.lang3.BooleanUtils;

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

    public Integer getMinItems() {
        return getSource().getMinItems();
    }

    public Integer getMaxItems() {
        return getSource().getMaxItems();
    }

    public DataModel getItemsDataModel() {
        return itemsDataModel;
    }

    public String getArrayItemTypeName() {
        return getItemsDataModel().getTypeName();
    }

    @Override
    public String toString() {
        return "ArrayDataModelSchema{" +
                "itemsDataModelSchema=" + itemsDataModel.toString() +
                '}';
    }
}
