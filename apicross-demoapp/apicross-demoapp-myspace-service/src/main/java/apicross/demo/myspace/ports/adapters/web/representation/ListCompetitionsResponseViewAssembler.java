package apicross.demo.myspace.ports.adapters.web.representation;

import apicross.demo.common.models.ModelTransformer;
import apicross.demo.myspace.app.dto.RpmCmListCompetitionsResponse;
import apicross.demo.myspace.domain.Competition;

import java.util.List;

public class ListCompetitionsResponseViewAssembler implements ModelTransformer<List<Competition>, RpmCmListCompetitionsResponse> {
    @Override
    public RpmCmListCompetitionsResponse transform(List<Competition> source) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
