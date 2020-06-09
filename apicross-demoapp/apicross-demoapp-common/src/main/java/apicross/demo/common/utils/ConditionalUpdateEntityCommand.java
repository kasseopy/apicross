package apicross.demo.common.utils;


import apicross.demo.common.models.AbstractEntity;

public interface ConditionalUpdateEntityCommand<T extends AbstractEntity> {
    ConditionalUpdateStatus updateIfEtagMatch(T entity);
}
