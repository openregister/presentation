package uk.gov.register.presentation.resource;

import com.fasterxml.jackson.databind.JsonNode;
import io.dropwizard.views.View;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.slf4j.LoggerFactory;
import uk.gov.register.presentation.DbEntry;
import uk.gov.register.presentation.dao.RecentEntryIndexQueryDAO;
import uk.gov.register.presentation.representations.ExtraMediaType;
import uk.gov.register.presentation.view.EntryListView;
import uk.gov.register.presentation.view.ViewFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Path("/")
public class DataResource {
    protected final RequestContext requestContext;
    private final RecentEntryIndexQueryDAO queryDAO;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ViewFactory viewFactory;

    @Inject
    public DataResource(ViewFactory viewFactory, RequestContext requestContext, RecentEntryIndexQueryDAO queryDAO) {
        this.viewFactory = viewFactory;
        this.requestContext = requestContext;
        this.queryDAO = queryDAO;
    }

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadRegister(@QueryParam(Pagination.INDEX_PARAM) Optional<Long> pageIndex, @QueryParam(Pagination.SIZE_PARAM) Optional<Long> pageSize) {
        List<DbEntry> entries = queryDAO.getAllEntriesNoPagination();

        StreamingOutput stream = output -> {
            ZipOutputStream zos = new ZipOutputStream(output);

            ZipEntry ze = new ZipEntry("register.txt");
            zos.putNextEntry(ze);
            zos.write("This will contain the /register data in JSON".getBytes());
            zos.closeEntry();

            ze = new ZipEntry("proof.txt");
            zos.putNextEntry(ze);
            zos.write("Unsure what this contains".getBytes());
            zos.closeEntry();

            entries.forEach(singleEntry -> {
                JsonNode entryData = singleEntry.getContent().getContent();
                ZipEntry singleZipEntry = new ZipEntry(String.format("item/%s.json", singleEntry.getSerialNumber()));
                try {
                    zos.putNextEntry(singleZipEntry);
                    zos.write(entryData.toString().getBytes(StandardCharsets.UTF_8));
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            zos.flush();
            zos.close();
        };

        return Response
                .ok(stream)
                .header("Content-Disposition", String.format("attachment; filename=%s.zip", requestContext.getRegisterPrimaryKey()))
                .header("Content-Transfer-Encoding", "binary")
                .build();
    }

    @GET
    @Path("/entries")
    @Produces({ExtraMediaType.TEXT_HTML, MediaType.APPLICATION_JSON, ExtraMediaType.TEXT_YAML, ExtraMediaType.TEXT_CSV, ExtraMediaType.TEXT_TSV, ExtraMediaType.TEXT_TTL})
    public EntryListView entries(@QueryParam(Pagination.INDEX_PARAM) Optional<Long> pageIndex, @QueryParam(Pagination.SIZE_PARAM) Optional<Long> pageSize) {
        Pagination pagination = new Pagination(pageIndex, pageSize, queryDAO.getTotalEntries());

        setNextAndPreviousPageLinkHeader(pagination);

        getFileExtension().ifPresent(ext -> addContentDispositionHeader(requestContext.getRegisterPrimaryKey() + "-entries." + ext));
        return viewFactory.getEntriesView(queryDAO.getAllEntries(pagination.pageSize(), pagination.offset()), pagination);
    }

    @GET
    @Path("/records")
    @Produces({ExtraMediaType.TEXT_HTML, MediaType.APPLICATION_JSON, ExtraMediaType.TEXT_YAML, ExtraMediaType.TEXT_CSV, ExtraMediaType.TEXT_TSV, ExtraMediaType.TEXT_TTL})
    public EntryListView records(@QueryParam(Pagination.INDEX_PARAM) Optional<Long> pageIndex, @QueryParam(Pagination.SIZE_PARAM) Optional<Long> pageSize) {
        Pagination pagination = new Pagination(pageIndex, pageSize, queryDAO.getTotalRecords());

        setNextAndPreviousPageLinkHeader(pagination);

        getFileExtension().ifPresent(ext -> addContentDispositionHeader(requestContext.getRegisterPrimaryKey() + "-records." + ext));
        return viewFactory.getRecordsView(queryDAO.getLatestEntriesOfRecords(pagination.pageSize(), pagination.offset()), pagination);
    }

    private Optional<String> getFileExtension() {
        String requestURI = requestContext.getHttpServletRequest().getRequestURI();
        if (requestURI.lastIndexOf('.') == -1) {
            return Optional.empty();
        }
        String[] tokens = requestURI.split("\\.");
        return Optional.of(tokens[tokens.length - 1]);
    }

    private void addContentDispositionHeader(String fileName) {
        ContentDisposition contentDisposition = ContentDisposition.type("attachment").fileName(fileName).build();
        requestContext.getHttpServletResponse().addHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
    }

    private void setNextAndPreviousPageLinkHeader(Pagination pagination) {
        List<String> headerValues = new ArrayList<>();

        if (pagination.hasNextPage()) {
            headerValues.add(String.format("<%s>; rel=\"%s\"", pagination.getNextPageLink(), "next"));
        }

        if (pagination.hasPreviousPage()) {
            headerValues.add(String.format("<%s>; rel=\"%s\"", pagination.getPreviousPageLink(), "previous"));
        }

        if (!headerValues.isEmpty()) {
            requestContext.getHttpServletResponse().setHeader("Link", String.join(",", headerValues));
        }
    }

    private Response create301Response(String path, Optional<Long> pageIndex, Optional<Long> pageSize) {
        String requestURI = requestContext.requestURI();
        String representation = requestURI.substring(requestURI.lastIndexOf("/")).replaceAll("[^\\.]+(.*)", "$1");

        UriBuilder builder = UriBuilder
                .fromUri(requestURI)
                .replacePath(null)
                .path(path + representation);

        if (pageIndex.isPresent()) {
            builder = builder.queryParam(Pagination.INDEX_PARAM, pageIndex.get());
        }
        if (pageSize.isPresent()) {
            builder = builder.queryParam(Pagination.SIZE_PARAM, pageSize.get());
        }

        return Response
                .status(Response.Status.MOVED_PERMANENTLY)
                .location(builder.build())
                .build();
    }

}
