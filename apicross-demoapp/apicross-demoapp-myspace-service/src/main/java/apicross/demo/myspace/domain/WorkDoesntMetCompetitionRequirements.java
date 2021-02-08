package apicross.demo.myspace.domain;

import lombok.Getter;

@Getter
public class WorkDoesntMetCompetitionRequirements extends RuntimeException {
    private final String competitionId;
    private final String workId;

    public WorkDoesntMetCompetitionRequirements(String competitionId, String workId) {
        this.competitionId = competitionId;
        this.workId = workId;
    }
}
