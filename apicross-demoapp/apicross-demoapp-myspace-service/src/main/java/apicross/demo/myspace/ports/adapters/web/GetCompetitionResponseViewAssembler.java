package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.common.models.QueryResultTransformer;
import apicross.demo.myspace.app.dto.CmGetCompetitionResponse;
import apicross.demo.myspace.domain.Competition;

public class GetCompetitionResponseViewAssembler implements QueryResultTransformer<Competition, CmGetCompetitionResponse> {
    @Override
    public CmGetCompetitionResponse transform(Competition source) {
        return null;
    }
}
