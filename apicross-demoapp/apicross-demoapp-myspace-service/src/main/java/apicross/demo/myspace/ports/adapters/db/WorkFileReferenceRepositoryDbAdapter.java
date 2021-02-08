package apicross.demo.myspace.ports.adapters.db;

import apicross.demo.myspace.domain.WorkFileReference;
import apicross.demo.myspace.domain.WorkFileReferenceRepository;
import org.springframework.stereotype.Component;

@Component
class WorkFileReferenceRepositoryDbAdapter implements WorkFileReferenceRepository {
    @Override
    public void add(WorkFileReference workFileReference) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void delete(String fileId) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
