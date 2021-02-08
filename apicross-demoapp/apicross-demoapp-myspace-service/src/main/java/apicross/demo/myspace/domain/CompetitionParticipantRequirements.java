package apicross.demo.myspace.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CompetitionParticipantRequirements {
    @Column(name = "participant_req_min_age")
    private Integer minAge;
    @Column(name = "participant_req_max_age")
    private Integer maxAge;

    public Integer getMinAge() {
        return minAge;
    }

    public CompetitionParticipantRequirements setMinAge(Integer minAge) {
        this.minAge = minAge;
        return this;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public CompetitionParticipantRequirements setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public boolean isAuthorAgeSatisfied(int authorAge) {
        return ((minAge == null) || (authorAge >= minAge)) && ((maxAge == null) || (authorAge <= maxAge));
    }
}
