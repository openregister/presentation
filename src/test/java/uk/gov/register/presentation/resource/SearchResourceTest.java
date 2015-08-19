package uk.gov.register.presentation.resource;

import com.google.common.base.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.register.presentation.Record;
import uk.gov.register.presentation.dao.RecentEntryIndexQueryDAO;
import uk.gov.register.presentation.representations.ExtraMediaType;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchResourceTest {
    @Mock
    RecentEntryIndexQueryDAO queryDAO;

    @Mock
    HttpServletRequest httpServletRequest;

    @Test
    public void findByPrimaryKey_throwsNotFoundException_whenSearchedKeyIsNotPrimaryKeyOfRegister() {
        SearchResource resource = new SearchResource(queryDAO);
        resource.httpServletRequest = httpServletRequest;

        when(httpServletRequest.getHeader("Host")).thenReturn("localhost");
        try {
            resource.findByPrimaryKey("someOtherKey", "value");
            fail("Must fail");
        } catch (NotFoundException e) {
            //success
        }
    }

    @Test
    public void findByPrimaryKey_throwsNotFoundException_whenSearchedKeyIsNotFound() {
        SearchResource resource = new SearchResource(queryDAO);
        resource.httpServletRequest = httpServletRequest;

        when(httpServletRequest.getHeader("Host")).thenReturn("school.openregister.org");
        when(queryDAO.findByKeyValue("school", "value")).thenReturn(Optional.<Record>absent());
        try {
            resource.findByPrimaryKey("school", "value");
            fail("Must fail");
        } catch (NotFoundException e) {
            //success
        }
    }


    @Test
    public void findByHash_throwsNotFoundWhenHashIsNotFound() {
        SearchResource resource = new SearchResource(queryDAO);
        resource.httpServletRequest = httpServletRequest;

        when(httpServletRequest.getHeader("Host")).thenReturn("school.openregister.org");
        when(queryDAO.findByHash("123")).thenReturn(Optional.<Record>absent());
        try {
            resource.findByHash("123");
            fail("Must fail");
        } catch (NotFoundException e) {
            //success
        }
    }

    @Test
    public void searchSupportsCsvTsvHtmlTurtleAndJson() throws Exception {
        Method searchMethod = SearchResource.class.getDeclaredMethod("search", UriInfo.class);
        List<String> declaredMediaTypes = asList(searchMethod.getDeclaredAnnotation(Produces.class).value());
        assertThat(declaredMediaTypes,
                hasItems(MediaType.TEXT_HTML,
                        MediaType.APPLICATION_JSON,
                        ExtraMediaType.TEXT_CSV,
                        ExtraMediaType.TEXT_TTL,
                        ExtraMediaType.TEXT_TSV));
    }

    @Test
    public void findByPrimaryKeySupportsTurtleHtmlAndJson() throws Exception {
        Method searchMethod = SearchResource.class.getDeclaredMethod("findByPrimaryKey", String.class, String.class);
        List<String> declaredMediaTypes = asList(searchMethod.getDeclaredAnnotation(Produces.class).value());
        assertThat(declaredMediaTypes,
                hasItems(MediaType.TEXT_HTML,
                         MediaType.APPLICATION_JSON,
                         ExtraMediaType.TEXT_TTL));
    }

    @Test
    public void findByHashSupportsTurtleHtmlAndJson() throws Exception {
        Method findByPrimaryKeyMethod = SearchResource.class.getDeclaredMethod("findByHash", String.class);
        List<String> declaredMediaTypes = asList(findByPrimaryKeyMethod.getDeclaredAnnotation(Produces.class).value());
        assertThat(declaredMediaTypes,
                hasItems(MediaType.TEXT_HTML,
                         MediaType.APPLICATION_JSON,
                         ExtraMediaType.TEXT_TTL));
    }
}
