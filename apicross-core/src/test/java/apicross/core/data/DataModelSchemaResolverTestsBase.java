package apicross.core.data;

import apicross.core.data.model.DataModel;
import apicross.utils.OpenApiComponentsIndex;
import apicross.utils.OpenApiSpecificationParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import java.io.IOException;

public abstract class DataModelSchemaResolverTestsBase {
    protected OpenApiComponentsIndex openAPIComponentsIndex;
    protected DataModelResolver resolver;

    protected void load(String apiSpecificationResource) throws IOException {
        OpenAPI openAPI = OpenApiSpecificationParser.parse(getClass()
                .getResourceAsStream(apiSpecificationResource));
        openAPIComponentsIndex = new OpenApiComponentsIndex(openAPI);
        resolver = new DataModelResolver(openAPIComponentsIndex, (propertySchema, apiPropertyName) -> apiPropertyName);
    }

    protected DataModel resolveModel(String modelName) {
        Schema<?> schema = this.openAPIComponentsIndex.schemaByName(modelName);
        return this.resolver.resolve(schema);
    }
}
