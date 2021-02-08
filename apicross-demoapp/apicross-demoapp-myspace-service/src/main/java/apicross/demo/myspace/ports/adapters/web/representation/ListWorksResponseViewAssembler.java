package apicross.demo.myspace.ports.adapters.web.representation;

import apicross.demo.common.models.ModelTransformer;
import apicross.demo.myspace.app.dto.RpmWorkSummary;
import apicross.demo.myspace.app.dto.RpmWpListWorksResponse;
import apicross.demo.myspace.domain.Work;

import java.util.List;
import java.util.stream.Collectors;

public class ListWorksResponseViewAssembler implements ModelTransformer<List<Work>, RpmWpListWorksResponse> {
    @Override
    public RpmWpListWorksResponse transform(List<Work> source) {
        return new RpmWpListWorksResponse()
                .works(transformWorks(source));
    }

    private List<RpmWorkSummary> transformWorks(List<Work> source) {
        return source.stream().map(work -> new RpmWorkSummary()
                .author(work.getAuthor())
                .title(work.getTitle())
                .placementDate(work.getPlacedAt())
                .status(work.getStatus().toString())).collect(Collectors.toList());
    }
}
