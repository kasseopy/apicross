package apicross.openapi.preprocessor.extensions;

import apicross.openapi.preprocessor.utils.ObjectMapperInstance;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HypermediaMetadataExtension {
    private String linkModel;
    private List<HypermediaRelation> relations;
    private Map<String, HypermediaRelation> relationsMap;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public HypermediaMetadataExtension(@JsonProperty("link-model") String linkModel,
                                       @JsonProperty("relations") List<HypermediaRelation> relations) {
        this.linkModel = linkModel;
        this.relations = relations;
        this.relationsMap = relations.stream().collect(Collectors.toMap(HypermediaRelation::getRel, hypermediaRelation -> hypermediaRelation));
    }

    @Nullable
    public HypermediaRelation getRelation(String rel) {
        return this.relationsMap.get(rel);
    }

    @Nullable
    public static HypermediaMetadataExtension from(OpenAPI openAPI) {
        Map<String, Object> extension = (Map<String, Object>) openAPI.getExtensions().get("x-apicross-hypermedia");
        if (extension == null) {
            return null;
        } else {
            return ObjectMapperInstance.readExtension(extension, HypermediaMetadataExtension.class);
        }
    }
}
