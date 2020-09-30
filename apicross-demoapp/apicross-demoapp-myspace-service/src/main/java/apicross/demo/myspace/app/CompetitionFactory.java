package apicross.demo.myspace.app;

import apicross.demo.myspace.app.dto.RpmCmRegisterCompetitionRequest;
import apicross.demo.myspace.app.dto.RpmParticipantRequirements;
import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.domain.CompetitionParticipantRequirements;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Component
class CompetitionFactory {
    Competition create(RpmCmRegisterCompetitionRequest request, String userId) {
        return new Competition(UUID.randomUUID().toString(), userId)
                .setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setVotingType(VotingTypeFactory.detectVotingType(request.getVotingType()))
                .setParticipantRequirements(createParticipantRequirements(request))
                .setRegisteredAt(ZonedDateTime.now());

    }

    private CompetitionParticipantRequirements createParticipantRequirements(RpmCmRegisterCompetitionRequest request) {
        RpmParticipantRequirements participantReqs = request.getParticipantReqs();
        return new CompetitionParticipantRequirements()
                .setMaxAge(participantReqs.isMaxAgePresent() ? participantReqs.getMaxAge() : null)
                .setMinAge(participantReqs.isMinAgePresent() ? participantReqs.getMinAge() : null);
    }
}
