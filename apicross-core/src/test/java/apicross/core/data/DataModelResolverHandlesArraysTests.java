package apicross.core.data;

import apicross.core.data.model.ArrayDataModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataModelResolverHandlesArraysTests extends DataModelSchemaResolverTestsBase {
    @Test
    public void simpleArrayResolved() throws IOException {
        load("DataModelSchemaResolverTest.simpleArrayResolved.yaml");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolveModel("SimpleArray");

        assertTrue(resolvedSchema.isArray());
        assertEquals("string", resolvedSchema.getItemsDataModel().getTypeName());
    }

    @Test
    public void arrayWithRefOntoPrimitiveTypeResolved() throws IOException {
        load("DataModelSchemaResolverTest.arrayWithRefOntoPrimitiveTypeResolved.yaml");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolveModel("ArrayWithRefOntoPrimitiveType");

        assertTrue(resolvedSchema.isArray());
        assertEquals("string", resolvedSchema.getItemsDataModel().getTypeName());
    }

    @Test
    public void arrayWithRefOntoObjectTypeResolved() throws IOException {
        load("DataModelSchemaResolverTest.arrayWithRefOntoObjectTypeResolved.yaml");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolveModel("ArrayWithRefOntoObjectType");

        assertTrue(resolvedSchema.isArray());
        assertEquals("SimpleObject", resolvedSchema.getItemsDataModel().getTypeName());
    }

    @Test
    public void arrayConstraintsResolvedWhenItemIsRefObjectType() throws IOException {
        load("DataModelSchemaResolverTest.arrayConstraintsResolved.yaml");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolveModel("ArrayWithItemReferredOntoObjectType");
        assertEquals("ObjectType", resolvedSchema.getItemsDataModel().getTypeName());
        assertEquals(1, resolvedSchema.getMinItems().intValue());
        assertEquals(10, resolvedSchema.getMaxItems().intValue());
        assertTrue(resolvedSchema.isUniqueItems());
    }

    @Test
    public void arrayConstraintsResolvedWhenItemPrimitiveType() throws IOException {
        load("DataModelSchemaResolverTest.arrayConstraintsResolved.yaml");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolveModel("ArrayWithItemOfPrimitiveType");
        assertEquals("string", resolvedSchema.getItemsDataModel().getTypeName());
        assertNull(resolvedSchema.getMinItems());
        assertEquals(10, resolvedSchema.getMaxItems().intValue());
        assertTrue(resolvedSchema.isUniqueItems());
    }

    @Test
    public void arrayConstraintsResolvedWhenItemIsRefOntoPrimitiveType() throws IOException {
        load("DataModelSchemaResolverTest.arrayConstraintsResolved.yaml");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolveModel("ArrayWithItemReferredOntoPrimitiveType");
        assertEquals("string", resolvedSchema.getItemsDataModel().getTypeName());
        assertNull(resolvedSchema.getMaxItems());
        assertEquals(1, resolvedSchema.getMinItems().intValue());
        assertFalse(resolvedSchema.isUniqueItems());
    }

}
