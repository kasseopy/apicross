package apicross.demo.myspace.domain;

import apicross.demo.common.models.AbstractEntity;

import javax.persistence.*;
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

    public Competition(String id, String ownerId) {
        super(id, 0);
        this.ownerId = ownerId;
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
}
