package apicross.demo.common.models;

public interface ModelTransformer<S, R> {
    R transform(S source);
}
