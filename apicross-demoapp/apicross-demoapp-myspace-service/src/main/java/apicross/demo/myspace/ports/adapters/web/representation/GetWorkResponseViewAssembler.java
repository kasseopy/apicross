package apicross.demo.myspace.ports.adapters.web.representation;

import apicross.demo.common.models.ModelTransformer;
import apicross.demo.myspace.app.dto.RpmWpGetWorkResponse;
import apicross.demo.myspace.domain.Work;

public class GetWorkResponseViewAssembler implements ModelTransformer<Work, RpmWpGetWorkResponse> {
    @Override
    public RpmWpGetWorkResponse transform(Work source) {
        return new RpmWpGetWorkResponse()
                .author(source.getAuthor())
                .description(source.getDescription())
                .title(source.getTitle())
                .status(source.getStatus().toString())
                .datePlaced(source.getPlacedAt());
    }
}
