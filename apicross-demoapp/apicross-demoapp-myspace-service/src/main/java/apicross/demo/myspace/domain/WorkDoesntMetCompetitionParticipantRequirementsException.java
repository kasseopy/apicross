package apicross.demo.myspace.domain;

import lombok.Getter;

@Getter
public class WorkDoesntMetCompetitionParticipantRequirementsException extends RuntimeException {
    private final String competitionId;
    private final CompetitionParticipantRequirements participantRequirements;
    private final WorkDescription workDescription;

    public WorkDoesntMetCompetitionParticipantRequirementsException(String competitionId, CompetitionParticipantRequirements participantRequirements, WorkDescription workDescription) {
        this.competitionId = competitionId;
        this.participantRequirements = participantRequirements;
        this.workDescription = workDescription;
    }
}
