package apicross.java;

import apicross.CodeGenerator;
import apicross.CodeGeneratorException;
import apicross.core.data.DataModelResolver;
import apicross.core.data.model.ArrayDataModel;
import apicross.core.data.model.ObjectDataModel;
import apicross.core.data.PropertyNameResolver;
import apicross.core.handler.*;
import apicross.core.handler.model.RequestsHandler;
import apicross.core.handler.model.RequestsHandlerMethod;
import apicross.utils.PluginsHelper;
import apicross.utils.SourceCodeLineNumberUtil;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Slf4j
public abstract class JavaCodeGenerator<T extends JavaCodeGeneratorOptions> extends CodeGenerator<T> {
    protected final Formatter formatter = new Formatter();
    protected Template requestsHandlerSourceCodeTemplate;
    protected Template dataModelSourceCodeTemplate;
    protected String apiModelPackage;
    protected String apiHandlerPackage;
    protected Map<String, String> dataModelsInterfacesMap;
    protected Map<String, String> dataModelsExternalTypesMap;
    protected Map<String, String> queryObjectsInterfacesMap;
    protected Set<String> globalQueryObjectsInterfaces;

    @Override
    public void setOptions(T options) throws Exception {
        super.setOptions(options);
        this.apiHandlerPackage = options.getApiHandlerPackage();
        this.apiModelPackage = options.getApiModelPackage();
        this.dataModelsInterfacesMap = Collections.unmodifiableMap(options.getDataModelsInterfacesMap());
        this.dataModelsExternalTypesMap = Collections.unmodifiableMap(options.getDataModelsExternalTypesMap());
        this.queryObjectsInterfacesMap = Collections.unmodifiableMap(options.getQueryObjectsInterfacesMap());
        this.globalQueryObjectsInterfaces = Collections.unmodifiableSet(options.getGlobalQueryObjectsInterfaces());
    }

    @Override
    protected void generate(Collection<ObjectDataModel> models, List<RequestsHandler> handlers) throws IOException {
        List<ObjectDataModel> modelsJavaClasses = prepareDataModelJavaClasses(models, handlers);

        if (!getOptions().isGenerateOnlyModels()) {
            if (!this.apiHandlerPackage.equals(this.apiModelPackage)) {
                for (RequestsHandler handler : handlers) {
                    handler.addCustomAttribute("imports", Collections.singleton(this.apiModelPackage + ".*"));
                }
            }

            if (this.dataModelsExternalTypesMap != null) {
                for (RequestsHandler handler : handlers) {
                    handler.replaceHandlerParametersByExternalTypesMap(this.dataModelsExternalTypesMap);
                }
            }
        }

        log.info("Setup source code templates...");
        Handlebars handlebars = setupHandlebars();

        initHandlebarTemplates(handlebars);

        log.info("Writing sources...");
        File writeSourcesTo = new File(getOptions().getWriteSourcesTo());
        File modelsPackageDir = new File(writeSourcesTo, toFilePath(this.apiModelPackage));

        if (!modelsPackageDir.exists()) {
            modelsPackageDir.mkdirs();
            log.info("Directory {} created", modelsPackageDir.getAbsolutePath());
        }

        writeModelsSources(modelsPackageDir, modelsJavaClasses);

        if (!getOptions().isGenerateOnlyModels()) {
            File handlersPackageDir = new File(writeSourcesTo, toFilePath(this.apiHandlerPackage));
            if (!handlersPackageDir.exists()) {
                handlersPackageDir.mkdirs();
                log.info("Directory {} created", handlersPackageDir.getAbsolutePath());
            }
            writeHandlersSources(handlersPackageDir, modelsPackageDir, handlers);
        }
    }

