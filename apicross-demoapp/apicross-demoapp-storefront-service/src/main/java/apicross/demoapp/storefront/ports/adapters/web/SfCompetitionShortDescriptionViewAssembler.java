package apicross.demoapp.storefront.ports.adapters.web;

import apicross.demoapp.storefront.app.dto.SfCompetitionsShortDescriptionRepresentationModel;
import apicross.demoapp.storefront.domain.Competition;
import apicross.demo.common.models.QueryResultTransformer;

public class SfCompetitionShortDescriptionViewAssembler implements QueryResultTransformer<Competition, SfCompetitionsShortDescriptionRepresentationModel> {
    @Override
    public SfCompetitionsShortDescriptionRepresentationModel transform(Competition source) {
        // TODO: implement me!!
        throw new UnsupportedOperationException("not implemented yet");
    }
}
