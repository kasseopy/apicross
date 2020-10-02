package apicross.core.data.model;

import io.swagger.v3.oas.models.media.*;
import org.apache.commons.lang3.BooleanUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PrimitiveDataModel extends DataModel {
    PrimitiveDataModel(Schema<?> source) {
        super(source);
    }

    public String getType() {
        return getSource().getType();
    }

    public String getFormat() {
        return getSource().getFormat();
    }

    public boolean isConstrainedStringLength() {
        return isMaxLengthDefined() || isMinLengthDefined();
    }

    public boolean isMinLengthDefined() {
        return getSource().getMinLength() != null;
    }

    public Integer getMinLength() {
        return getSource().getMinLength();
    }

    public boolean isMaxLengthDefined() {
        return getSource().getMaxLength() != null;
    }

    public Integer getMaxLength() {
        return getSource().getMaxLength();
    }

    public String getPattern() {
        return getSource().getPattern();
    }

    @Nullable
    public Set<String> getEnumValues() {
        List<?> anEnum = getSource().getEnum();
        return anEnum == null ? null : anEnum.stream()
                .map((Function<Object, String>) Object::toString)
                .collect(Collectors.toSet());
    }

    public boolean isMinimumDefined() {
        return getSource().getMinimum() != null;
    }

    public Number getMinimum() {
        BigDecimal minimum = getSource().getMinimum();
        return minimum == null ? null : toNumberAccordingType(minimum);
    }

    public boolean isMaximumDefined() {
        return getSource().getMaximum() != null;
    }

    public Number getMaximum() {
        BigDecimal maximum = getSource().getMaximum();
        return maximum == null ? null : toNumberAccordingType(maximum);
    }

    public boolean isExclusiveMinimum() {
        return BooleanUtils.isTrue(getSource().getExclusiveMinimum());
    }

    public boolean isExclusiveMaximum() {
        return BooleanUtils.isTrue(getSource().getExclusiveMaximum());
    }

    private Number toNumberAccordingType(BigDecimal value) {
        Schema<?> source = getSource();
        if (source instanceof IntegerSchema && "int32".equals(getFormat())) {
            return value.intValue();
        } else if (source instanceof IntegerSchema && "int64".equals(getFormat())) {
            return value.longValue();
        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        return "PrimitiveDataModelSchema{" + getType() + "}";
    }
}
