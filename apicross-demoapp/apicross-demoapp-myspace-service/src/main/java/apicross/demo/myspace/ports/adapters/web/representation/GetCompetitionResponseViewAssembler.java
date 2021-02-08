package apicross.demo.myspace.ports.adapters.web.representation;

import apicross.demo.common.models.ModelTransformer;
import apicross.demo.myspace.app.dto.RpmCmGetCompetitionResponse;
import apicross.demo.myspace.domain.Competition;

public class GetCompetitionResponseViewAssembler implements ModelTransformer<Competition, RpmCmGetCompetitionResponse> {
    @Override
    public RpmCmGetCompetitionResponse transform(Competition source) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
