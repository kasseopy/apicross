package apicross.openapi.preprocessor.extensions;

import apicross.openapi.preprocessor.utils.ObjectMapperInstance;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HypermediaModelExtension {
    @JsonProperty("links")
    private List<HypermediaModelLink> links;
    @JsonProperty("type")
    private String type;
    @JsonProperty("collection-item")
    private String collectionItemRef;
    @JsonProperty("collection-items-property")
    private String collectionItemsPropertyName;
    @JsonProperty("page-model")
    private String pageModelRef;

    @Nullable
    public static HypermediaModelExtension from(Schema schema) {
        Map extensions = schema.getExtensions();
        if (extensions == null) {
            return null;
        }
        Map<String, Object> o = (Map<String, Object>) extensions.get("x-apicross-hypermedia-model");
        if (o == null) {
            return null;
        } else {
            return ObjectMapperInstance.readExtension(o, HypermediaModelExtension.class);
        }
    }
}
