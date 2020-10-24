package apicross.core.data;

import apicross.core.data.model.DataModel;
import apicross.core.data.model.PrimitiveDataModel;
import apicross.utils.SchemaIndex;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataModelResolverTests {
    @Mock
    private SchemaIndex schemaIndex;
    private DataModelResolver dataModelResolver;

    @Before
    public void setup() {
        this.dataModelResolver = new DataModelResolver(schemaIndex, (propertySchema, apiPropertyName) -> apiPropertyName);
    }

    @Test
    public void simpleStringSchemaResolved() {
        Schema<?> schema = new StringSchema()
                .maxLength(10)
                .nullable(true);
        DataModel dataModel = dataModelResolver.resolve(schema);
        assertTrue(dataModel.isPrimitive());
        assertTrue(dataModel.isNullable());
        assertEquals("string", dataModel.getTypeName());
        assertEquals(10, ((PrimitiveDataModel) dataModel).getMaxLength().intValue());
    }

    @Test
    public void schemaWithReferenceOntoPrimitiveTypeResolved() {
        String $ref = "#/components/schemas/IntSchema";
        Schema intSchema = new IntegerSchema()
                .maximum(BigDecimal.valueOf(10));
        Schema<?> schemaWithRef = new Schema<>()
                .$ref($ref);

        Mockito.when(schemaIndex.schemaBy$ref($ref)).thenReturn(intSchema);

        DataModel dataModel = dataModelResolver.resolve(schemaWithRef);
        assertTrue(dataModel.isPrimitive());
        assertFalse(dataModel.isNullable());
        assertEquals("integer", dataModel.getTypeName());
        assertEquals(10, ((PrimitiveDataModel) dataModel).getMaximum().intValue());
    }
}
