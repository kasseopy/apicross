package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.common.models.QueryResultTransformer;
import apicross.demo.myspace.app.dto.RpmCmGetCompetitionResponse;
import apicross.demo.myspace.domain.Competition;

public class GetCompetitionResponseViewAssembler implements QueryResultTransformer<Competition, RpmCmGetCompetitionResponse> {
    @Override
    public RpmCmGetCompetitionResponse transform(Competition source) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
