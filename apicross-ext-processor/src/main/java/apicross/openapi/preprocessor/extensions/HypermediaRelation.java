package apicross.openapi.preprocessor.extensions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
public class HypermediaRelation {
    @JsonProperty("rel")
    private String rel;
    @JsonProperty("title")
    private String title;
    @JsonProperty("operation-ids")
    private Set<String> openApiOperations;
}
