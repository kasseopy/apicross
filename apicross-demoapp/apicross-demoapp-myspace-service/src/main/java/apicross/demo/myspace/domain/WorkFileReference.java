package apicross.demo.myspace.domain;

import apicross.demo.common.models.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "work_file")
public class WorkFileReference extends AbstractEntity {
    @Column(name = "content_type", length = 200)
    private String contentType;
    @ManyToOne
    private Work work;

    WorkFileReference(Work work, String contentType) {
        super(UUID.randomUUID().toString(), 0);
        this.work = work;
        this.contentType = contentType;
    }

    protected WorkFileReference() {
    }

    public String getContentType() {
        return contentType;
    }

    public Work getWork() {
        return work;
    }
}
