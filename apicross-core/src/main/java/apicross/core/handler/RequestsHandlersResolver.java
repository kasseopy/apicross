package apicross.core.handler;

import apicross.core.handler.model.HttpOperation;
import apicross.core.handler.model.HttpOperationsGroup;
import apicross.core.handler.model.RequestsHandler;
import apicross.core.handler.model.RequestsHandlerMethod;
import io.swagger.v3.oas.models.Paths;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
public class RequestsHandlersResolver {
    private final HttpOperationsGroupsResolver httpOperationsGroupsResolver;
    private final RequestsHandlerTypeNameResolver requestsHandlerTypeNameResolver;
    private final RequestsHandlerMethodNameResolver requestsHandlerMethodNameResolver;
    private final RequestsHandlerMethodsResolver requestsHandlerMethodsResolver;
    private final ParameterNameResolver parameterNameResolver;

    public RequestsHandlersResolver(@NonNull HttpOperationsGroupsResolver httpOperationsGroupsResolver,
                                    @NonNull RequestsHandlerTypeNameResolver requestsHandlerTypeNameResolver,
                                    @NonNull RequestsHandlerMethodNameResolver requestsHandlerMethodNameResolver,
                                    @NonNull RequestsHandlerMethodsResolver requestsHandlerMethodsResolver,
                                    @NonNull ParameterNameResolver parameterNameResolver) {
        this.httpOperationsGroupsResolver = httpOperationsGroupsResolver;
        this.requestsHandlerTypeNameResolver = requestsHandlerTypeNameResolver;
        this.requestsHandlerMethodNameResolver = requestsHandlerMethodNameResolver;
        this.requestsHandlerMethodsResolver = requestsHandlerMethodsResolver;
        this.parameterNameResolver = parameterNameResolver;
    }

    @NonNull
    public List<RequestsHandler> resolve(@NonNull Paths paths) {
        Collection<HttpOperationsGroup> requestsHandlersOperationsGroups = httpOperationsGroupsResolver.resolve(paths);

        List<RequestsHandler> result = new ArrayList<>();

        for (HttpOperationsGroup requestsHandlerOperationsGroup : requestsHandlersOperationsGroups) {
            log.debug("Resolve requests handler for operations group: {}...", requestsHandlerOperationsGroup.getName());

            String handlerTypeName = requestsHandlerTypeNameResolver.resolve(requestsHandlerOperationsGroup);

            List<RequestsHandlerMethod> methods = resolveMethodsOf(requestsHandlerOperationsGroup);
            RequestsHandler handler = new RequestsHandler(handlerTypeName, methods);

            log.debug("Request handler resolved: {}", handler);

            result.add(handler);
        }

        return result;
    }

    private List<RequestsHandlerMethod> resolveMethodsOf(HttpOperationsGroup requestsHandlerOperationsGroup) {
        List<RequestsHandlerMethod> methods = new ArrayList<>();

        for (HttpOperation httpOperation : requestsHandlerOperationsGroup.operations()) {
            log.info("Resolve methods for: {}", httpOperation);
            List<RequestsHandlerMethod> operationMethods =
                    requestsHandlerMethodsResolver.resolve(httpOperation, this.requestsHandlerMethodNameResolver, this.parameterNameResolver);
            methods.addAll(operationMethods);
        }
        return methods;
    }
}
