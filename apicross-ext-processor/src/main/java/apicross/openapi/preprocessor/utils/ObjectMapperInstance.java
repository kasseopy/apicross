package apicross.openapi.preprocessor.utils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;

public class ObjectMapperInstance {
    private static final ObjectMapper INSTANCE = new ObjectMapper();

    static {
        INSTANCE.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        INSTANCE.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        INSTANCE.addMixIn(Schema.class, OasSchemaMixIn.class);
        INSTANCE.addMixIn(OpenAPI.class, OasExtensionsMixIn.class);
    }

    public static <T> T readExtension(Map<String, Object> extension, Class<T> modelClass) {
        return INSTANCE.convertValue(extension, modelClass);
    }

    public static String toJsonString(OpenAPI openAPI) throws JsonProcessingException {
        return INSTANCE.writeValueAsString(openAPI);
    }

    public static abstract class OasExtensionsMixIn {
        @JsonAnyGetter
        public abstract Map<String, Object> getExtensions();
    }

    public static abstract class OasSchemaMixIn extends OasExtensionsMixIn {
        @JsonIgnore
        public abstract boolean isExampleSetFlag();

        @JsonIgnore
        public abstract boolean getExampleSetFlag();
    }
}
