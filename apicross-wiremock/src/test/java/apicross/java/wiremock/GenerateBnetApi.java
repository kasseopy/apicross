package apicross.java.wiremock;

import apicross.core.handler.RequestsHandlerMethodNameResolver;
import io.swagger.v3.oas.models.Operation;

import javax.annotation.Nonnull;

public class GenerateBnetApi {
    public static void main(String[] args) throws Exception {
        WireMockStubCodeGenerator codeGenerator = new WireMockStubCodeGenerator();
        WireMockStubCodeGeneratorOptions generatorOptions = new WireMockStubCodeGeneratorOptions();
        generatorOptions.setApiHandlerPackage("bnet.api.handler");
        generatorOptions.setApiModelPackage("bnet.api.model");
        generatorOptions.setWriteSourcesTo("C:/Users/Виктор/IdeaProjects/OpenAPIV3-SpringMVC-Generator/apicross-wiremock/target/generated-test-sources/java");
        generatorOptions.setRequestsHandlerMethodNameResolver(new RequestsHandlerMethodNameResolver() {
            @Nonnull
            @Override
            public String resolve(@Nonnull Operation operation, @Nonnull String uriPath, String consumesMediaType, String producesMediaType) {
                return operation.getOperationId();
            }
        });
//        generatorOptions.setDataModelsExternalTypesMap(Collections.singletonMap("JsonPatch", "Object"));
//        codeGenerator.setSpecUrl("file:///C:/Users/Виктор/IdeaProjects/OpenAPIV3-SpringMVC-Generator/apicross-springmvc/src/test/resources/api/trade-offers-publisher-api.yaml");
        codeGenerator.setSpecUrl("file:///C:/Users/Виктор/IdeaProjects/OpenAPIV3-SpringMVC-Generator/apicross-springmvc/src/test/resources/api/Test.yaml");
        codeGenerator.generate(generatorOptions);
    }
}
