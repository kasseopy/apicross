package apicross.core.handler;

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
    private HttpOperationsGroupsResolver httpOperationsGroupsResolver;
    private RequestsHandlerTypeNameResolver requestsHandlerTypeNameResolver;
    private RequestsHandlerMethodNameResolver requestsHandlerMethodNameResolver;
    private RequestsHandlerMethodsResolver requestsHandlerMethodsResolver;

    public RequestsHandlersResolver(@Nonnull HttpOperationsGroupsResolver httpOperationsGroupsResolver,
                                    @Nonnull RequestsHandlerTypeNameResolver requestsHandlerTypeNameResolver,
                                    @Nonnull RequestsHandlerMethodNameResolver requestsHandlerMethodNameResolver,
                                    @Nonnull RequestsHandlerMethodsResolver requestsHandlerMethodsResolver) {
        this.httpOperationsGroupsResolver = Objects.requireNonNull(httpOperationsGroupsResolver);
        this.requestsHandlerTypeNameResolver = Objects.requireNonNull(requestsHandlerTypeNameResolver);
        this.requestsHandlerMethodNameResolver = Objects.requireNonNull(requestsHandlerMethodNameResolver);
        this.requestsHandlerMethodsResolver = Objects.requireNonNull(requestsHandlerMethodsResolver);
    }

    public List<RequestsHandler> resolve(Paths paths) {
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

    public List<RequestsHandlerMethod> resolveMethodsOf(HttpOperationsGroup requestsHandlerOperationsGroup) {
        List<RequestsHandlerMethod> methods = new ArrayList<>();

        for (HttpOperation httpOperation : requestsHandlerOperationsGroup.operations()) {
            List<RequestsHandlerMethod> operationMethods =
                    requestsHandlerMethodsResolver.resolve(httpOperation, this.requestsHandlerMethodNameResolver);
            methods.addAll(operationMethods);
        }
        return methods;
    }
}
