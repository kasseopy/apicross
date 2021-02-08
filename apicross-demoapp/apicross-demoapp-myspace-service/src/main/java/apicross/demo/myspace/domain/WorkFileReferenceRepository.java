package apicross.demo.myspace.domain;

public interface WorkFileReferenceRepository {
    void add(WorkFileReference workFileReference);

    void delete(String fileId);
}
