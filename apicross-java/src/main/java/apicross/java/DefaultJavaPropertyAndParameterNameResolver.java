package apicross.java;

import apicross.CodeGeneratorException;
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
        String javaIdentifier = removeNonJavaSymbols(apiPropertyName);
        return javaIdentifier.contains("_") ? CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, javaIdentifier) : javaIdentifier;
    }

    private String removeNonJavaSymbols(@Nonnull String apiPropertyName) {
        StringBuilder buff = new StringBuilder();

        for (int i = 0; i < apiPropertyName.length(); i++) {
            char ch = apiPropertyName.charAt(i);
            if (CharUtils.isAsciiAlpha(ch) || ch == '_' || ch == '$' || CharUtils.isAsciiNumeric(ch)) {
                buff.append(ch);
            }
        }

        String javaIdentifier = buff.toString();

        while (CharUtils.isAsciiNumeric(javaIdentifier.charAt(0)) || javaIdentifier.charAt(0) == '_') {
            if (javaIdentifier.length() > 1) {
                javaIdentifier = javaIdentifier.substring(1);
            } else {
                throw new CodeGeneratorException("Unable to resolve java identifier from property name '" + apiPropertyName + "'");
            }
        }
        return javaIdentifier;
    }
}
