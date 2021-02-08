package apicross.demoapp.storefront.ports.adapters.web;

import apicross.demoapp.storefront.app.dto.SfCompetitionsShortDescriptionRepresentationModel;
import apicross.demoapp.storefront.domain.Competition;
import apicross.demo.common.models.ModelTransformer;

public class SfCompetitionShortDescriptionViewAssembler implements ModelTransformer<Competition, SfCompetitionsShortDescriptionRepresentationModel> {
    @Override
    public SfCompetitionsShortDescriptionRepresentationModel transform(Competition source) {
        // TODO: implement me!!
        throw new UnsupportedOperationException("not implemented yet");
    }
}
