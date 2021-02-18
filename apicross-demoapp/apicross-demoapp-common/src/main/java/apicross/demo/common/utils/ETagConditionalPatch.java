package apicross.demo.common.utils;

import apicross.demo.common.models.AbstractEntity;

public abstract class ETagConditionalPatch<T extends AbstractEntity> {
    private final IfETagMatchPolicy ifETagMatchPolicy;

    public ETagConditionalPatch(IfETagMatchPolicy ifETagMatchPolicy) {
        this.ifETagMatchPolicy = ifETagMatchPolicy;
    }

    public void apply(T entity) {
        if (ifETagMatchPolicy.ifMatch(entity.etag())) {
            doPatch(entity);
        } else {
            throw new ETagDoesntMatchException();
        }
    }

    protected abstract void doPatch(T entity);
}
