package apicross.demo.myspace.domain;

import apicross.demo.common.models.AbstractEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "competition")
public class Competition extends AbstractEntity {
    @Column(name = "owner_id", length = 100)
    private String ownerId;
    @Column(name = "title", length = 100)
    private String title;
    @Column(name = "description", length = 1000)
    private String description;
    @Embedded
    private CompetitionParticipantRequirements participantRequirements;
    @Column(name = "voting_type", length = 100)
    @Enumerated(EnumType.STRING)
    private CompetitionVotingType votingType;
    @Column(name = "registered_at")
    private ZonedDateTime registeredAt;
    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private CompetitionStatus status;
    @Column(name = "accept_works_till")
    private LocalDate acceptWorksTillDate;
    @Column(name = "accept_votes_till")
    private LocalDate acceptVotesTillDate;

    public Competition(String id, String ownerId) {
        super(id, 0);
        this.ownerId = ownerId;
        this.status = CompetitionStatus.PENDING;
    }

    protected Competition() {
        super();
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public Competition setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Competition setDescription(String description) {
        this.description = description;
        return this;
    }

    public CompetitionParticipantRequirements getParticipantRequirements() {
        return participantRequirements;
    }

    public Competition setParticipantRequirements(CompetitionParticipantRequirements participantRequirements) {
        this.participantRequirements = participantRequirements;
        return this;
    }

    public CompetitionVotingType getVotingType() {
        return votingType;
    }

    public Competition setVotingType(CompetitionVotingType votingType) {
        this.votingType = votingType;
        return this;
    }

    public ZonedDateTime getRegisteredAt() {
        return registeredAt;
    }

    public Competition setRegisteredAt(ZonedDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public CompetitionStatus getStatus() {
        return status;
    }

    public LocalDate getAcceptWorksTillDate() {
        return acceptWorksTillDate;
    }

    public LocalDate getAcceptVotesTillDate() {
        return acceptVotesTillDate;
    }

    public void open(LocalDate acceptWorksTillDate, LocalDate acceptVotesTillDate) {
        if (status != CompetitionStatus.PENDING) {
            throw new IllegalCompetitionStatusException(getId(), status);
        }
        this.status = CompetitionStatus.OPEN;
        this.acceptWorksTillDate = acceptWorksTillDate;
        this.acceptVotesTillDate = acceptVotesTillDate;
    }

    public void startVoting() {
        if (status != CompetitionStatus.OPEN) {
            throw new IllegalCompetitionStatusException(getId(), status);
        }
        this.status = CompetitionStatus.VOTING;
    }

    public void close() {
        if (status == CompetitionStatus.CLOSED) {
            throw new IllegalCompetitionStatusException(getId(), status);
        }
        this.status = CompetitionStatus.CLOSED;
    }

    public void checkRequirementsSatisfiedBy(Work work) throws WorkDoesntMetCompetitionRequirements {
        int authorAge = work.getAuthorAge();
        if (!participantRequirements.isAuthorAgeSatisfied(authorAge)) {
            throw new WorkDoesntMetCompetitionRequirements(this.getId(), work.getId());
        }
    }
}
