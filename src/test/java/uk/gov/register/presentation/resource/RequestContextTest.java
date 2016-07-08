package uk.gov.register.presentation.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.register.presentation.config.RegistersConfiguration;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestContextTest {
    @Mock
    private HttpServletRequest httpServletRequest;

    @Test
    public void takesRegisterNameFromHttpHost() throws Exception {
        RequestContext requestContext = new RequestContext(new RegistersConfiguration(Optional.empty()), new EmptyRegisterDomainConfiguration());
        requestContext.httpServletRequest = httpServletRequest;
        when(httpServletRequest.getHeader("Host")).thenReturn("school.beta.openregister.org");

        String registerPrimaryKey = requestContext.getRegisterPrimaryKey();

        assertThat(registerPrimaryKey, equalTo("school"));
    }

    @Test
    public void behavesGracefullyWhenGivenHostWithNoDots() throws Exception {
        RequestContext requestContext = new RequestContext(new RegistersConfiguration(Optional.empty()), new EmptyRegisterDomainConfiguration());
        requestContext.httpServletRequest = httpServletRequest;
        when(httpServletRequest.getHeader("Host")).thenReturn("school");

        String registerPrimaryKey = requestContext.getRegisterPrimaryKey();

        assertThat(registerPrimaryKey, equalTo("school"));
    }

    @Test
    public void resourceExtension_returnsTheResourceExtensionIfExists() {
        RequestContext requestContext = new RequestContext(new RegistersConfiguration(Optional.empty()), new EmptyRegisterDomainConfiguration());
        requestContext.httpServletRequest = httpServletRequest;
        when(httpServletRequest.getRequestURI()).thenReturn("/foo/bar.json");

        assertThat(requestContext.resourceExtension(), equalTo(Optional.of("json")));
    }

    @Test
    public void resourceExtension_returnsEmptyIfResourceExtensionIsNotExists() {
        RequestContext requestContext = new RequestContext(new RegistersConfiguration(Optional.empty()), new EmptyRegisterDomainConfiguration());
        requestContext.httpServletRequest = httpServletRequest;
        when(httpServletRequest.getRequestURI()).thenReturn("/foo/bar");

        assertThat(requestContext.resourceExtension(), equalTo(Optional.empty()));
    }

}
