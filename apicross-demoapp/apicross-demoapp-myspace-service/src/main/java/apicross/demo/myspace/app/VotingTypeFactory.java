package apicross.demo.myspace.app;

import apicross.demo.myspace.domain.CompetitionVotingType;

class VotingTypeFactory {
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
