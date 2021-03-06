package uk.gov.register.presentation.representations.turtle;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import uk.gov.register.presentation.representations.ExtraMediaType;
import uk.gov.register.presentation.resource.RequestContext;
import uk.gov.register.presentation.view.EntryListView;
import uk.gov.register.presentation.view.EntryView;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

@Provider
@Produces(ExtraMediaType.TEXT_TTL)
public class EntryListTurtleWriter extends TurtleRepresentationWriter<EntryListView> {

    @Inject
    public EntryListTurtleWriter(RequestContext requestContext) {
        super(requestContext);
    }

    @Override
    protected Model rdfModelFor(EntryListView view) {
        Model model = ModelFactory.createDefaultModel();
        for (EntryView entryView : view.getEntries().stream().map(e -> new EntryView(requestContext, view.getCustodian(), view.getBranding(), e)).collect(Collectors.toList())) {
            model.add(new EntryTurtleWriter(requestContext).rdfModelFor(entryView));
        }
        return model;
    }
}
