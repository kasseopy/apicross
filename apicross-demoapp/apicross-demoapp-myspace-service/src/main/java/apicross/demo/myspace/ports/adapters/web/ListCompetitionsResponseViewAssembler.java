package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.common.models.QueryResultTransformer;
import apicross.demo.myspace.app.dto.CmListCompetitionsResponse;
import apicross.demo.myspace.domain.Competition;

import java.util.List;

public class ListCompetitionsResponseViewAssembler implements QueryResultTransformer<List<Competition>, CmListCompetitionsResponse> {
    @Override
    public CmListCompetitionsResponse transform(List<Competition> source) {
        return null;
    }
}
