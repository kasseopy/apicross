package apicross.core.data;

import apicross.core.data.model.*;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataModelResolverHandlesAnyObjectTypesTests extends DataModelSchemaResolverTestsBase {
    @Before
    public void setup() throws IOException {
        init("DataModelSchemaResolverTest.anyObjectLikeSchemaResolved.yaml");
    }

    @Test
    public void modelResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ObjectType1");

        DataModel resolvedSchema = resolver.resolve(schema);
        assertTrue(resolvedSchema instanceof ObjectDataModel);
    }

    @Test
    public void propertyResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ObjectType2");

        DataModel resolvedSchema = resolver.resolve(schema);
        assertTrue(resolvedSchema instanceof ObjectDataModel);
        ObjectDataModelProperty property1 = ((ObjectDataModel) resolvedSchema).getProperty("prop1");
        assertNotNull(property1);
        assertTrue(property1.getType().isAnyType());
        assertEquals("Anytype", property1.getDescription());
    }

    @Test
    public void arrayItemResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ArrayType1");

        DataModel resolvedSchema = resolver.resolve(schema);
        assertTrue(resolvedSchema instanceof ArrayDataModel);
        DataModel itemsDataModel = ((ArrayDataModel) resolvedSchema).getItemsDataModel();
        assertTrue(itemsDataModel.isAnyType());
    }
}
