package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.common.utils.ConditionalUpdateResult;
import apicross.demo.common.utils.ResourceObjectWithTag;
import apicross.demo.myspace.app.ManageCompetitionsService;
import apicross.demo.myspace.app.UpdateCompetitionCommand;
import apicross.demo.myspace.app.dto.*;
import apicross.demo.myspace.domain.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

@RestController
public class CompetitionsRequestsHandlerAdapter implements CompetitionsRequestsHandler {
    private final ManageCompetitionsService manageCompetitionsService;

    @Autowired
    public CompetitionsRequestsHandlerAdapter(ManageCompetitionsService manageCompetitionsService) {
        this.manageCompetitionsService = manageCompetitionsService;
    }

    @Override
    public ResponseEntity<?> registerNewCompetitionConsumeVndDemoappV1Json(HttpHeaders headers, CmRegisterCompetitionRequest requestEntity) {
        ResourceObjectWithTag<Competition> registerCompetitionResult = manageCompetitionsService.registerNewCompetition(requestEntity);
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(registerCompetitionResult.getEntity().getId());
        return ResponseEntity.created(location.toUri())
                .eTag(registerCompetitionResult.getEntityTag())
                .build();
    }

    @Override
    public ResponseEntity<CmListCompetitionsResponse> listCompetitionsProduceVndDemoappV1Json(HttpHeaders headers) {
        CmListCompetitionsResponse response = manageCompetitionsService.listAllCompetitionsForCurrentUser(new ListCompetitionsResponseViewAssembler());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CmGetCompetitionResponse> getCompetitionDescriptionProduceVndDemoappV1Json(String competitionId, HttpHeaders headers) {
        ResourceObjectWithTag<CmGetCompetitionResponse> response = manageCompetitionsService.getCompetition(competitionId, new GetCompetitionResponseViewAssembler());
        return ResponseEntity.status(HttpStatus.OK)
                .eTag(response.getEntityTag())
                .body(response.getEntity());
    }

    @Override
    public ResponseEntity<?> updateCompetitionConsumeVndDemoappV1Json(String competitionId, HttpHeaders headers, CmUpdateCompetitionRequest requestEntity) {
        ConditionalUpdateResult updateResult = manageCompetitionsService.updateCompetition(competitionId,
                new UpdateCompetitionCommand(requestEntity, headers.getIfMatch()));
        if (updateResult.isOk()) {
            return ResponseEntity.noContent().eTag(updateResult.getEtag()).build();
        } else {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }

    @Override
    public ResponseEntity<?> openCompetitionConsumeVndDemoappV1Json(String competitionId, HttpHeaders headers, CmOpenCompetitionRequest requestEntity) {
        return null;
    }
}