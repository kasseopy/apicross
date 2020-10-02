package apicross.core.handler;

import apicross.core.handler.model.HttpOperation;
import apicross.core.handler.model.RequestsHandlerMethod;

import javax.annotation.Nonnull;
import java.util.List;

public interface RequestsHandlerMethodsResolver {
    @Nonnull
    List<RequestsHandlerMethod> resolve(@Nonnull HttpOperation httpOperation, RequestsHandlerMethodNameResolver methodNameResolver);
}
