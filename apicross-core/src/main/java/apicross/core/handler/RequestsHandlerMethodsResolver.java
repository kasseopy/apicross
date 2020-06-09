package apicross.core.handler;

import javax.annotation.Nonnull;
import java.util.List;

public interface RequestsHandlerMethodsResolver {
    @Nonnull
    List<RequestsHandlerMethod> resolve(@Nonnull HttpOperation httpOperation, RequestsHandlerMethodNameResolver methodNameResolver);
}
