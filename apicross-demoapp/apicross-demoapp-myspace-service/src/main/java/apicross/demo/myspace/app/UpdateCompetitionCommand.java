package apicross.demo.myspace.app;

import apicross.demo.common.utils.AbstractConditionalUpdateEntityCommand;
import apicross.demo.myspace.app.dto.CmUpdateCompetitionRequest;
import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.domain.CompetitionParticipantRequirements;

import java.util.Collection;
import java.util.Objects;

public class UpdateCompetitionCommand extends AbstractConditionalUpdateEntityCommand<Competition> {
    private CmUpdateCompetitionRequest request;

    public UpdateCompetitionCommand(CmUpdateCompetitionRequest request, Collection<String> requiredEtags) {
        super(requiredEtags);
        this.request = Objects.requireNonNull(request);
    }

    @Override
    protected void doUpdate(Competition entityToBeUpdated) {
        request.getTitle().ifPresent(entityToBeUpdated::setTitle);
        request.getDescription().ifPresent(entityToBeUpdated::setDescription);
        request.getParticipantReqs().ifPresent(participantRequirements -> {
            CompetitionParticipantRequirements originReqs = entityToBeUpdated.getParticipantRequirements();
            if (originReqs == null) {
                originReqs = new CompetitionParticipantRequirements();
            }
            participantRequirements.getMaxAge().ifPresent(originReqs::setMaxAge);
            participantRequirements.getMinAge().ifPresent(originReqs::setMinAge);
            entityToBeUpdated.setParticipantRequirements(originReqs);
        });
        request.getVotingType().map(VotingTypeFactory::detectVotingType)
                .ifPresent(entityToBeUpdated::setVotingType);
    }
}