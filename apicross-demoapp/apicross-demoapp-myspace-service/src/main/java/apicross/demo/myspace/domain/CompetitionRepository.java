package apicross.demo.myspace.domain;

import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface CompetitionRepository {
    void add(Competition competition);

    Competition findForUser(String competitionId, User user) throws CompetitionNotFoundException;

    List<Competition> findAllForUser(User user);

    void delete(String competitionId, User user);

    Competition find(String competitionId);
}
