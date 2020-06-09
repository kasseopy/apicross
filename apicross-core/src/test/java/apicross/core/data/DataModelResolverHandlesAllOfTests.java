package apicross.core.data;

import io.swagger.v3.oas.models.media.Schema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DataModelResolverHandlesAllOfTests extends DataModelSchemaResolverTestsBase {
    @Test
    public void allOfSchemaTypeResolved() throws IOException {
        init("DataModelSchemaResolverTest.allOfSchemaTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfSchemaType");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);

        assertEquals(4, resolvedSchema.getProperties().size());
        assertEquals("string", resolvedSchema.getProperty("a").getType().getTypeName());
        assertEquals("string", resolvedSchema.getProperty("b").getType().getTypeName());
        assertEquals("AllOfSchemaTypePart1", resolvedSchema.getProperty("d").getType().getTypeName());
    }

    @Test
    public void allOfSchemaTypeResolved_withConstraints() throws IOException {
        init("DataModelSchemaResolverTest.allOfSchemaTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfSchemaTypeOnlyConstraints");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);

        assertEquals(3, resolvedSchema.getProperties().size());
        assertTrue(resolvedSchema.getProperty("a").isRequired());
        assertTrue(resolvedSchema.getProperty("b").isOptional());
        assertTrue(resolvedSchema.getProperty("c").isRequired());
        assertEquals("Test", resolvedSchema.getDescription());
    }
}
