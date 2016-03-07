package uk.gov.register.presentation.functional;

import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.register.presentation.functional.testSupport.DBSupport;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class EntriesResourceWithDataFunctionalTest extends FunctionalTestBase {
    @BeforeClass
    public static void publishTestMessages() {
        DBSupport.publishMessages(ImmutableList.of(
                "{\"hash\":\"hash1\",\"entry\":{\"name\":\"ellis\",\"address\":\"12345\"}}",
                "{\"hash\":\"hash2\",\"entry\":{\"name\":\"presley\",\"address\":\"6789\"}}",
                "{\"hash\":\"hash3\",\"entry\":{\"name\":\"ellis\",\"address\":\"145678\"}}",
                "{\"hash\":\"hash4\",\"entry\":{\"name\":\"updatedEllisName\",\"address\":\"145678\"}}",
                "{\"hash\":\"hash5\",\"entry\":{\"name\":\"ellis\",\"address\":\"6789\"}}"
        ));
    }

    @Test
    public void entriesPageHasXhtmlLangAttributes() throws Throwable {
        Response response = getRequest("address", "/entries");

        Document doc = Jsoup.parse(response.readEntity(String.class));
        Elements htmlElement = doc.select("html");
        assertThat(htmlElement.size(), equalTo(1));
        assertThat(htmlElement.first().attr("lang"), equalTo("en"));
        assertThat(htmlElement.first().attr("xml:lang"), equalTo("en"));
    }

    @Test
    public void entriesTableHasCaption() {
        Response response = getRequest("address", "/entries");

        Document doc = Jsoup.parse(response.readEntity(String.class));
        Elements htmlElement = doc.select("table > caption");
        assertThat(htmlElement.size(), equalTo(1));
        assertThat(htmlElement.first().text(), equalTo("5 entries"));
    }
}
