package apicross.core.handler;

import apicross.core.HasCustomModelAttributes;
import io.swagger.v3.oas.models.Operation;
import apicross.core.NamedDatum;
import apicross.core.data.DataModel;
import apicross.core.data.ObjectDataModel;

import java.util.Map;
import java.util.Set;

public class RequestsHandlerMethod extends HasCustomModelAttributes {
    private String httpMethod;
    private MediaTypeContentModel responseBody;
    private MediaTypeContentModel requestBody;
    private Set<RequestQueryParameter> queryParameters;
    private String uriPath;
    private String methodName;
    private String documentation;
    private String requestDocumentation;
    private String responseDocumentation;
    private Set<RequestUriPathParameter> pathParameters;
    private Operation operation;
    private boolean requestBodyRequired;

    public String getHttpMethod() {
        return httpMethod;
    }

    public RequestsHandlerMethod setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public MediaTypeContentModel getResponseBody() {
        return responseBody;
    }

    public RequestsHandlerMethod setResponseBody(MediaTypeContentModel responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public DataModel getResponseBodyContent() {
        return responseBody != null ? responseBody.getContent() : null;
    }

    public MediaTypeContentModel getRequestBody() {
        return requestBody;
    }

    public RequestsHandlerMethod setRequestBody(MediaTypeContentModel requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public DataModel getRequestBodyContent() {
        return requestBody != null ? requestBody.getContent() : null;
    }

    public Set<RequestQueryParameter> getQueryParameters() {
        return queryParameters;
    }

    public RequestsHandlerMethod setQueryParameters(Set<RequestQueryParameter> queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    public String getUriPath() {
        return uriPath;
    }

    public RequestsHandlerMethod setUriPath(String uriPath) {
        this.uriPath = uriPath;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public RequestsHandlerMethod setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getDocumentation() {
        return documentation;
    }

    public RequestsHandlerMethod setDocumentation(String documentation) {
        this.documentation = documentation;
        return this;
    }

    public String getRequestDocumentation() {
        return requestDocumentation;
    }

    public RequestsHandlerMethod setRequestDocumentation(String requestDocumentation) {
        this.requestDocumentation = requestDocumentation;
        return this;
    }

    public String getResponseDocumentation() {
        return responseDocumentation;
    }

    public RequestsHandlerMethod setResponseDocumentation(String responseDocumentation) {
        this.responseDocumentation = responseDocumentation;
        return this;
    }

    public Set<RequestUriPathParameter> getPathParameters() {
        return pathParameters;
    }

    public RequestsHandlerMethod setPathParameters(Set<RequestUriPathParameter> pathParameters) {
        this.pathParameters = pathParameters;
        return this;
    }

    public RequestsHandlerMethod setOperation(Operation operation) {
        this.operation = operation;
        return this;
    }

    public boolean hasQueryParameters() {
        return (this.queryParameters != null) && !this.queryParameters.isEmpty();
    }

    public String getConsumesMediaType() {
        return requestBody != null ? requestBody.getMediaType() : null;
    }

    public String getProducesMediaType() {
        return responseBody != null ? responseBody.getMediaType() : null;
    }

    public String getOperationId() {
        return operation.getOperationId();
    }

    public boolean isAnyOptionalQueryParameter() {
        return queryParameters != null && queryParameters.stream().anyMatch(NamedDatum::isOptional);
    }

    public void replaceModelTypesByExternalTypesMap(Map<String, String> externalTypesMap) {
        if (requestBody != null) {
            replaceMediaTypeContentModelByExternalTypesMap(externalTypesMap, requestBody);
        }
        if (responseBody != null) {
            replaceMediaTypeContentModelByExternalTypesMap(externalTypesMap, responseBody);
        }
    }

    private void replaceMediaTypeContentModelByExternalTypesMap(Map<String, String> externalTypesMap, MediaTypeContentModel contentModel) {
        for (String internalSchemaName : externalTypesMap.keySet()) {
            DataModel content = contentModel.getContent();
            if (content.getTypeName().equals(internalSchemaName)) {
                if (content instanceof ObjectDataModel) {
                    ((ObjectDataModel) content).changeTypeName(externalTypesMap.get(internalSchemaName));
                }
            }
        }
    }

    public RequestsHandlerMethod setRequestBodyRequired(boolean requestBodyRequired) {
        this.requestBodyRequired = requestBodyRequired;
        return this;
    }

    public boolean isRequestBodyRequired() {
        return requestBodyRequired;
    }


    @Override
    public String toString() {
        return "RequestsHandlerMethod{" +
                "httpMethod='" + httpMethod + '\'' +
                ", uriPath='" + uriPath + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
