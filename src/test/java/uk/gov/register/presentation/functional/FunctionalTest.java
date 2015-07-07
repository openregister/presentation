package uk.gov.register.presentation.functional;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.register.presentation.PresentationApplication;
import uk.gov.register.presentation.PresentationConfiguration;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

public class FunctionalTest {
    public static final String TOPIC = "register";

    private static final TestKafkaCluster testKafkaCluster = new TestKafkaCluster(TOPIC);

    @ClassRule
    public static final DropwizardAppRule<PresentationConfiguration> RULE =
            new DropwizardAppRule<>(PresentationApplication.class,
                    ResourceHelpers.resourceFilePath("test-app-config.yaml"),
                    ConfigOverride.config("zookeeperServer", "localhost:" + testKafkaCluster.getZkPort()));
    private final Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");

    @Test
    public void shouldConsumeMessageFromKafkaAndShowAsLatest() throws Exception {
        String message = "{\"foo\":\"bar\"}";
        testKafkaCluster.getProducer().send(new ProducerRecord<>(TOPIC, message.getBytes()));
        waitForAppToConsumeMessage();

        Response response = client.target(String.format("http://localhost:%d/latest.json", RULE.getLocalPort())).request().get();

        assertThat(response.readEntity(String.class), equalTo("[{\"foo\":\"bar\"}]"));

    }

    @Test
    public void appSupportsCORS() {
        String origin = "http://originfortest.com";
        Response response = client.target(String.format("http://localhost:%d/latest.json", RULE.getLocalPort()))
                .request()
                .header(HttpHeaders.ORIGIN, origin)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "X-Requested-With")
                .options();


        MultivaluedMap<String, Object> headers = response.getHeaders();

        assertThat(headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN), is(ImmutableList.of(origin)));
        assertThat(headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS), is(ImmutableList.of("true")));
        assertNotNull(headers.get(HttpHeaders.ACCESS_CONTROL_MAX_AGE));
        assertThat(headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS), is(ImmutableList.of("OPTIONS,GET,PUT,POST,DELETE,HEAD")));
        assertThat(headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS), is(ImmutableList.of("X-Requested-With,Content-Type,Accept,Origin")));
    }

    private void waitForAppToConsumeMessage() throws InterruptedException {
        Thread.sleep(3000);
    }

}
