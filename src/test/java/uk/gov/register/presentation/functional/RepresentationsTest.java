package uk.gov.register.presentation.functional;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static uk.gov.register.presentation.functional.TestEntry.anEntry;

@RunWith(Parameterized.class)
public class RepresentationsTest extends FunctionalTestBase {
    private static final String REGISTER_NAME = "register";
    private final String extension;
    private final String expectedContentType;
    private final String expectedItemValue;
    private final String expectedEntryValue;
    private final String expectedRecordValue;
    private final String expectedRecordsValue;
    private final String expectedEntriesValue;
    private final String expectedRecordEntriesValue;

    @Before
    public void publishTestMessages() {
        dbSupport.publishEntries(REGISTER_NAME, ImmutableList.of(
                anEntry(1, "{\"fields\":[\"field1\"],\"register\":\"value1\",\"text\":\"The Entry 1\"}",
                        Instant.parse("2016-03-01T01:02:03Z")),
                anEntry(2, "{\"fields\":[\"field1\",\"field2\"],\"register\":\"value2\",\"text\":\"The Entry 2\"}",
                        Instant.parse("2016-03-02T02:03:04Z"))
        ));
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"csv", "text/csv;charset=UTF-8"},
                {"tsv", "text/tab-separated-values;charset=UTF-8"},
                {"ttl", "text/turtle;charset=UTF-8"},
                {"json", "application/json"},
                {"yaml", "text/yaml;charset=UTF-8"}
        });
    }

    public RepresentationsTest(String extension, String expectedContentType) {
        this.extension = extension;
        this.expectedContentType = expectedContentType;
        this.expectedItemValue = fixture("fixtures/item." + extension);
        this.expectedEntryValue = fixture("fixtures/entry." + extension);
        this.expectedRecordValue = fixture("fixtures/record." + extension);
        this.expectedRecordsValue = fixture("fixtures/list." + extension);
        this.expectedEntriesValue = fixture("fixtures/entries." + extension);
        this.expectedRecordEntriesValue = fixture("fixtures/record-entries." + extension);
    }

    @Test
    public void representationIsSupportedForEntryResource() {
        assumeThat(expectedEntryValue, notNullValue());

        Response response = getRequest(REGISTER_NAME, "/entry/1." + extension);

        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getHeaderString("Content-Type"), equalTo(expectedContentType));
        assertThat(response.readEntity(String.class), equalTo(expectedEntryValue));
    }

    @Test
    public void representationIsSupportedForItemResource() {
        assumeThat(expectedItemValue, notNullValue());

        Response response = getRequest(REGISTER_NAME, "/item/sha-256:877d8bd1ab71dc6e48f64b4ca83c6d7bf645a1eb56b34d50fa8a833e1101eb18." + extension);

        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getHeaderString("Content-Type"), equalTo(expectedContentType));
        assertThat(response.readEntity(String.class), equalTo(expectedItemValue));
    }

    @Test
    public void representationIsSupportedForRecordResource() {
        assumeThat(expectedRecordValue, notNullValue());

        Response response = getRequest(REGISTER_NAME, "/record/value1." + extension);

        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getHeaderString("Content-Type"), equalTo(expectedContentType));
        assertThat(response.readEntity(String.class), equalTo(expectedRecordValue));
    }

    @Test
    public void representationIsSupportedForRecordsResource() {
        assumeThat(expectedRecordsValue, notNullValue());

        Response response = getRequest(REGISTER_NAME, "/records." + extension);

        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getHeaderString("Content-Type"), equalTo(expectedContentType));
        assertThat(response.readEntity(String.class), equalTo(expectedRecordsValue));
    }

    @Test
    public void representationIsSupportedForEntriesResource() {
        assumeThat(expectedEntriesValue, notNullValue());

        Response response = getRequest(REGISTER_NAME, "/entries." + extension);

        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getHeaderString("Content-Type"), equalTo(expectedContentType));
        assertThat(response.readEntity(String.class), equalTo(expectedEntriesValue));
    }

    @Test
    public void representationIsSupportedForRecordEntriesResource(){
        assumeThat(expectedRecordEntriesValue, notNullValue());

        Response response = getRequest(REGISTER_NAME, "/record/value1/entries." + extension);

        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getHeaderString("Content-Type"), equalTo(expectedContentType));
        assertThat(response.readEntity(String.class), equalTo(expectedRecordEntriesValue));
    }

    private static String fixture(String resourceName) {
        try {
            return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }
}