    protected List<ObjectDataModel> prepareDataModelJavaClasses(Collection<ObjectDataModel> schemas, Collection<RequestsHandler> handlers) {
        log.info("Prepare data models java classes...");

        List<ObjectDataModel> result = new ArrayList<>();

        resolveInlineModelsFromSchemas(result, schemas);
        resolveInlineModelsFromRequestsHandler(result, handlers);

        if (this.dataModelsExternalTypesMap != null) {
            log.info("Process data models external types map...");
            processExternalTypesForDataModels(result, this.dataModelsExternalTypesMap);
        }

        if (this.dataModelsInterfacesMap != null) {
            log.info("Process data models interfaces...");
            processInterfacesForDataModels(result, this.dataModelsInterfacesMap);
        }

        String modelNameSuffix = getOptions().getModelClassNameSuffix();
        if (modelNameSuffix != null && !modelNameSuffix.isEmpty()) {
            log.info("Process data models name suffix...");
            addModelNameSuffix(modelNameSuffix, this.dataModelsExternalTypesMap, result);
        }
        String modelNamePrefix = getOptions().getModelClassNamePrefix();
        if (modelNamePrefix != null && !modelNamePrefix.isEmpty()) {
            log.info("Process data models name prefix...");
            addModelNamePrefix(modelNamePrefix, this.dataModelsExternalTypesMap, result);
        }

        log.info("Preparing data models java classes completed!");

        return result;
    }

    private static void resolveInlineModelsFromRequestsHandler(List<ObjectDataModel> collectTo, Collection<RequestsHandler> handlers) {
        for (RequestsHandler handler : handlers) {
            for (RequestsHandlerMethod method : handler.getMethods()) {
                if (method.getRequestBody() != null && method.getRequestBody().getContent().isArray()) {
                    ArrayDataModel requestBodyModel = (ArrayDataModel) method.getRequestBody().getContent();
                    List<ObjectDataModel> objectDataModels = DataModelResolver.resolveInlineModels(requestBodyModel, (typeName, propertyResolvedName) -> typeName + StringUtils.capitalize(propertyResolvedName));
                    collectTo.addAll(objectDataModels);
                }
            }
        }
    }

    private static void resolveInlineModelsFromSchemas(List<ObjectDataModel> collectTo, Collection<ObjectDataModel> schemas) {
        for (ObjectDataModel model : schemas) {
            collectTo.add(model);
            List<ObjectDataModel> inlineModels =
                    DataModelResolver.resolveInlineModels(model, (typeName, propertyName) -> typeName + StringUtils.capitalize(propertyName));
            if (!inlineModels.isEmpty()) {
                collectTo.addAll(inlineModels);
                resolveInlineModelsFromSchemas(collectTo, inlineModels);
            }
        }
    }

    protected void processExternalTypesForDataModels(List<ObjectDataModel> models, Map<String, String> dataModelsExternalTypesMap) {
        Iterator<ObjectDataModel> dataModelIterator = models.iterator();
        while (dataModelIterator.hasNext()) {
            ObjectDataModel model = dataModelIterator.next();
            String modelClassName = model.getTypeName();
            if (dataModelsExternalTypesMap.containsKey(modelClassName)) {
                log.debug("Model '{}' replaced by external type '{}'", modelClassName, dataModelsExternalTypesMap.get(modelClassName));
                dataModelIterator.remove();
            }
        }

        dataModelIterator = models.iterator();
        while (dataModelIterator.hasNext()) {
            ObjectDataModel model = dataModelIterator.next();
            model.replacePropertyTypeByExternalTypesMap(dataModelsExternalTypesMap);
        }
    }

    protected void processInterfacesForDataModels(List<ObjectDataModel> models, Map<String, String> dataModelsInterfacesMap) {
        for (ObjectDataModel model : models) {
            String iface = dataModelsInterfacesMap.get(model.getTypeName());
            if (iface != null) {
                model.addCustomAttribute("implementsInterfaces", Collections.singleton(iface));
            }
        }
    }

    private void addModelNameSuffix(String modelNameSuffix, Map<String, String> dataModelsExternalTypesMap, Collection<ObjectDataModel> models) {
        for (ObjectDataModel model : models) {
            if (!dataModelsExternalTypesMap.containsKey(model.getTypeName())) {
                model.changeTypeName(model.getTypeName() + modelNameSuffix, false);
            }
        }
    }

    private void addModelNamePrefix(String modelNamePrefix, Map<String, String> dataModelsExternalTypesMap, Collection<ObjectDataModel> models) {
        for (ObjectDataModel model : models) {
            if (!dataModelsExternalTypesMap.containsKey(model.getTypeName())) {
                model.changeTypeName(modelNamePrefix + model.getTypeName(), false);
            }
        }
    }

