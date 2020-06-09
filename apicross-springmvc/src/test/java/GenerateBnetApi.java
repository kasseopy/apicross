import apicross.core.handler.RequestsHandlerMethodNameResolver;
import apicross.java.SpringMvcCodeGenerator;
import apicross.java.SpringMvcCodeGeneratorOptions;
import io.swagger.v3.oas.models.Operation;

import javax.annotation.Nonnull;

public class GenerateBnetApi {
    public static void main(String[] args) throws Exception {
        SpringMvcCodeGenerator codeGenerator = new SpringMvcCodeGenerator();
        SpringMvcCodeGeneratorOptions generatorOptions = new SpringMvcCodeGeneratorOptions();
        generatorOptions.setEnableApicrossJavaBeanValidationSupport(true);
        generatorOptions.setApiHandlerPackage("bnet.api.handler");
        generatorOptions.setApiModelPackage("bnet.api.model");
        generatorOptions.setWriteSourcesTo("C:/Users/Виктор/IdeaProjects/OpenAPIV3-SpringMVC-Generator/apicross-springmvc/target/generated-sources/java");
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
