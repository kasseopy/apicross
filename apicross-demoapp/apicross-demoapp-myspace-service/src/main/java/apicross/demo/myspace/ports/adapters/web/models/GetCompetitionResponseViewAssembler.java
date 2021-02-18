package apicross.demo.myspace.ports.adapters.web.models;

import apicross.demo.common.models.ModelConverter;
import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.domain.CompetitionStatus;
import apicross.demo.myspace.domain.CompetitionVotingType;

public class GetCompetitionResponseViewAssembler implements ModelConverter<Competition, RpmCmGetCompetitionResponse> {
    @Override
    public RpmCmGetCompetitionResponse convert(Competition source) {
        return new RpmCmGetCompetitionResponse()
                .withId(source.getId())
                .withTitle(source.getTitle())
                .withDescription(source.getDescription())
                .withParticipantReqs(new RpmParticipantRequirements()
                        .withMaxAge(source.getParticipantRequirements().getMaxAge())
                        .withMinAge(source.getParticipantRequirements().getMinAge()))
                .withStatus(encodeStatus(source.getStatus()))
                .withVotingType(encodeVotingType(source.getVotingType()));
    }

    private String encodeStatus(CompetitionStatus status) {
        switch (status) {
            case OPEN:
                return "Open";
            case CLOSED:
                return "Closed";
            case VOTING:
                return "Voting";
            case PENDING:
                return "Pending";
            case REJECTED:
                return "Rejected";
            default:
                throw new IllegalArgumentException("Unsupported status: " + status);
        }
    }

    private String encodeVotingType(CompetitionVotingType votingType) {
        switch (votingType) {
            case CLAPS_VOTING:
                return "ClapsVoting";
            case POINTS_VOTING:
                return "PointsVoting";
            default:
                throw new IllegalArgumentException("Unsupported votingType: " + votingType);
        }
    }


}
