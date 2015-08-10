package uk.gov.register.presentation.resource;

import uk.gov.register.presentation.app.RegisterProvider;
import uk.gov.register.presentation.representations.ExtraMediaType;
import uk.gov.register.presentation.view.ListResultView;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class DataResource extends ResourceBase {
    @Inject
    public DataResource(RegisterProvider registerProvider) {
        super(registerProvider);
    }

    @GET
    @Path("/feed")
    @Produces({MediaType.APPLICATION_JSON, ExtraMediaType.TEXT_CSV, ExtraMediaType.TEXT_TSV, ExtraMediaType.TEXT_TTL})
    public ListResultView feed() {
        return new ListResultView("/templates/entries.mustache", queryDAO.getFeeds(ENTRY_LIMIT));
    }

    @GET
    @Path("/all")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, ExtraMediaType.TEXT_CSV, ExtraMediaType.TEXT_TSV, ExtraMediaType.TEXT_TTL})
    public ListResultView all() {
        return new ListResultView("/templates/entries.mustache", queryDAO.getAllEntries(getRegisterPrimaryKey(), ENTRY_LIMIT));
    }

}
