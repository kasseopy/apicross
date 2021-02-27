package apicross.demo.myspace.app;

import apicross.demo.common.utils.*;
import apicross.demo.common.models.ModelConverter;
import apicross.demo.myspace.app.dto.*;
import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.domain.CompetitionParticipantRequirements;
import apicross.demo.myspace.domain.CompetitionRepository;
import apicross.demo.myspace.domain.CompetitionVotingType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated({ValidationStages.class})
public class ManageCompetitionsService {
    private final CompetitionRepository competitionRepository;

    @Autowired
    public ManageCompetitionsService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    @Transactional
    public EntityWithETag<Competition> registerNewCompetition(@NonNull User competitionOrganizer,
                                                              @NonNull @Valid IReadRpmCmRegisterCompetitionRequest command) {

        IReadRpmParticipantRequirements participantReqs = command.getParticipantReqs();

        Competition competition = new Competition(
                competitionOrganizer, command.getTitle(), command.getDescription(),
                new CompetitionParticipantRequirements(
                        participantReqs.getMinAgeOrElse(null),
                        participantReqs.getMaxAgeOrElse(null)),
                VotingTypeFactory.detectVotingType(command.getVotingType())
        );

        competitionRepository.add(competition);

        return new EntityWithETag<>(competition, competition::etag);
    }

    @Transactional
    public HasETag updateCompetition(@NonNull User user, @NonNull String competitionId,
                                     @NonNull @Valid IReadRpmCmUpdateCompetitionRequest command,
                                     @NonNull ETagMatchPolicy ETagMatchPolicy) {
        Competition competition = competitionRepository.findForUser(user, competitionId);
        CompetitionPatch patch = new CompetitionPatch(ETagMatchPolicy, command);
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
        Competition competition = competitionRepository.findForUser(user, competitionId);
        return new EntityWithETag<>(modelConverter.convert(competition), competition::etag);
    }

    @Transactional
    public void deleteCompetition(@NonNull User user, @NonNull String competitionId) {
        competitionRepository.delete(user, competitionId);
    }

    @Transactional
    public void openCompetition(@NonNull User user, @NonNull String competitionId,
                                @NonNull @Valid IReadRpmCmOpenCompetitionRequest command) {
        Competition competition = competitionRepository.findForUser(user, competitionId);
        competition.open(command.getAcceptWorksTillDate(), command.getAcceptVotesTillDate());
    }

    @Transactional
    public void startVoting(@NonNull User user, @NonNull String competitionId) {
        Competition competition = competitionRepository.findForUser(user, competitionId);
        competition.startVoting();
    }

    @Transactional
    public void closeCompetition(@NonNull User user, @NonNull String competitionId) {
        Competition competition = competitionRepository.findForUser(user, competitionId);
        competition.close();
    }

    static class CompetitionPatch extends ETagConditionalPatch<Competition> {
        private final IReadRpmCmUpdateCompetitionRequest request;

        CompetitionPatch(@NonNull ETagMatchPolicy ETagMatchPolicy, @NonNull IReadRpmCmUpdateCompetitionRequest request) {
            super(ETagMatchPolicy);
            this.request = request;
        }

        @Override
        protected void doPatch(Competition entityToBeUpdated) {
            entityToBeUpdated.setTitle(request.getTitleOrElse(null));
            entityToBeUpdated.setDescription(request.getDescriptionOrElse(null));
            if (request.isParticipantReqsPresent()) {
                IReadRpmParticipantRequirements participantReqs = request.getParticipantReqs();
                entityToBeUpdated.setParticipantRequirements(new CompetitionParticipantRequirements(
                        participantReqs.getMinAgeOrElse(null),
                        participantReqs.getMaxAgeOrElse(null)));
            }
            entityToBeUpdated.setVotingType(VotingTypeFactory.detectVotingType(request.getVotingType()));
        }
    }

    static class VotingTypeFactory {
        static CompetitionVotingType detectVotingType(String votingType) {
            if ("ClapsVoting".equals(votingType)) {
                return CompetitionVotingType.CLAPS_VOTING;
            } else if ("PointsVoting".equals(votingType)) {
                return CompetitionVotingType.POINTS_VOTING;
            } else {
                throw new IllegalArgumentException("Unknown voting type: " + votingType);
            }
        }
    }
}
