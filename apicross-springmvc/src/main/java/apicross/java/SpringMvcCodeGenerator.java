package apicross.java;

import apicross.core.data.model.ObjectDataModel;
import apicross.core.handler.model.MediaTypeContentModel;
import apicross.core.handler.model.RequestQueryParameter;
import apicross.core.handler.model.RequestsHandler;
import apicross.core.handler.model.RequestsHandlerMethod;
import apicross.utils.HandlebarsFactory;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class SpringMvcCodeGenerator extends JavaCodeGenerator<SpringMvcCodeGeneratorOptions> {
    private Template requestsHandlerQueryObjectTemplate;
    private Template dataModelReadInterfaceTemplate;
    private boolean enableApicrossJavaBeanValidationSupport = false;
    private boolean enableDataModelReadInterfaces = false;
    private String apiModelReadInterfacesPackage;

    @Override
    protected void initHandlebarTemplates(Handlebars templatesEngine) throws IOException {
        super.initHandlebarTemplates(templatesEngine);
        this.requestsHandlerQueryObjectTemplate = templatesEngine.compile("requestsHandlerQueryStringParametersObject");
        this.dataModelReadInterfaceTemplate = templatesEngine.compile("dataModelReadInterface");
        templatesEngine.setInfiniteLoops(true); // for recursion with type.hbs
    }

    @Override
    public void setOptions(SpringMvcCodeGeneratorOptions options) throws Exception {
        super.setOptions(options);
        this.enableApicrossJavaBeanValidationSupport = options.isEnableApicrossJavaBeanValidationSupport();
        this.enableDataModelReadInterfaces = options.isEnableDataModelReadInterfaces();
        this.apiModelReadInterfacesPackage = options.getApiModelReadInterfacesPackage();
        if (this.apiModelReadInterfacesPackage == null) {
            this.apiModelReadInterfacesPackage = super.apiModelPackage;
        }
    }

    @Override
    protected void preProcess(Collection<ObjectDataModel> models, List<RequestsHandler> handlers) {
        super.preProcess(models, handlers);
        if (!getOptions().isGenerateOnlyModels()) {
            handleNeedToUseRequestBodyAnnotation(handlers);
        }
    }

    @Override
    protected List<ObjectDataModel> prepareDataModelJavaClasses(Collection<ObjectDataModel> schemas, List<RequestsHandler> handlers) {
        final List<ObjectDataModel> objectDataModels = super.prepareDataModelJavaClasses(schemas, handlers);
        if (enableDataModelReadInterfaces) {
            for (ObjectDataModel model : objectDataModels) {
                Map<String, Object> customAttributes = model.getCustomAttributes();
                List<String> ifaces = (List<String>) customAttributes.get("implementsInterfaces");

                if (ifaces == null) {
                    ifaces = new ArrayList<>();
                    model.addCustomAttribute("implementsInterfaces", ifaces);
                }

                String iface = "IRead" + model.getTypeName();
                ifaces.add(iface);

                if (!apiModelReadInterfacesPackage.equals(super.apiModelPackage)) {
                    model.addCustomAttribute("apiModelReadInterfacesPackage", apiModelReadInterfacesPackage);
                }
            }
        }
        return objectDataModels;
    }

    protected void handleNeedToUseRequestBodyAnnotation(List<RequestsHandler> handlers) {
        handlers.forEach(requestsHandler -> requestsHandler.getMethods().forEach(requestsHandlerMethod -> {
            MediaTypeContentModel requestBody = requestsHandlerMethod.getRequestBody();
            if (requestBody != null) {
                if ("multipart/form-data".equals(requestBody.getMediaType()) ||
                        "application/x-www-form-urlencoded".equals(requestBody.getMediaType())) {
                    requestBody.addCustomAttribute("avoidRequestBodyAnnotation", Boolean.TRUE);
                }
            }
        }));
    }

    @Override
    protected Handlebars setupHandlebars() {
        return HandlebarsFactory.setupHandlebars("/apicross/templates/springmvc");
    }

    @Override
    protected void writeHandlersSources(File handlersPackageDir, File modelsPackageDir, List<RequestsHandler> handlers) throws IOException {
        writeRequestsHandlersSourceFiles(handlers, handlersPackageDir, handler -> handler.getTypeName() + ".java");
        writeApiHandlerQueryObjectModels(handlers, modelsPackageDir);
    }

    @Override
    protected void writeModelsSources(File modelsPackageDir, List<ObjectDataModel> models) throws IOException {
        if (enableDataModelReadInterfaces) {
            File apiModelReadInterfacesDirectory = new File(getWriteSourcesTo(), toFilePath(apiModelReadInterfacesPackage));
            apiModelReadInterfacesDirectory.mkdirs();
            writeDataModelsReadInterfaceSourceFiles(models, apiModelReadInterfacesDirectory, model -> "IRead" + model.getTypeName() + ".java");
        }
        writeDataModelsSourceFiles(models, modelsPackageDir, model -> model.getTypeName() + ".java");
    }

    protected void writeApiHandlerQueryObjectModels(List<RequestsHandler> handlers, File modelsPackageDir) throws IOException {
        log.info("Writing QueryObject data models...");

        Set<String> handledOperations = new LinkedHashSet<>(); // 1 query object for operationId
        for (RequestsHandler handler : handlers) {
            List<RequestsHandlerMethod> methods = handler.getMethods();
            for (RequestsHandlerMethod method : methods) {
                String operationId = method.getOperationId();
                if (method.hasQueryParameters() && !handledOperations.contains(operationId)) {
                    File sourceFile = new File(modelsPackageDir, StringUtils.capitalize(operationId) + "Query.java");
                    try (FileOutputStream out = new FileOutputStream(sourceFile)) {
                        PrintWriter sourcePrintWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                        String iface = queryObjectsInterfacesMap != null ? queryObjectsInterfacesMap.get(operationId) : null;
                        Set<String> ifaces = new LinkedHashSet<>();
                        if (iface != null) {
                            ifaces.add(iface);
                        }
                        if (globalQueryObjectsInterfaces != null && !globalQueryObjectsInterfaces.isEmpty()) {
                            ifaces.addAll(globalQueryObjectsInterfaces);
                        }
                        Context context = buildTemplateContext(method, apiModelPackage);
                        if (!ifaces.isEmpty()) {
                            context.combine("queryObjectTypeInterfaces", ifaces);
                        }
                        context.combine("queryObjectRequiredProperties", method.getQueryParameters().stream()
                                .filter(RequestQueryParameter::isRequired)
                                .map(RequestQueryParameter::getName)
                                .collect(Collectors.toSet()));
                        writeSource(context, requestsHandlerQueryObjectTemplate, sourcePrintWriter);
                    }
                    handledOperations.add(operationId);
                }
            }
        }
    }

    protected void writeDataModelsReadInterfaceSourceFiles(List<ObjectDataModel> models, File modelsPackageDir, Function<ObjectDataModel, String> fileNameFactory) throws IOException {
        log.info("Writing API data models read interfaces...");

        for (ObjectDataModel model : models) {
            File sourceFile = new File(modelsPackageDir, fileNameFactory.apply(model));
            try (FileOutputStream out = new FileOutputStream(sourceFile)) {
                PrintWriter sourcePrintWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                Context context = buildTemplateContext(model, apiModelReadInterfacesPackage);
                writeSource(context, dataModelReadInterfaceTemplate, sourcePrintWriter);
            }
        }
    }

    @Override
    protected Context buildTemplateContext(Object model, String packageName) {
        Context context = super.buildTemplateContext(model, packageName);
        return context.combine("extraOpts",
                Collections.singletonMap("enableApicrossJavaBeanValidationSupport", this.enableApicrossJavaBeanValidationSupport));
    }
}
