package apicross.core.handler;

import apicross.core.handler.model.HttpOperationsGroup;

public interface RequestsHandlerTypeNameResolver {
    String resolve(HttpOperationsGroup requestsHandlerOperations);
}
