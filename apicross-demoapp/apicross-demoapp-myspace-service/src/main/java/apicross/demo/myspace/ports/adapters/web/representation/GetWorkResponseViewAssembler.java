package apicross.demo.myspace.ports.adapters.web.representation;

import apicross.demo.common.models.ModelConverter;
import apicross.demo.myspace.app.dto.RpmWpGetWorkResponse;
import apicross.demo.myspace.domain.Work;

public class GetWorkResponseViewAssembler implements ModelConverter<Work, RpmWpGetWorkResponse> {
    @Override
    public RpmWpGetWorkResponse convert(Work source) {
        return new RpmWpGetWorkResponse()
                .withAuthor(source.getAuthor())
                .withDescription(source.getDescription())
                .withTitle(source.getTitle())
                .withStatus(source.getStatus().toString()) // TODO: encode according API specification
                .withDatePlaced(source.getPlacedAt());
    }
}
