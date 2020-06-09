package apicross.core.handler;

import com.google.common.base.Preconditions;
import io.swagger.v3.oas.models.Operation;
import lombok.NonNull;

import java.util.Objects;

public class HttpOperation {
    private String uriPath;
    private String httpMethod;
    private Operation operation;

    public HttpOperation(@NonNull String uriPath, @NonNull String httpMethod, @NonNull Operation operation) {
        Preconditions.checkArgument(operation.getOperationId() != null, "'operation.operationId' must not be null");
        this.uriPath = uriPath;
        this.httpMethod = httpMethod;
        this.operation = operation;
    }

    public String getUriPath() {
        return uriPath;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpOperation that = (HttpOperation) o;
        return uriPath.equals(that.uriPath) &&
                httpMethod.equals(that.httpMethod) &&
                operation.getOperationId().equals(that.operation.getOperationId()); // operationId must be unique within API spec!
    }

    @Override
    public int hashCode() {
        return Objects.hash(uriPath, httpMethod, operation.getOperationId());
    }

    @Override
    public String toString() {
        return "HttpOperation{" +
                "httpMethod='" + httpMethod + '\'' +
                ", uriPath='" + uriPath + '\'' +
                ", operation=" + operation.getOperationId() +
                '}';
    }
}
