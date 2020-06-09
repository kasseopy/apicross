package apicross.demoapp.storefront.ports.adapters.web;

import apicross.demoapp.storefront.app.VotesService;
import apicross.demoapp.storefront.app.dto.VtCompetitionResultsResponse;
import apicross.demoapp.storefront.app.dto.VtVoteRequest;
import apicross.demoapp.storefront.app.dto.VtVotesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VotesRequestsController implements VotesRequestsHandler {
    private final VotesService votesService;

    @Autowired
    public VotesRequestsController(VotesService votesService) {
        this.votesService = votesService;
    }

    @Override
    public ResponseEntity<?> vote(String workId, HttpHeaders headers, VtVoteRequest requestEntity) {
        votesService.voteForWork(workId, requestEntity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<VtVotesResponse> listVotes(String workId, HttpHeaders headers) {
        return null;
    }

    @Override
    public ResponseEntity<VtCompetitionResultsResponse> getContestResults(String competitionId, HttpHeaders headers) {
        return null;
    }
}
