package apicross.openapi.preprocessor;

import apicross.utils.PathItemsOperationsMapper;
import apicross.openapi.preprocessor.extensions.HypermediaMetadataExtension;
import apicross.openapi.preprocessor.extensions.HypermediaModelExtension;
import apicross.openapi.preprocessor.extensions.HypermediaModelLink;
import apicross.openapi.preprocessor.extensions.HypermediaRelation;
import apicross.openapi.preprocessor.utils.ObjectMapperInstance;
import apicross.utils.OpenApiSpecificationParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OpenAPIHypermediaExtensionsHandler {
    private final OpenAPI openAPI;
    private final Map<String, OperationWithPath> operationsIndex;
    private final HypermediaMetadataExtension hypermediaMetadata;

    public OpenAPIHypermediaExtensionsHandler(OpenAPI openAPI) {
        this.openAPI = openAPI;
        this.operationsIndex = indexOperations(openAPI.getPaths());
        this.hypermediaMetadata = HypermediaMetadataExtension.from(openAPI);
    }

    public void extend() {
        Map<String, Schema> schemas = openAPI.getComponents().getSchemas();

        for (Schema extendingSchema : schemas.values()) {
            if (!(extendingSchema instanceof ObjectSchema)) {
                continue;
            }

            HypermediaModelExtension hypermediaModel = HypermediaModelExtension.from(extendingSchema);
            if (hypermediaModel != null) {
                extendSchema(extendingSchema, hypermediaModel);
            }
        }
    }

    private Map<String, OperationWithPath> indexOperations(Paths paths) {
        Map<String, OperationWithPath> index = new HashMap<>();
        for (String path : paths.keySet()) {
            PathItem pathItem = paths.get(path);
            Collection<Operation> operations = PathItemsOperationsMapper.mapOperationsByHttpMethod(pathItem).values();
            for (Operation operation : operations) {
                index.put(operation.getOperationId(), new OperationWithPath(path, operation));
            }
        }
        return index;
    }

    private void extendSchema(Schema extendingSchema,
                              HypermediaModelExtension hypermediaModel) {
        Collection<HypermediaModelLink> links = hypermediaModel.getLinks();
        if (links != null) {
            ObjectSchema linksSchema = new ObjectSchema();
            Map<String, Map<String, Object>> linksExample = new HashMap<>();
            for (HypermediaModelLink link : links) {
                Schema linkSchema = new Schema();
                HypermediaRelation hypermediaMetadataRelation = hypermediaMetadata.getRelation(link.getRel());
                if (hypermediaMetadataRelation != null) {
                    linksExample.put(link.getRel(), buildLinkExample(hypermediaMetadataRelation));
                }
                linksSchema.addProperties(link.getRel(), linkSchema.$ref(hypermediaMetadata.getLinkModel()));
            }
            linksSchema.setExample(linksExample);
            extendingSchema.addProperties("_links", linksSchema);
        }

        String type = hypermediaModel.getType();
        if ("collection".equals(type) || "paginated-collection".equals(type)) {
            String collectionItemRef = Objects.requireNonNull(hypermediaModel.getCollectionItemRef());
            String collectionItemsPropertyName = Objects.requireNonNull(hypermediaModel.getCollectionItemsPropertyName());
            Schema embedded = new ObjectSchema();
            embedded.addProperties(collectionItemsPropertyName, new Schema().$ref(collectionItemRef));
            embedded.addRequiredItem(collectionItemsPropertyName);
            extendingSchema.addProperties("_embedded", embedded);
            extendingSchema.addRequiredItem("_embedded");
            if ("paginated-collection".equals(type)) {
                String pageModelRef = hypermediaModel.getPageModelRef();
                extendingSchema.addProperties("page", new Schema().$ref(pageModelRef));
                extendingSchema.addRequiredItem("page");
            }
        }
    }

    private Map<String, Object> buildLinkExample(HypermediaRelation relation) {
        return new HashMap<String, Object>() {{
            put("title", relation.getTitle());
            put("href", linkHrefExample(relation));
        }};
    }

    private String linkHrefExample(HypermediaRelation relation) {
        String apiHost = openAPI.getServers().get(0).getUrl();
//        Set<String> apiOperations = relation.getOpenApiOperations();
//        return apiOperations != null && !apiOperations.isEmpty() ?
//                apiHost + operationsIndex.get(apiOperations.iterator().next()).getPath() :
//                apiHost + "/" + RandomStringUtils.randomAlphanumeric(40);
        return apiHost + "/" + RandomStringUtils.randomAlphanumeric(40);
    }

    public static void main(String[] args) throws IOException {
        OpenAPI openAPI = OpenApiSpecificationParser.parse("C:\\Users\\Виктор\\IdeaProjects\\apicross\\apicross-demoapp\\api-specifications\\eshop-catalog-hypermedia.yaml");
        OpenAPIHypermediaExtensionsHandler handler = new OpenAPIHypermediaExtensionsHandler(openAPI);
        handler.extend();
        String json = ObjectMapperInstance.toJsonString(openAPI);
        FileUtils.writeStringToFile(new File("C:\\Users\\Виктор\\IdeaProjects\\apicross\\apicross-demoapp\\api-specifications\\eshop-catalog-hypermedia.json"), json, StandardCharsets.UTF_8);
    }

    @AllArgsConstructor
    @Getter
    private static class OperationWithPath {
        private String path;
        private Operation operation;
    }
}
