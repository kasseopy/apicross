package apicross.core.data;

import apicross.core.data.model.*;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataModelResolverHandlesSimpleCasesTests extends DataModelSchemaResolverTestsBase {
    @Test
    public void primitiveTypeResolved() throws IOException {
        init("DataModelSchemaResolverTest.primitiveTypeResolved.yaml");

        Schema<?> schema = openAPIComponentsIndex.schemaByName("StringType");

        DataModel resolvedSchema = resolver.resolve(schema);

        assertTrue(resolvedSchema instanceof PrimitiveDataModel);
        assertTrue(((PrimitiveDataModel) resolvedSchema).isString());
    }

    @Test
    public void simpleObjectResolved() throws IOException {
        init("DataModelSchemaResolverTest.simpleObjectResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("SimpleObject");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);

        Set<ObjectDataModelProperty> properties = resolvedSchema.getProperties();
        assertEquals(3, properties.size());

        ObjectDataModelProperty aProperty = resolvedSchema.getProperty("a");
        assertTrue(aProperty.getType() instanceof PrimitiveDataModel);
        assertTrue(((PrimitiveDataModel) aProperty.getType()).isInteger());
        assertTrue(aProperty.isOptional());

        ObjectDataModelProperty bProperty = resolvedSchema.getProperty("b");
        assertTrue(((PrimitiveDataModel) bProperty.getType()).isString());
        assertFalse(bProperty.isOptional());

        ObjectDataModelProperty cProperty = resolvedSchema.getProperty("c");
        assertTrue(((PrimitiveDataModel) cProperty.getType()).isString());
        assertTrue(cProperty.isOptional());
    }

    @Test
    public void objectWithRefsResolved() throws IOException {
        init("DataModelSchemaResolverTest.objectWithRefsResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ObjectWithRefs");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);

        Set<ObjectDataModelProperty> properties = resolvedSchema.getProperties();
        assertEquals(2, properties.size());

        ObjectDataModelProperty aProperty = resolvedSchema.getProperty("s");
        assertTrue(((PrimitiveDataModel) aProperty.getType()).isString());
        assertTrue(aProperty.isOptional());

        ObjectDataModelProperty bProperty = resolvedSchema.getProperty("o");
        assertEquals("SimpleObject", ((ObjectDataModel) bProperty.getType()).getTypeName());
        assertFalse(bProperty.isOptional());
    }

    @Test
    public void objectWithRefOntoArrayTypeResolved() throws IOException {
        init("DataModelSchemaResolverTest.objectWithRefOntoArrayTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ObjectWithRefOntoArrayType");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);

        Set<ObjectDataModelProperty> properties = resolvedSchema.getProperties();
        assertEquals(2, properties.size());
        ObjectDataModelProperty a1Property = resolvedSchema.getProperty("a1");
        assertTrue(((PrimitiveDataModel)((ArrayDataModel) a1Property.getType()).getItemsDataModel()).isString());
        ObjectDataModelProperty a2Property = resolvedSchema.getProperty("a2");
        assertEquals("SimpleObject", ((ObjectDataModel)((ArrayDataModel) a2Property.getType()).getItemsDataModel()).getTypeName());
    }

    @Test
    public void propertyDescriptionDelegatedFormReferencedSchema() throws IOException {
        init("DataModelSchemaResolverTest.propertyDescriptionDelegatedFormReferencedSchema.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("TestSchema");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertEquals("Test1", resolvedSchema.getProperty("p1").getDescription());
        assertEquals("ZIP", resolvedSchema.getProperty("p2").getDescription());
    }
}
