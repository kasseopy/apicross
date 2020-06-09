package apicross.core.handler;

import apicross.core.HasCustomAttribures;
import apicross.core.data.DataModel;

import javax.annotation.Nonnull;
import java.util.Objects;

public class MediaTypeContentModel extends HasCustomAttribures {
    private DataModel content;
    private String mediaType;

    public MediaTypeContentModel(@Nonnull DataModel content, @Nonnull String mediaType) {
        this.content = Objects.requireNonNull(content, "'content' argument must not be null");
        this.mediaType = Objects.requireNonNull(mediaType, "'mediaType' argument must not be null");
    }

    @Nonnull
    public DataModel getContent() {
        return content;
    }

    @Nonnull
    public String getMediaType() {
        return mediaType;
    }
}
