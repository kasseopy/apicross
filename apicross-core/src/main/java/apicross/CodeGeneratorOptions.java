package apicross;

import apicross.core.data.PropertyNameResolver;
import apicross.core.handler.ParameterNameResolver;
import apicross.core.handler.RequestsHandlerMethodNameResolver;
import apicross.core.handler.RequestsHandlerTypeNameResolver;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;

@Getter
@Setter
public class CodeGeneratorOptions {
    private String writeSourcesTo;
    private boolean generateOnlyModels = false;
    private Set<String> skipTags = Collections.emptySet();
    private Set<String> generateOnlyTags = Collections.emptySet();
    private String requestsHandlerMethodNameResolverClassName;
    private String requestsHandlerTypeNameResolverClassName;
    private String propertyNameResolverClassName;
    private String parameterNameResolverClassName;
    private RequestsHandlerMethodNameResolver requestsHandlerMethodNameResolver;
    private RequestsHandlerTypeNameResolver requestsHandlerTypeNameResolver;
    private ParameterNameResolver parameterNameResolver;
    private PropertyNameResolver propertyNameResolver;
}
