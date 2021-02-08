package apicross.demo.myspace.domain;

import lombok.Getter;

@Getter
public class IllegalCompetitionStatusException extends RuntimeException {
    private final String competitionId;
    private final CompetitionStatus status;

    public IllegalCompetitionStatusException(String competitionId, CompetitionStatus status) {
        this.competitionId = competitionId;
        this.status = status;
    }
}
