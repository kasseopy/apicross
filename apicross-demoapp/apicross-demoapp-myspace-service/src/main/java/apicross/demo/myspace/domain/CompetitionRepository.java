package apicross.demo.myspace.domain;

import javax.annotation.Nonnull;
import java.util.List;

public interface CompetitionRepository {
    void add(@Nonnull Competition competition);

    @Nonnull
    Competition findCompetitionManagedByUser(@Nonnull String competitionId, @Nonnull String userId) throws CompetitionNotFoundException;

    @Nonnull
    List<Competition> findAllForUser(@Nonnull String userId);
}
