package uk.gov.register.presentation.resource;

import com.google.common.base.Optional;
import uk.gov.register.presentation.Entry;
import uk.gov.register.presentation.app.RegisterProvider;
import uk.gov.register.presentation.representations.ExtraMediaType;
import uk.gov.register.presentation.view.ListResultView;
import uk.gov.register.presentation.view.SingleResultView;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;


@Path("/")
public class SearchResource extends ResourceBase {

    @Inject
    public SearchResource(RegisterProvider registerProvider) {
        super(registerProvider);
    }

    @GET
    @Path("search")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, ExtraMediaType.TEXT_CSV, ExtraMediaType.TEXT_TSV, ExtraMediaType.TEXT_TTL})
    public ListResultView search(@Context UriInfo uriInfo) {
        final MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

        return new ListResultView("/templates/entries.mustache",
                queryParameters.entrySet()
                        .stream()
                        .findFirst()
                        .map(e -> queryDAO.findAllByKeyValue(e.getKey(), e.getValue().get(0)))
                        .orElseGet(() -> queryDAO.getAllEntries(getRegisterPrimaryKey(), ENTRY_LIMIT)));
    }

    @GET
    @Path("/{primaryKey}/{primaryKeyValue}")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, ExtraMediaType.TEXT_TTL})
    public SingleResultView findByPrimaryKey(@PathParam("primaryKey") String key, @PathParam("primaryKeyValue") String value) {
        String registerPrimaryKey = getRegisterPrimaryKey();
        if (key.equals(registerPrimaryKey)) {
            Optional<Entry> entry = queryDAO.findByKeyValue(key, value);
            if (entry.isPresent()) {
                return new SingleResultView("/templates/entry.mustache", entry.get());
            }
        }

        throw new NotFoundException();
    }

    @GET
    @Path("/hash/{hash}")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, ExtraMediaType.TEXT_TTL})
    public SingleResultView findByHash(@PathParam("hash") String hash) {
        Optional<Entry> entry = queryDAO.findByHash(hash);
        if (entry.isPresent()) {
            return new SingleResultView("/templates/entry.mustache", entry.orNull());
        }
        throw new NotFoundException();
    }
}
