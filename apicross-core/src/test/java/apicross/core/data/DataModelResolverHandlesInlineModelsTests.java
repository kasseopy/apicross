package apicross.core.data;

import apicross.core.data.model.ObjectDataModel;
import lombok.NonNull;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class DataModelResolverHandlesInlineModelsTests extends DataModelSchemaResolverTestsBase {
    @Before
    public void setup() throws IOException {
        load("DataModelSchemaResolverTest.inlineModelsResolved.yaml");
    }

    @Test
    public void resolvedInPlainStructure() {
        ObjectDataModel resolvedSchema = (ObjectDataModel) resolveModel("Model1");

        List<ObjectDataModel> resolvedInlineModels = DataModelResolver.resolveInlineModels(resolvedSchema, inlineModelTypeNameResolver());

        assertThat(resolvedInlineModels, hasSize(2));
        assertThat(resolvedInlineModels, containsModelWithTypeName("Model1_p1"));
        assertThat(resolvedInlineModels, containsModelWithTypeName("Model1_p2"));
    }

    @Test
    public void resolvedInStack() {
        ObjectDataModel resolvedSchema = (ObjectDataModel) resolveModel("Model3");

        List<ObjectDataModel> resolvedInlineModels = DataModelResolver.resolveInlineModels(resolvedSchema, inlineModelTypeNameResolver());

        assertThat(resolvedInlineModels, containsModelWithTypeName("Model3_p1"));
        assertThat(resolvedInlineModels, containsModelWithTypeName("Model3_p1_p2"));
    }

    @Test
    public void resolvedInAdditionalProperties() {
        ObjectDataModel resolvedSchema = (ObjectDataModel) resolveModel("Model4");

        List<ObjectDataModel> resolvedInlineModels = DataModelResolver.resolveInlineModels(resolvedSchema, inlineModelTypeNameResolver());

        assertThat(resolvedInlineModels, containsModelWithTypeName("Model4_additionalProperties"));
    }

    @Test
    public void resolvedInArrays() {
        ObjectDataModel resolvedSchema = (ObjectDataModel) resolveModel("Model5");

        List<ObjectDataModel> resolvedInlineModels = DataModelResolver.resolveInlineModels(resolvedSchema, inlineModelTypeNameResolver());

        assertThat(resolvedInlineModels, containsModelWithTypeName("Model5_arrayItem"));
    }

    private InlineModelTypeNameResolver inlineModelTypeNameResolver() {
        return (typeName, propertyName) -> typeName + "_" + propertyName;
    }

    private Matcher<List<ObjectDataModel>> containsModelWithTypeName(@NonNull String modelName) {
        return new CustomMatcher<List<ObjectDataModel>>("Models list contains model with type name: " + modelName) {
            @Override
            public boolean matches(Object o) {
                List<ObjectDataModel> list = (List<ObjectDataModel>) o;
                return list.stream().anyMatch(objectDataModel -> modelName.equals(objectDataModel.getTypeName()));
            }
        };
    }
}
