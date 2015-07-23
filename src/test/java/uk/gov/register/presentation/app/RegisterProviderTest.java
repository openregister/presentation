package uk.gov.register.presentation.app;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.skife.jdbi.v2.DBI;
import uk.gov.register.presentation.dao.RecentEntryIndexQueryDAO;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegisterProviderTest {

    @Mock
    DBProvider dbProvider;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    DBI dbi;

    @Mock
    RecentEntryIndexQueryDAO recentEntryIndexQueryDAO;

    RegisterProvider registerProvider;

    @Before
    public void setup() throws Exception {
        registerProvider = new RegisterProvider(dbProvider);
        registerProvider.httpServletRequest = httpServletRequest;
    }
    @Test
    public void provide_multipleTimesReturnsSameRegisterObjectFromCacheWhichWasStoredOnFirstRequestForARegister() {


        stub(dbProvider.getJdbi(anyString())).toReturn(dbi);
        stub(dbi.onDemand(anyObject())).toReturn(recentEntryIndexQueryDAO);

        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://school.openregister.org/school/value"));
        Register schoolRegister = registerProvider.provide();

        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://address.openregister.org/foo"));
        Register addressRegister = registerProvider.provide();

        assertThat(schoolRegister.getName(), equalTo("school"));
        assertThat(addressRegister.getName(), equalTo("address"));

        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://address.openregister.org/foo"));
        Register addressRegisterRetrievedAgain = registerProvider.provide();

        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://school.openregister.org/foo"));
        Register schoolRegisterRetrievedAgain = registerProvider.provide();


        assertThat(addressRegister, sameInstance(addressRegisterRetrievedAgain));
        assertThat(schoolRegister, sameInstance(schoolRegisterRetrievedAgain));

    }
}