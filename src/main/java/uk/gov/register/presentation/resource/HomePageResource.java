package uk.gov.register.presentation.resource;

import io.dropwizard.views.View;
import uk.gov.register.presentation.dao.RecentEntryIndexQueryDAO;
import uk.gov.register.presentation.representations.ExtraMediaType;
import uk.gov.register.presentation.view.ViewFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/")
public class HomePageResource {
    private final ViewFactory viewFactory;
    private final RecentEntryIndexQueryDAO recentEntryIndexQueryDAO;

    @Inject
    public HomePageResource(ViewFactory viewFactory, RecentEntryIndexQueryDAO recentEntryIndexQueryDAO) {
        this.viewFactory = viewFactory;
        this.recentEntryIndexQueryDAO = recentEntryIndexQueryDAO;
    }

    @GET
    @Produces({ExtraMediaType.TEXT_HTML})
    public View home() {
        return viewFactory.homePageView(recentEntryIndexQueryDAO.getTotalRecords(), recentEntryIndexQueryDAO.getTotalEntries(), recentEntryIndexQueryDAO.getLastUpdatedTime());
    }

    @GET
    @Path("/robots.txt")
    @Produces(MediaType.TEXT_PLAIN)
    public String robots() {
        return "User-agent: *\n" +
                "Disallow: /\n";
    }

    @GET
    @Path("/favicon.ico")
    public Response favicon() {
        return Response.temporaryRedirect(UriBuilder.fromUri("/assets/images/favicon.ico").build()).build();
    }
}
