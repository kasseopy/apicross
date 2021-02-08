package apicross.demo.myspace.domain;

import apicross.demo.common.models.AbstractEntity;
import lombok.NonNull;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "work")
public class Work extends AbstractEntity {
    @Column(name = "owner_id", length = 100)
    private String owner;
    @Column(name = "title", length = 100)
    private String title;
    @Column(name = "description", length = 2000)
    private String description;
    @Column(name = "author", length = 150)
    private String author;
    @Column(name = "author_age")
    private int authorAge;
    @Column(name = "placed_at")
    private LocalDate placedAt;
    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private WorkStatus status;
    @OneToMany
    private Set<WorkFileReference> files = new HashSet<>();

    public Work(@NonNull String id, @NonNull User owner, @NonNull String author, int authorAge) {
        super(id, 0);
        this.placedAt = LocalDate.now();
        this.owner = owner.getUsername();
        this.author = author;
        this.authorAge = authorAge;
    }

    protected Work() {
        super();
    }

    public String getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public Work setTitle(@NonNull String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Work setDescription(@NonNull String description) {
        this.description = description;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public int getAuthorAge() {
        return authorAge;
    }

    public LocalDate getPlacedAt() {
        return placedAt;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public WorkFileReference addFileReference(String contentType) {
        WorkFileReference workFileReference = new WorkFileReference(this, contentType);
        this.files.add(workFileReference);
        return workFileReference;
    }

    public void publishTo(Competition competition) {
        if (competition.getStatus() == CompetitionStatus.OPEN) {
            competition.checkRequirementsSatisfiedBy(this);
            this.status = WorkStatus.PUBLISHED;
        } else {
            throw new IllegalCompetitionStatusException(competition.getId(), competition.getStatus());
        }
    }
}
