package apicross.demo.common.utils;

import java.util.Objects;
import java.util.function.Supplier;

public class ConditionalUpdateResult {
    private ConditionalUpdateStatus status;
    private Supplier<String> etagSupplier;

    public ConditionalUpdateResult(ConditionalUpdateStatus status, Supplier<String> etagSupplier) {
        this.status = Objects.requireNonNull(status);
        this.etagSupplier = Objects.requireNonNull(etagSupplier);
    }

    public String getEtag() {
        if (status != ConditionalUpdateStatus.UPDATED) {
            throw new IllegalStateException();
        }
        return etagSupplier.get();
    }

    public boolean isOk() {
        return status == ConditionalUpdateStatus.UPDATED;
    }
}
