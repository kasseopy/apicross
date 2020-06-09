package apicross.demo.myspace.domain;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CompetitionNotFoundException extends RuntimeException {
    private String competitionId;

    public CompetitionNotFoundException(@Nonnull String competitionId) {
        this.competitionId = Objects.requireNonNull(competitionId);
    }

    public String getCompetitionId() {
        return competitionId;
    }
}
