package apicross.utils;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SchemaIndex {
    @Nonnull
    Schema<?> schemaBy$ref(String $ref);

    @Nullable
    RequestBody requestBodyBy$ref(String $ref);
}
