package apicross.demo.common.utils;

import apicross.demo.common.models.AbstractEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public abstract class AbstractConditionalUpdateEntityCommand<T extends AbstractEntity> implements ConditionalUpdateEntityCommand<T> {
    private final Collection<String> requiredEtags;

    protected AbstractConditionalUpdateEntityCommand(Collection<String> requiredEtags) {
        this.requiredEtags = Collections.unmodifiableCollection(
                requiredEtags.stream().map(this::removeLeadingAndTrailingDoubleQuotes)
                        .collect(Collectors.toSet()));
    }

    private String removeLeadingAndTrailingDoubleQuotes(String etag) {
        String result = etag;
        if (etag.startsWith("\"")) {
            result = result.substring(1);
        }
        if (etag.endsWith("\"")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    @Override
    public ConditionalUpdateStatus updateIfEtagMatch(T entity) {
        if (requiredTagsMatch(entity)) {
            doUpdate(entity);
            return ConditionalUpdateStatus.UPDATED;
        } else {
            return ConditionalUpdateStatus.NOT_UPDATED_IS_NONE_MATCH;
        }
    }

    protected abstract void doUpdate(T entity);

    protected Collection<String> getRequiredEtags() {
        return requiredEtags;
    }

    protected boolean requiredTagsMatch(T entity) {
        return requiredEtags == null || requiredEtags.isEmpty() || requiredEtags.contains(entity.etag());
    }
}
