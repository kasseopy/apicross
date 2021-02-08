package apicross.demo.common.utils;

import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class EntityWithTag<T> {
    private final T entity;
    private final Supplier<String> etagSupplier;

    public EntityWithTag(@NonNull T entity, @NonNull Supplier<String> etagSupplier) {
        this.entity = Objects.requireNonNull(entity);
        this.etagSupplier = Objects.requireNonNull(etagSupplier);
    }

    @Nonnull
    public T getEntity() {
        return entity;
    }

    public String getEntityTag() {
        return etagSupplier.get();
    }
}
