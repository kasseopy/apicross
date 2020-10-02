package apicross.core.data.model;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class ObjectDataModelConstraints {
    private Integer minProperties;
    private Integer maxProperties;
    private Set<String> requiredProperties;

    public ObjectDataModelConstraints() {
    }

    public void setMinProperties(Integer minProperties) {
        this.minProperties = minProperties;
    }

    public void setMaxProperties(Integer maxProperties) {
        this.maxProperties = maxProperties;
    }

    public void setRequiredProperties(@Nonnull Set<String> requiredProperties) {
        Preconditions.checkArgument(requiredProperties != null);
        Preconditions.checkArgument(requiredProperties.size() > 0);
        this.requiredProperties = new HashSet<>(requiredProperties);
    }

    public void removeRequiredProperty(String propertyName) {
        // For post-processing needs
        if (this.requiredProperties != null) {
            this.requiredProperties.remove(propertyName);
        }
    }

    public Integer getMinProperties() {
        return minProperties;
    }

    public Integer getMaxProperties() {
        return maxProperties;
    }

    public Set<String> getRequiredProperties() {
        return requiredProperties;
    }
}
