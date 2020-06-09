package apicross.demoapp.storefront.ports.adapters.web;

import apicross.demoapp.storefront.app.dto.SfCompetitionsShortDescription;
import apicross.demoapp.storefront.domain.Competition;
import apicross.demo.common.models.QueryResultTransformer;

public class SfCompetitionShortDescriptionViewAssembler implements QueryResultTransformer<Competition, SfCompetitionsShortDescription> {
    @Override
    public SfCompetitionsShortDescription transform(Competition source) {
        // TODO: implement me!!
        throw new UnsupportedOperationException("not implemented yet");
    }
}
