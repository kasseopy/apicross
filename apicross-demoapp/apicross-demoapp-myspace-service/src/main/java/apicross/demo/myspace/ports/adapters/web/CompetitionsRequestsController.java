package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.common.utils.ETagDoesntMatchException;
import apicross.demo.common.utils.HasETag;
import apicross.demo.common.utils.HttpEtagIfETagMatchPolicy;
import apicross.demo.common.utils.EntityWithETag;
import apicross.demo.myspace.app.ManageCompetitionsService;
import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.ports.adapters.web.models.*;
import apicross.demo.myspace.ports.adapters.web.models.GetCompetitionResponseViewAssembler;
import apicross.demo.myspace.ports.adapters.web.models.ListCompetitionsResponseViewAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

@RestController
public class CompetitionsRequestsController implements CompetitionsRequestsHandler {
    private final ManageCompetitionsService manageCompetitionsService;

    @Autowired
    public CompetitionsRequestsController(ManageCompetitionsService manageCompetitionsService) {
        this.manageCompetitionsService = manageCompetitionsService;
    }

    @Override
    public ResponseEntity<?> registerNewCompetitionConsumeVndDemoappV1Json(HttpHeaders headers,
                                                                           RpmCmRegisterCompetitionRequest requestEntity) {
        EntityWithETag<Competition> registerCompetitionResult = manageCompetitionsService.registerNewCompetition(currentUser(), requestEntity);
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(registerCompetitionResult.getEntity().getId());
        return ResponseEntity.created(location.toUri())
                .eTag(registerCompetitionResult.etag())
                .build();
    }

    @Override
    public ResponseEntity<RpmCmListCompetitionsResponse> listCompetitionsProduceVndDemoappV1Json(HttpHeaders headers) {
        RpmCmListCompetitionsResponse response = manageCompetitionsService.listAllCompetitionsForCurrentUser(currentUser(), new ListCompetitionsResponseViewAssembler());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteCompetition(String competitionId, HttpHeaders headers) {
        manageCompetitionsService.deleteCompetition(currentUser(), competitionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RpmCmGetCompetitionResponse> getCompetitionDescriptionProduceVndDemoappV1Json(String competitionId, HttpHeaders headers) {
        EntityWithETag<RpmCmGetCompetitionResponse> outcome = manageCompetitionsService.getCompetition(currentUser(), competitionId, new GetCompetitionResponseViewAssembler());
        return ResponseEntity.status(HttpStatus.OK)
                .eTag(outcome.etag())
                .body(outcome.getEntity());
    }

    @Override
    public ResponseEntity<?> updateCompetitionConsumeVndDemoappV1Json(String competitionId, HttpHeaders headers,
                                                                      RpmCmUpdateCompetitionRequest requestEntity) {
        try {
            HasETag outcome = manageCompetitionsService.updateCompetition(currentUser(), competitionId, requestEntity, new HttpEtagIfETagMatchPolicy(headers));
            return ResponseEntity.noContent().eTag(outcome.etag()).build();
        } catch (ETagDoesntMatchException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }

    @Override
    public ResponseEntity<?> openCompetitionConsumeVndDemoappV1Json(String competitionId, HttpHeaders headers,
                                                                    RpmCmOpenCompetitionRequest requestEntity) {
        manageCompetitionsService.openCompetition(currentUser(), competitionId, requestEntity);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> startCompetitionVoting(String competitionId, HttpHeaders headers) {
        manageCompetitionsService.startVoting(currentUser(), competitionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> closeCompetition(String competitionId, HttpHeaders headers) {
        manageCompetitionsService.closeCompetition(currentUser(), competitionId);
        return ResponseEntity.noContent().build();
    }

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
