package apicross.demo.myspace.app;

import apicross.demo.common.utils.*;
import apicross.demo.common.models.ModelConverter;
import apicross.demo.myspace.app.dto.*;
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
    private final CompetitionRepository competitionRepository;

    @Autowired
    public ManageCompetitionsService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    @Transactional
    public EntityWithETag<Competition> registerNewCompetition(@NonNull User user,
                                                              @NonNull @Valid IReadRpmCmRegisterCompetitionRequest command) {
        Competition competition = create(command, user.getUsername());
        competitionRepository.add(competition);
        return new EntityWithETag<>(competition, competition::etag);
    }

    @Transactional
    public HasETag updateCompetition(@NonNull User user, @NonNull String competitionId,
                                     @NonNull @Valid IReadRpmCmUpdateCompetitionRequest command,
                                     @NonNull IfETagMatchPolicy ifETagMatchPolicy) {
        Competition competition = competitionRepository.findForUser(competitionId, user);
        CompetitionPatch patch = new CompetitionPatch(ifETagMatchPolicy, command);
        patch.apply(competition);
        return new HasETagSupplier(competition::etag);
    }

    @Transactional(readOnly = true)
    public <T> T listAllCompetitionsForCurrentUser(@NonNull User user, @NonNull ModelConverter<List<Competition>, T> modelConverter) {
        List<Competition> result = competitionRepository.findAllForUser(user);
        return modelConverter.convert(result);
    }

    @Transactional(readOnly = true)
    public <T> EntityWithETag<T> getCompetition(@NonNull User user, @NonNull String competitionId,
                                                @NonNull ModelConverter<Competition, T> modelConverter) {
        Competition competition = competitionRepository.findForUser(competitionId, user);
        return new EntityWithETag<>(modelConverter.convert(competition), competition::etag);
    }

    @Transactional
    public void deleteCompetition(@NonNull User user, @NonNull String competitionId) {
        competitionRepository.delete(competitionId, user);
    }

    @Transactional
    public void openCompetition(@NonNull User user, @NonNull String competitionId,
                                @NonNull @Valid IReadRpmCmOpenCompetitionRequest command) {
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

    static class CompetitionPatch extends ETagConditionalPatch<Competition> {
        private final IReadRpmCmUpdateCompetitionRequest request;

        CompetitionPatch(@NonNull IfETagMatchPolicy ifETagMatchPolicy, @NonNull IReadRpmCmUpdateCompetitionRequest request) {
            super(ifETagMatchPolicy);
            this.request = request;
        }

        @Override
        protected void doPatch(Competition entityToBeUpdated) {
            entityToBeUpdated.setTitle(request.getTitleOrElse(null));
            entityToBeUpdated.setDescription(request.getDescriptionOrElse(null));
            if (request.isParticipantReqsPresent()) {
                entityToBeUpdated.setParticipantRequirements(createParticipantRequirements(request.getParticipantReqs()));
            }
            entityToBeUpdated.setVotingType(VotingTypeFactory.detectVotingType(request.getVotingType()));
        }
    }

    private static Competition create(IReadRpmCmRegisterCompetitionRequest request, String userId) {
        return new Competition(UUID.randomUUID().toString(), userId)
                .setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setVotingType(VotingTypeFactory.detectVotingType(request.getVotingType()))
                .setParticipantRequirements(createParticipantRequirements(request.getParticipantReqs()))
                .setRegisteredAt(ZonedDateTime.now());

    }

    private static CompetitionParticipantRequirements createParticipantRequirements(IReadRpmParticipantRequirements participantReqs) {
        return new CompetitionParticipantRequirements()
                .setMaxAge(participantReqs.getMaxAgeOrElse(null))
                .setMinAge(participantReqs.getMinAgeOrElse(null));
    }
}
