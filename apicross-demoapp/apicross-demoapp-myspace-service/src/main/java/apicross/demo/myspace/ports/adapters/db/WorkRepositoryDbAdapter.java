package apicross.demo.myspace.ports.adapters.db;

import apicross.demo.myspace.domain.Work;
import apicross.demo.myspace.domain.WorkRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class WorkRepositoryDbAdapter implements WorkRepository {
    @Override
    public void add(Work work) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<Work> findAllForUser(User user) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Work findForUser(User user, String workId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void delete(String workId, User user) {
        Work work = findForUser(user, workId);
        throw new UnsupportedOperationException("not implemented yet");
    }
}
