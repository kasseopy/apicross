package apicross.demoapp.storefront.ports.adapters.web;

import apicross.demoapp.storefront.app.dto.*;
import apicross.demo.common.models.PaginatedResult;
import apicross.demoapp.storefront.app.StorefrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
public class StorefrontRequestsController implements StorefrontRequestsHandler {
    private final StorefrontService storefrontService;

    @Autowired
    public StorefrontRequestsController(StorefrontService storefrontService) {
        this.storefrontService = storefrontService;
    }

    @Override
    public ResponseEntity<SfListCompetitionsResponse> listOpenCompetitions(ListOpenCompetitionsQuery queryParameters, HttpHeaders headers) {
        PaginatedResult<SfCompetitionsShortDescription> result = storefrontService.search(queryParameters, new SfCompetitionShortDescriptionViewAssembler());
        return ResponseEntity.ok(new SfListCompetitionsResponse()
                .page(result.getPage())
                .pageContent(result.getContent()));
    }

    @Override
    public ResponseEntity<SfGetCompetitionResponse> getCompetitionDescription(String competitionId, GetCompetitionDescriptionQuery queryParameters, HttpHeaders headers) {
        return null;
    }

    @Override
    public ResponseEntity<SfListCompetitionWorksResponse> listCompetitionWorks(String competitionId, ListCompetitionWorksQuery queryParameters, HttpHeaders headers) {
        return null;
    }

    @Override

    public ResponseEntity<SfGetCompetitionWorkResponse> getWork(String workId, GetWorkQuery queryParameters, HttpHeaders headers) {
        return null;
    }

    @Override
    public ResponseEntity<InputStream> getWorkMediaProduceImageJpeg(String fileId, HttpHeaders headers) {
        return null;
    }

    @Override
    public ResponseEntity<InputStream> getWorkMediaProduceAudioMp4(String fileId, HttpHeaders headers) {
        return null;
    }

    @Override
    public ResponseEntity<InputStream> getWorkMediaProduceVideoMp4(String fileId, HttpHeaders headers) {
        return null;
    }
}
