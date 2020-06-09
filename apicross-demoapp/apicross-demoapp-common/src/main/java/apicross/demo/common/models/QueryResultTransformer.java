package apicross.demo.common.models;

public interface QueryResultTransformer<S, R> {
    R transform(S source);
}
