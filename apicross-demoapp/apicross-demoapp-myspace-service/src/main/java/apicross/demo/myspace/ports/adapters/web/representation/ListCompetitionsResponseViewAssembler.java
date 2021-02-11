package apicross.demo.myspace.ports.adapters.web.representation;

import apicross.demo.common.models.ModelConverter;
import apicross.demo.myspace.app.dto.RpmCmListCompetitionsResponse;
import apicross.demo.myspace.domain.Competition;

import java.util.List;

public class ListCompetitionsResponseViewAssembler implements ModelConverter<List<Competition>, RpmCmListCompetitionsResponse> {
    @Override
    public RpmCmListCompetitionsResponse convert(List<Competition> source) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
