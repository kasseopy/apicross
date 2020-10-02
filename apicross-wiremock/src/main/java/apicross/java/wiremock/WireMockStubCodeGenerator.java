package apicross.java.wiremock;

import apicross.core.data.model.ObjectDataModel;
import apicross.core.handler.model.RequestsHandler;
import apicross.java.JavaCodeGenerator;
import apicross.java.JavaCodeGeneratorOptions;
import apicross.utils.HandlebarsFactory;
import com.github.jknack.handlebars.Handlebars;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class WireMockStubCodeGenerator extends JavaCodeGenerator<JavaCodeGeneratorOptions> {
    @Override
    protected Handlebars setupHandlebars() {
        return HandlebarsFactory.setupHandlebars("/apicross/templates/wiremock");
    }

    @Override
    protected void writeHandlersSources(File handlersPackageDir, File modelsPackageDir, List<RequestsHandler> handlers) throws IOException {
        writeRequestsHandlersSourceFiles(handlers, handlersPackageDir, handler -> handler.getTypeName() + "OperationsMockConfigurer.java");
    }

    @Override
    protected void writeModelsSources(File modelsPackageDir, List<ObjectDataModel> models) throws IOException {
        writeDataModelsSourceFiles(models, modelsPackageDir, model -> model.getTypeName() + ".java");
    }
}
