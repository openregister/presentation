package uk.gov.register.presentation.representations;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import uk.gov.register.presentation.FieldValue;
import uk.gov.register.presentation.LinkValue;
import uk.gov.register.presentation.EntryView;
import uk.gov.register.presentation.StringValue;
import uk.gov.register.presentation.resource.RequestContext;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class TurtleWriterTest {

    private RequestContext requestContext = new RequestContext() {
        @Override
        public String requestUrl() {
            return "http://widget.openregister.org/widget/123";
        }
    };

    private TurtleWriter turtleWriter;

    @Before
    public void setUp() throws Exception {
        turtleWriter = new TurtleWriter(requestContext);
    }

    @Test
    public void rendersLinksCorrectlyAsUrls() throws Exception {
        Map<String, FieldValue> entryMap =
                ImmutableMap.of(
                        "registered-address", new LinkValue("address", "1111111", "openregister.org"),
                        "name", new StringValue("foo")
                );

        EntryView entry = new EntryView(52, "abcd", "registerName", entryMap);

        TestOutputStream entityStream = new TestOutputStream();

        turtleWriter.writeEntriesTo(entityStream, Collections.singletonList(entry));


        assertThat(entityStream.contents, containsString("field:registered-address <http://address.openregister.org/address/1111111>"));
        assertThat(entityStream.contents, containsString("field:name \"foo\""));
    }
}
