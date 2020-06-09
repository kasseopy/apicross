package apicross.demo.myspace.ports.adapters.db;

import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.domain.CompetitionNotFoundException;
import apicross.demo.myspace.domain.CompetitionRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@Component
public class CompetitionRepositoryDbAdapter implements CompetitionRepository {
    private final CompetitionDao competitionDao;

    public CompetitionRepositoryDbAdapter(CompetitionDao competitionDao) {
        this.competitionDao = competitionDao;
    }

    @Override
    public void add(@Nonnull Competition competition) {
        competitionDao.save(competition);
    }

    @Nonnull
    @Override
    public Competition findCompetitionManagedByUser(@Nonnull String competitionId, @Nonnull String userId) {
        Optional<Competition> competitionOpt = competitionDao.findById(competitionId);
        if (competitionOpt.isPresent()) {
            Competition competition = competitionOpt.get();
            if (competition.getOwnerId().equals(userId)) {
                return competition;
            }
        }
        throw new CompetitionNotFoundException(competitionId);
    }

    @Nonnull
    @Override
    public List<Competition> findAllForUser(@Nonnull String userId) {
        return competitionDao.findAllForUserId(userId);
    }
}
