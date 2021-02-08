package apicross.demo.common.utils;

import apicross.demo.common.models.AbstractEntity;

public abstract class EntityPatcher<T extends AbstractEntity> {
    private final IfETagMatchPolicy ifETagMatchPolicy;

    public EntityPatcher(IfETagMatchPolicy ifETagMatchPolicy) {
        this.ifETagMatchPolicy = ifETagMatchPolicy;
    }

    public ConditionalUpdateStatus patchIfEtagMatch(T entity) {
        if (ifETagMatchPolicy.ifMatch(entity.etag())) {
            doPatch(entity);
            return ConditionalUpdateStatus.UPDATED;
        }
        return ConditionalUpdateStatus.NOT_UPDATED_IS_NONE_MATCH;
    }

    protected abstract void doPatch(T entity);
}
