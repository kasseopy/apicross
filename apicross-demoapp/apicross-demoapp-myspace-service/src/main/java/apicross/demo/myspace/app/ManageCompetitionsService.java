package apicross.demo.myspace.app;

import apicross.demo.common.utils.*;
import apicross.demo.common.models.ModelConverter;
import apicross.demo.myspace.app.dto.RpmCmOpenCompetitionRequest;
import apicross.demo.myspace.app.dto.RpmCmRegisterCompetitionRequest;
import apicross.demo.myspace.app.dto.RpmCmUpdateCompetitionRequest;
import apicross.demo.myspace.app.dto.RpmParticipantRequirements;
import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.domain.CompetitionParticipantRequirements;
import apicross.demo.myspace.domain.CompetitionRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Validated({ValidationStages.class})
public class ManageCompetitionsService {
    private final CompetitionFactory competitionFactory = new CompetitionFactory();
    private final CompetitionRepository competitionRepository;

    @Autowired
    public ManageCompetitionsService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    @Transactional
    public EntityWithTag<Competition> registerNewCompetition(@NonNull User user,
                                                             @NonNull @Valid RpmCmRegisterCompetitionRequest command) {
        Competition competition = competitionFactory.create(command, user.getUsername());
        competitionRepository.add(competition);
        return new EntityWithTag<>(competition, competition::etag);
    }

    @Transactional
    public ConditionalUpdateResult updateCompetition(@NonNull User user, @NonNull String competitionId,
                                                     @NonNull @Valid RpmCmUpdateCompetitionRequest command, @NonNull IfETagMatchPolicy ifETagMatchPolicy) {
        Competition competition = competitionRepository.findForUser(competitionId, user);
        ConditionalUpdateStatus conditionalUpdateStatus = new CompetitionPatcher(ifETagMatchPolicy, command).patchIfEtagMatch(competition);
        return new ConditionalUpdateResult(conditionalUpdateStatus, competition::etag);
    }

    @Transactional(readOnly = true)
    public <T> T listAllCompetitionsForCurrentUser(@NonNull User user, @NonNull ModelConverter<List<Competition>, T> resultTransformer) {
        List<Competition> result = competitionRepository.findAllForUser(user);
        return resultTransformer.convert(result);
    }

    @Transactional(readOnly = true)
    public <T> EntityWithTag<T> getCompetition(@NonNull User user, @NonNull String competitionId,
                                               @NonNull ModelConverter<Competition, T> resultTransformer) {
        Competition competition = competitionRepository.findForUser(competitionId, user);
        return new EntityWithTag<>(resultTransformer.convert(competition), competition::etag);
    }

    @Transactional
    public void deleteCompetition(@NonNull User user, @NonNull String competitionId) {
        competitionRepository.delete(competitionId, user);
    }

    @Transactional
    public void openCompetition(@NonNull User user, @NonNull String competitionId,
                                @NonNull @Valid RpmCmOpenCompetitionRequest command) {
        Competition competition = competitionRepository.findForUser(competitionId, user);
        competition.open(command.getAcceptWorksTillDate(), command.getAcceptVotesTillDate());
    }

    @Transactional
    public void startVoting(@NonNull User user, @NonNull String competitionId) {
        Competition competition = competitionRepository.findForUser(competitionId, user);
        competition.startVoting();
    }

    @Transactional
    public void closeCompetition(@NonNull User user, @NonNull String competitionId) {
        Competition competition = competitionRepository.findForUser(competitionId, user);
        competition.close();
    }

    static class CompetitionPatcher extends EntityPatcher<Competition> {
        private final RpmCmUpdateCompetitionRequest request;

        CompetitionPatcher(@NonNull IfETagMatchPolicy ifETagMatchPolicy, @NonNull RpmCmUpdateCompetitionRequest request) {
            super(ifETagMatchPolicy);
            this.request = request;
        }

        @Override
        protected void doPatch(Competition entityToBeUpdated) {
            request.ifTitlePresent(entityToBeUpdated::setTitle);
            request.ifDescriptionPresent(entityToBeUpdated::setDescription);
            request.ifParticipantReqsPresent(participantRequirements -> {
                CompetitionParticipantRequirements originReqs = entityToBeUpdated.getParticipantRequirements();
                if (originReqs == null) {
                    originReqs = new CompetitionParticipantRequirements();
                }
                participantRequirements.ifMaxAgePresent(originReqs::setMaxAge);
                participantRequirements.ifMinAgePresent(originReqs::setMinAge);
                entityToBeUpdated.setParticipantRequirements(originReqs);
            });
            request.ifVotingTypePresent(votingTypeAsString ->
                    entityToBeUpdated.setVotingType(VotingTypeFactory.detectVotingType(votingTypeAsString)));
        }
    }

    static class CompetitionFactory {
        Competition create(RpmCmRegisterCompetitionRequest request, String userId) {
            return new Competition(UUID.randomUUID().toString(), userId)
                    .setTitle(request.getTitle())
                    .setDescription(request.getDescription())
                    .setVotingType(VotingTypeFactory.detectVotingType(request.getVotingType()))
                    .setParticipantRequirements(createParticipantRequirements(request.getParticipantReqs()))
                    .setRegisteredAt(ZonedDateTime.now());

        }

        private CompetitionParticipantRequirements createParticipantRequirements(RpmParticipantRequirements participantReqs) {
            return new CompetitionParticipantRequirements()
                    .setMaxAge(participantReqs.isMaxAgePresent() ? participantReqs.getMaxAge() : null)
                    .setMinAge(participantReqs.isMinAgePresent() ? participantReqs.getMinAge() : null);
        }
    }
}
