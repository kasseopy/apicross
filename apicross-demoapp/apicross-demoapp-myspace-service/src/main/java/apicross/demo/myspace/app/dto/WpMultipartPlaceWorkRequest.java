package apicross.demo.myspace.app.dto;

import org.springframework.web.multipart.MultipartFile;

public class WpMultipartPlaceWorkRequest extends WpPlaceWorkRequest {
    private MultipartFile file1;
    private MultipartFile file2;
    private MultipartFile file3;

    public MultipartFile getFile1() {
        return file1;
    }

    public WpMultipartPlaceWorkRequest setFile1(MultipartFile file1) {
        this.file1 = file1;
        return this;
    }

    public MultipartFile getFile2() {
        return file2;
    }

    public WpMultipartPlaceWorkRequest setFile2(MultipartFile file2) {
        this.file2 = file2;
        return this;
    }

    public MultipartFile getFile3() {
        return file3;
    }

    public WpMultipartPlaceWorkRequest setFile3(MultipartFile file3) {
        this.file3 = file3;
        return this;
    }

    public static class WorkDescription {
        private String title;
        private String description;

        public String getTitle() {
            return title;
        }

        public WorkDescription setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public WorkDescription setDescription(String description) {
            this.description = description;
            return this;
        }
    }
}
