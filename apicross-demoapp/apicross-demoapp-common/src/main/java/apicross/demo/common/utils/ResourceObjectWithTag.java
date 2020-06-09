package apicross.demo.common.utils;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class ResourceObjectWithTag<T> {
    private T entity;
    private Supplier<String> etagSupplier;

    public ResourceObjectWithTag(@Nonnull T entity, @Nonnull Supplier<String> etagSupplier) {
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
