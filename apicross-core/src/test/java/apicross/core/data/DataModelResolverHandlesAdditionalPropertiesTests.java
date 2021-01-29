package apicross.core.data;

import apicross.core.data.model.ObjectDataModel;
import apicross.core.data.model.PrimitiveDataModel;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataModelResolverHandlesAdditionalPropertiesTests extends DataModelSchemaResolverTestsBase {
    @Before
    public void setUp() throws IOException {
        init("DataModelSchemaResolverHandlesAdditionalPropertiesTests.yaml");
    }

    @Test
    public void unknownAdditionalPropertiesResolvedAsAnyType() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ObjectType1");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertNotNull(resolvedSchema.getAdditionalPropertiesDataModel());
        assertTrue(resolvedSchema.getAdditionalPropertiesDataModel().isAnyType());
    }

    @Test
    public void additionalPropertiesWithRefResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ObjectType2");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertNotNull(resolvedSchema.getAdditionalPropertiesDataModel());
        assertEquals("AdditionalPropertyType", ((ObjectDataModel)resolvedSchema.getAdditionalPropertiesDataModel()).getTypeName());
    }

    @Test
    public void additionalPropertiesDeclaredAsSimpleTypeResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ObjectType3");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertNotNull(resolvedSchema.getAdditionalPropertiesDataModel());
        assertTrue(((PrimitiveDataModel)resolvedSchema.getAdditionalPropertiesDataModel()).isString());
    }
}
