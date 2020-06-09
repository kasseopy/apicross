package apicross.core.data;

import apicross.java.DefaultJavaPropertyAndParameterNameResolver;
import apicross.utils.OpenApiComponentsIndex;
import apicross.utils.OpenApiSpecificationParser;
import io.swagger.v3.oas.models.OpenAPI;

import java.io.IOException;

public abstract class DataModelSchemaResolverTestsBase {
    protected OpenApiComponentsIndex openAPIComponentsIndex;
    protected DataModelResolver resolver;

    protected void init(String apiSpecificationResource) throws IOException {
        OpenAPI openAPI = OpenApiSpecificationParser.parse(getClass()
                .getResourceAsStream(apiSpecificationResource));
        openAPIComponentsIndex = new OpenApiComponentsIndex(openAPI);
        resolver = new DataModelResolver(openAPIComponentsIndex, new DefaultJavaPropertyAndParameterNameResolver());
    }
}
