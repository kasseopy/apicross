package apicross.core.data;

import apicross.core.data.model.ArrayDataModel;
import apicross.core.data.model.PrimitiveDataModel;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataModelResolverHandlesArraysTests extends DataModelSchemaResolverTestsBase {
    @Test
    public void simpleArrayResolved() throws IOException {
        init("DataModelSchemaResolverTest.simpleArrayResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("SimpleArray");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolver.resolve(schema);

        assertTrue(resolvedSchema.isArray());
        assertEquals("string", resolvedSchema.getItemsDataModel().getTypeName());
    }

    @Test
    public void arrayWithRefOntoPrimitiveTypeResolved() throws IOException {
        init("DataModelSchemaResolverTest.arrayWithRefOntoPrimitiveTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ArrayWithRefOntoPrimitiveType");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolver.resolve(schema);

        assertTrue(resolvedSchema.isArray());
        assertEquals("string", resolvedSchema.getItemsDataModel().getTypeName());
    }

    @Test
    public void arrayWithRefOntoObjectTypeResolved() throws IOException {
        init("DataModelSchemaResolverTest.arrayWithRefOntoObjectTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ArrayWithRefOntoObjectType");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolver.resolve(schema);

        assertTrue(resolvedSchema.isArray());
        assertEquals("SimpleObject", resolvedSchema.getItemsDataModel().getTypeName());
    }

    @Test
    public void arrayConstraintsResolved() throws IOException {
        init("DataModelSchemaResolverTest.arrayConstraintsResolved.yaml");

        // 1)
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ArrayWithRefOntoSimpleType");
        ArrayDataModel resolvedSchema = (ArrayDataModel) resolver.resolve(schema);
        arrayConstraintsResolvedVerification(resolvedSchema);
        schema = openAPIComponentsIndex.schemaByName("ArrayWithRefOntoSimpleTypeV2");
        resolvedSchema = (ArrayDataModel) resolver.resolve(schema);
        arrayConstraintsResolvedVerification(resolvedSchema);

        // 2)
        schema = openAPIComponentsIndex.schemaByName("ArrayWithRefOntoObjectType");

        resolvedSchema = (ArrayDataModel) resolver.resolve(schema);

        assertTrue(resolvedSchema.isArray());
        assertEquals("SimpleObject", resolvedSchema.getItemsDataModel().getTypeName());

        assertEquals(1, resolvedSchema.getMinItems().intValue());
        assertEquals(10, resolvedSchema.getMaxItems().intValue());
        assertTrue(resolvedSchema.isUniqueItems());

        assertFalse(resolvedSchema.getItemsDataModel().isNullable());
    }

    private void arrayConstraintsResolvedVerification(ArrayDataModel resolvedSchema) {
        assertTrue(resolvedSchema.isArray());
        assertEquals("string", resolvedSchema.getItemsDataModel().getTypeName());

        assertEquals(1, resolvedSchema.getMinItems().intValue());
        assertEquals(10, resolvedSchema.getMaxItems().intValue());
        assertTrue(resolvedSchema.isUniqueItems());

        Integer maxLength = ((PrimitiveDataModel) resolvedSchema.getItemsDataModel()).getMaxLength();
        assertEquals(100, (int) maxLength);
        assertTrue(resolvedSchema.getItemsDataModel().isNullable());
    }
}
