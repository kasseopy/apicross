package apicross.demo.myspace.app;

import apicross.demo.common.utils.ValidationStages;
import apicross.demo.common.models.QueryResultTransformer;
import apicross.demo.myspace.app.dto.RpmCmRegisterCompetitionRequest;
import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.domain.CompetitionRepository;
import apicross.demo.common.utils.ConditionalUpdateEntityCommand;
import apicross.demo.common.utils.ConditionalUpdateResult;
import apicross.demo.common.utils.ConditionalUpdateStatus;
import apicross.demo.common.utils.ResourceObjectWithTag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Service
@Slf4j
@Validated({ValidationStages.class})
public class ManageCompetitionsService {
    private final CompetitionFactory competitionFactory;
    private final CompetitionRepository competitionRepository;

    @Autowired
    public ManageCompetitionsService(CompetitionFactory competitionFactory, CompetitionRepository competitionRepository) {
        this.competitionFactory = competitionFactory;
        this.competitionRepository = competitionRepository;
    }

    @Transactional
    public ResourceObjectWithTag<Competition> registerNewCompetition(@Valid RpmCmRegisterCompetitionRequest command) {
        Competition competition = competitionFactory.create(command, currentUserId());
        competitionRepository.add(competition);
        return new ResourceObjectWithTag<>(competition, competition::etag);
    }

    @Transactional
    public ConditionalUpdateResult updateCompetition(String competitionId, @Valid ConditionalUpdateEntityCommand<Competition> command) {
        Competition competition = competitionRepository.findCompetitionManagedByUser(competitionId, currentUserId());
        ConditionalUpdateStatus conditionalUpdateStatus = command.updateIfEtagMatch(competition);
        return new ConditionalUpdateResult(conditionalUpdateStatus, competition::etag);
    }

    @Transactional(readOnly = true)
    public <T> T listAllCompetitionsForCurrentUser(QueryResultTransformer<List<Competition>, T> resultTransformer) {
        List<Competition> result = competitionRepository.findAllForUser(currentUserId());
        return resultTransformer.transform(result);
    }

    @Transactional(readOnly = true)
    public <T> ResourceObjectWithTag<T> getCompetition(String competitionId, QueryResultTransformer<Competition, T> responseAssembler) {
        Competition competition = competitionRepository.findCompetitionManagedByUser(competitionId, currentUserId());
        return new ResourceObjectWithTag<>(responseAssembler.transform(competition), competition::etag);
    }

    private String currentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();
    }
}
