package apicross.demo.common.utils;

public interface IfETagMatchPolicy {
    boolean ifMatch(String etag);
}