    @Override
    protected PropertyNameResolver setupPropertyNameResolver() {
        return PluginsHelper.instantiatePlugin(getOptions().getPropertyNameResolverClassName(),
                DefaultJavaPropertyAndParameterNameResolver::new);
    }

    @Override
    protected RequestsHandlerMethodNameResolver setupRequestsHandlerMethodNameResolver() {
        return PluginsHelper.instantiatePlugin(getOptions().getRequestsHandlerMethodNameResolverClassName(),
                DefaultJavaRequestsHandlerMethodNameResolver::new);
    }

    @Override
    protected RequestsHandlerTypeNameResolver setupRequestsHandlerTypeNameResolver() {
        return PluginsHelper.instantiatePlugin(getOptions().getRequestsHandlerTypeNameResolverClassName(),
                DefaultJavaRequestsHandlerTypeNameResolver::new);
    }

    @Override
    protected ParameterNameResolver setupParameterNameResolver() {
        return PluginsHelper.instantiatePlugin(getOptions().getParameterNameResolverClassName(),
                DefaultJavaPropertyAndParameterNameResolver::new);
    }

    protected abstract Handlebars setupHandlebars();

    protected void initHandlebarTemplates(Handlebars templatesEngine) throws IOException {
        this.requestsHandlerSourceCodeTemplate = templatesEngine.compile("requestsHandler");
        this.dataModelSourceCodeTemplate = templatesEngine.compile("dataModel");
    }

    protected abstract void writeModelsSources(File modelsPackageDir, List<ObjectDataModel> models) throws IOException;

    protected abstract void writeHandlersSources(File handlersPackageDir, File modelsPackageDir, List<RequestsHandler> handlers) throws IOException;

    protected void writeRequestsHandler(RequestsHandler requestsHandler, PrintWriter out) throws IOException {
        Context context = buildTemplateContext(requestsHandler, this.apiHandlerPackage);
        writeSource(context, this.requestsHandlerSourceCodeTemplate, out);
    }

    protected void writeDataModel(ObjectDataModel model, PrintWriter out) throws IOException {
        Context context = buildTemplateContext(model, this.apiModelPackage);
        writeSource(context, this.dataModelSourceCodeTemplate, out);
    }

    protected Context buildTemplateContext(Object model, String packageName) {
        return Context
                .newBuilder(model)
                .combine("package", packageName)
                .build();
    }

    protected void writeSource(Context model, Template template, PrintWriter out) throws IOException {
        String source = template.apply(model);
        String formattedSource;
        try {
            formattedSource = this.formatter.formatSource(source);
        } catch (FormatterException e) {
            log.error("Unable to format source:\n-------------------\n{}\n--------------------\n",
                    SourceCodeLineNumberUtil.addLineNumbers(source));
            throw new CodeGeneratorException(e);
        }
        out.println(formattedSource);
        out.flush();
    }

    protected String toFilePath(String packagePath) {
        return packagePath.replaceAll("\\.", "//");
    }

    protected void writeRequestsHandlersSourceFiles(List<RequestsHandler> handlers, File handlersPackageDir, Function<RequestsHandler, String> fileNameFactory) throws IOException {
        log.info("Writing API handlers...");

        for (RequestsHandler handler : handlers) {
            File handlerInterfaceSourceFile = new File(handlersPackageDir, fileNameFactory.apply(handler));
            try (FileOutputStream out = new FileOutputStream(handlerInterfaceSourceFile)) {
                PrintWriter sourcePrintWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                writeRequestsHandler(handler, sourcePrintWriter);
            }
        }
    }

    protected void writeDataModelsSourceFiles(List<ObjectDataModel> models, File modelsPackageDir, Function<ObjectDataModel, String> fileNameFactory) throws IOException {
        log.info("Writing API data models...");

        for (ObjectDataModel model : models) {
            File sourceFile = new File(modelsPackageDir, fileNameFactory.apply(model));
            try (FileOutputStream out = new FileOutputStream(sourceFile)) {
                PrintWriter sourcePrintWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                writeDataModel(model, sourcePrintWriter);
            }
        }
    }
}
