package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.myspace.app.ManageWorksService;
import apicross.demo.myspace.app.dto.WpGetWorkResponse;
import apicross.demo.myspace.app.dto.WpListWorksResponse;
import apicross.demo.myspace.app.dto.WpMultipartPlaceWorkRequest;
import apicross.demo.myspace.app.dto.WpPlaceWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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
    public ResponseEntity<?> placeWorkConsumeMultipartFormData(HttpHeaders headers, WpMultipartPlaceWorkRequest requestEntity) {
        return null;
    }

    @Override
    public ResponseEntity<?> placeWorkConsumeVndDemoappV1Json(HttpHeaders headers, WpPlaceWorkRequest requestEntity) {
        return null;
    }

    @Override
    public ResponseEntity<WpListWorksResponse> listWorksProduceVndDemoappV1Json(HttpHeaders headers) {
        return null;
    }

    @Override
    public ResponseEntity<WpGetWorkResponse> getWorkDescriptionProduceVndDemoappV1Json(String workId, HttpHeaders headers) {
        return null;
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeImageJpeg(String workId, HttpHeaders headers, InputStream requestEntity) {
        return null;
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeAudioMp4(String workId, HttpHeaders headers, InputStream requestEntity) {
        return null;
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeVideoMp4(String workId, HttpHeaders headers, InputStream requestEntity) {
        return null;
    }

    @Override
    public ResponseEntity<InputStream> downloadWorkFileProduceImageJpeg(String fileId, HttpHeaders headers) {
        return null;
    }

    @Override
    public ResponseEntity<InputStream> downloadWorkFileProduceAudioMp4(String fileId, HttpHeaders headers) {
        return null;
    }

    @Override
    public ResponseEntity<InputStream> downloadWorkFileProduceVideoMp4(String fileId, HttpHeaders headers) {
        return null;
    }
}
