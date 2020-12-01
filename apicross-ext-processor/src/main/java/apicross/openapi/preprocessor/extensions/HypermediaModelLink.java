package apicross.openapi.preprocessor.extensions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HypermediaModelLink {
    private String rel;

    public String getRel() {
        return rel;
    }
}
