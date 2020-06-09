package apicross.demo.common.utils;

public class RequestBodyJsonFieldValidationError {
    private String fieldPath;
    private Object invalidValue;
    private String message;

    public RequestBodyJsonFieldValidationError(String fieldPath, Object invalidValue, String message) {
        this.fieldPath = fieldPath;
        this.invalidValue = invalidValue;
        this.message = message;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public String getMessage() {
        return message;
    }
}
