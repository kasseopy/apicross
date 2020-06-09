package apicross.java;

import apicross.core.data.PropertyNameResolver;
import apicross.core.handler.ParameterNameResolver;
import com.github.jknack.handlebars.internal.lang3.CharUtils;
import com.google.common.base.CaseFormat;
import io.swagger.v3.oas.models.media.Schema;

import javax.annotation.Nonnull;

public class DefaultJavaPropertyAndParameterNameResolver implements PropertyNameResolver, ParameterNameResolver {
    public DefaultJavaPropertyAndParameterNameResolver() {
    }

    @Override
    @Nonnull
    public String resolvePropertyName(@Nonnull Schema<?> propertySchema, @Nonnull String apiPropertyName) {
        return doResolve(apiPropertyName);
    }

    @Nonnull
    @Override
    public String resolveParameterName(@Nonnull Schema<?> parameterSchema, @Nonnull String apiParameterName) {
        return doResolve(apiParameterName);
    }

    private String doResolve(@Nonnull String apiPropertyName) {
        String javaIdentifier = cutOffFirstNonJavaSymbols(apiPropertyName);
        return javaIdentifier.contains("_") ? CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, javaIdentifier) : javaIdentifier;
    }

    private String cutOffFirstNonJavaSymbols(@Nonnull String apiPropertyName) {
        String javaIdentifier = apiPropertyName;
        if (!CharUtils.isAsciiAlpha(javaIdentifier.charAt(0))) {
            javaIdentifier = javaIdentifier.substring(1);
        }
        if (javaIdentifier.startsWith("_")) {
            javaIdentifier = javaIdentifier.substring(1);
        }
        return javaIdentifier;
    }
}
