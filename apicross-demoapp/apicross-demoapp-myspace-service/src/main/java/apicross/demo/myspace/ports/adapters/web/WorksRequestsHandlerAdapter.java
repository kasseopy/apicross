package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.myspace.app.ManageWorksService;
import apicross.demo.myspace.app.dto.RpmWpGetWorkResponse;
import apicross.demo.myspace.app.dto.RpmWpListWorksResponse;
import apicross.demo.myspace.app.dto.RpmWpMultipartPlaceWorkRequest;
import apicross.demo.myspace.app.dto.RpmWpPlaceWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
public class WorksRequestsHandlerAdapter implements WorksRequestsHandler {
    private final ManageWorksService manageWorksService;

    @Autowired
    public WorksRequestsHandlerAdapter(ManageWorksService manageWorksService) {
        this.manageWorksService = manageWorksService;
    }

    @Override
    public ResponseEntity<?> placeWorkConsumeMultipartFormData(HttpHeaders headers, RpmWpMultipartPlaceWorkRequest requestEntity) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<?> placeWorkConsumeVndDemoappV1Json(HttpHeaders headers, RpmWpPlaceWorkRequest requestEntity) {
       throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<RpmWpListWorksResponse> listWorksProduceVndDemoappV1Json(HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<?> deleteWork(String workId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<RpmWpGetWorkResponse> getWorkDescriptionProduceVndDemoappV1Json(String workId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<?> publishWork(String workId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeImageJpeg(String workId, HttpHeaders headers, InputStream requestEntity) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeAudioMp4(String workId, HttpHeaders headers, InputStream requestEntity) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeVideoMp4(String workId, HttpHeaders headers, InputStream requestEntity) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<?> listWorkFiles(String workId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<?> deleteWorkFile(String fileId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<InputStream> downloadWorkFileProduceImageJpeg(String fileId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<InputStream> downloadWorkFileProduceAudioMp4(String fileId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<InputStream> downloadWorkFileProduceVideoMp4(String fileId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
