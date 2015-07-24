package uk.gov.register.presentation.functional;

import com.google.common.collect.ImmutableList;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TurtleRepresentationTest extends FunctionalTestBase {
    @BeforeClass
    public static void publishTestMessages() {
        publishMessagesToDB(ImmutableList.of(
                "{\"hash\":\"someHash1\",\"entry\":{\"name\":\"The Entry 1\", \"key1\":\"value1\", \"ft_presentation\":\"12345\"}}",
                "{\"hash\":\"someHash2\",\"entry\":{\"name\":\"The Entry 2\", \"key1\":\"value2\", \"ft_presentation\":\"67890\"}}"
        ));
    }

    public static final String EXPECTED_SINGLE_RECORD = "<http://localhost:9000/hash/someHash1>;\n" +
            " key1 \"value1\" ;\n" +
            " name \"The Entry 1\" ;\n" +
            " ft_presentation \"12345\" .\n";


    public static final String TEXT_TURTLE = "text/turtle;charset=utf-8";

    @Test
    public void turtleRepresentationIsSupportedForSingleEntryView() {
        Response response = getRequest("/hash/someHash1.ttl");

        assertThat(response.getHeaderString("Content-Type"), equalTo(TEXT_TURTLE));
        assertThat(response.readEntity(String.class), equalTo(EXPECTED_SINGLE_RECORD));
    }

    public static final String EXPECTED_LIST_RECORDS =
            "<http://localhost:9000/hash/someHash2>;\n" +
                    " key1 \"value2\" ;\n" +
                    " name \"The Entry 2\" ;\n" +
                    " ft_presentation \"67890\" .\n"
                    + EXPECTED_SINGLE_RECORD;

    @Test
    public void turtleRepresentationIsSupportedForListEntryView() {
        Response response = getRequest("/all.ttl");

        assertThat(response.getHeaderString("Content-Type"), equalTo(TEXT_TURTLE));
        assertThat(response.readEntity(String.class), equalTo(EXPECTED_LIST_RECORDS));
    }
}
