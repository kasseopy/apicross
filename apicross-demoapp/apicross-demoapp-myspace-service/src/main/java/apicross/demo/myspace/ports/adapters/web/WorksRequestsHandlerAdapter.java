package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.common.utils.EntityWithTag;
import apicross.demo.myspace.app.ManageWorksService;
import apicross.demo.myspace.app.dto.RpmWpGetWorkResponse;
import apicross.demo.myspace.app.dto.RpmWpListWorksResponse;
import apicross.demo.myspace.app.dto.RpmWpPlaceWorkRequest;
import apicross.demo.myspace.domain.Work;
import apicross.demo.myspace.domain.WorkFileReference;
import apicross.demo.myspace.ports.adapters.web.representation.GetWorkResponseViewAssembler;
import apicross.demo.myspace.ports.adapters.web.representation.ListWorksResponseViewAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class WorksRequestsHandlerAdapter implements WorksRequestsHandler {
    private final ManageWorksService manageWorksService;

    @Autowired
    public WorksRequestsHandlerAdapter(ManageWorksService manageWorksService) {
        this.manageWorksService = manageWorksService;
    }

    @Override
    public ResponseEntity<?> placeWorkConsumeVndDemoappV1Json(HttpHeaders headers, RpmWpPlaceWorkRequest requestEntity) {
        final EntityWithTag<Work> placeWorkResult = manageWorksService.placeWork(currentUser(), requestEntity);
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(placeWorkResult.getEntity().getId());
        return ResponseEntity.created(location.toUri())
                .eTag(placeWorkResult.getEntityTag())
                .build();
    }

    @Override
    public ResponseEntity<RpmWpListWorksResponse> listWorksProduceVndDemoappV1Json(HttpHeaders headers) {
        RpmWpListWorksResponse response = manageWorksService.listWorks(currentUser(), new ListWorksResponseViewAssembler());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteWork(String workId, HttpHeaders headers) {
        manageWorksService.deleteWork(currentUser(), workId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RpmWpGetWorkResponse> getWorkDescriptionProduceVndDemoappV1Json(String workId, HttpHeaders headers) {
        RpmWpGetWorkResponse response = manageWorksService.getWork(currentUser(), workId, new GetWorkResponseViewAssembler());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> publishWork(String competitionId, String workId, HttpHeaders headers) {
        manageWorksService.publishWork(currentUser(), competitionId, workId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeImageJpeg(String workId, HttpHeaders headers, InputStream requestEntity) throws IOException {
        return addWorkFile(workId, requestEntity, "image/jpeg");
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeAudioMp4(String workId, HttpHeaders headers, InputStream requestEntity) throws IOException {
        return addWorkFile(workId, requestEntity, "audio/mp4");
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeVideoMp4(String workId, HttpHeaders headers, InputStream requestEntity) throws IOException {
        return addWorkFile(workId, requestEntity, "video/mp4");
    }

    @Override
    public ResponseEntity<?> deleteWorkFile(String fileId, HttpHeaders headers) {
        manageWorksService.deleteWorkFile(fileId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadWorkFileProduceImageJpeg(String fileId, HttpHeaders headers) {
        return downloadFile(fileId);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadWorkFileProduceAudioMp4(String fileId, HttpHeaders headers) {
        return downloadFile(fileId);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadWorkFileProduceVideoMp4(String fileId, HttpHeaders headers) {
        return downloadFile(fileId);
    }

    private ResponseEntity<?> addWorkFile(String workId, InputStream requestEntity, String s) throws IOException {
        try {
            WorkFileReference result = manageWorksService.addWorkFile(currentUser(), workId, requestEntity, s);

            UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest().path("/my/files/{fileId}")
                    .buildAndExpand(result.getId());

            return ResponseEntity.created(location.toUri())
                    .build();
        } finally {
            requestEntity.close();
        }
    }

    private ResponseEntity<InputStreamResource> downloadFile(String fileId) {
        try {
            InputStreamResource inputStreamResource = new InputStreamResource(manageWorksService.fileContent(fileId));

            return ResponseEntity.ok(inputStreamResource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
