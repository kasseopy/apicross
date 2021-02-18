package apicross.demo.myspace.app;

import apicross.demo.common.models.ModelConverter;
import apicross.demo.common.utils.EntityWithETag;
import apicross.demo.common.utils.ValidationStages;
import apicross.demo.myspace.app.dto.IReadRpmWpPlaceWorkRequest;
import apicross.demo.myspace.domain.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@Validated({ValidationStages.class})
@Slf4j
public class ManageWorksService {
    private final WorkRepository workRepository;
    private final CompetitionRepository competitionRepository;
    private final WorkFileReferenceRepository workFileReferenceRepository;
    private final FilesStore filesStore;

    @Autowired
    public ManageWorksService(WorkRepository workRepository, CompetitionRepository competitionRepository,
                              WorkFileReferenceRepository workFileReferenceRepository, FilesStore filesStore) {
        this.workRepository = workRepository;
        this.competitionRepository = competitionRepository;
        this.workFileReferenceRepository = workFileReferenceRepository;
        this.filesStore = filesStore;
    }

    @Transactional
    public EntityWithETag<Work> placeWork(@NonNull User user, @NonNull @Valid IReadRpmWpPlaceWorkRequest command) {
        Work work = new Work(UUID.randomUUID().toString(), user, command.getAuthor(), command.getAuthorAge())
                .setTitle(command.getTitle())
                .setDescription(command.getDescription());

        workRepository.add(work);

        return new EntityWithETag<>(work, work::etag);
    }

    @Transactional(readOnly = true)
    public <T> T listWorks(@NonNull User user, @NonNull ModelConverter<List<Work>, T> resultTransformer) {
        List<Work> works = workRepository.findAllForUser(user);
        return resultTransformer.convert(works);
    }

    @Transactional
    public void deleteWork(@NonNull User user, @NonNull String workId) {
        workRepository.delete(workId, user);
    }

    @Transactional(readOnly = true)
    public <T> T getWork(@NonNull User user, @NonNull String workId, @NonNull ModelConverter<Work, T> resultTransformer) {
        Work work = workRepository.findForUser(user, workId);
        return resultTransformer.convert(work);
    }

    @Transactional
    public void publishWork(@NonNull User user, @NonNull String competitionId, @NonNull String workId) {
        Work work = workRepository.findForUser(user, workId);
        Competition competition = competitionRepository.find(competitionId);
        work.publishTo(competition);
    }

    @Transactional(rollbackFor = IOException.class)
    public WorkFileReference addWorkFile(@NonNull User user, @NonNull String workId,
                                         @NonNull InputStream content, @NonNull String contentType) throws IOException {
        Work work = workRepository.findForUser(user, workId);
        WorkFileReference workFileReference = work.addFileReference(contentType);
        workFileReferenceRepository.add(workFileReference);
        filesStore.save(workFileReference, content);
        return workFileReference;
    }

    @Transactional
    public void deleteWorkFile(@NonNull String fileId) {
        workFileReferenceRepository.delete(fileId);
        filesStore.delete(fileId);
    }

    public InputStream fileContent(@NonNull String fileId) throws FileNotFoundException {
        return filesStore.fileContent(fileId);
    }
}
