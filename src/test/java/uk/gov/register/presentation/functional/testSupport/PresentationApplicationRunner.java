package uk.gov.register.presentation.functional.testSupport;

import io.dropwizard.testing.ResourceHelpers;
import uk.gov.register.presentation.app.PresentationApplication;
import uk.gov.register.presentation.functional.FunctionalTestBase;

public class PresentationApplicationRunner {
    public static final String DATABASE_URL = FunctionalTestBase.DATABASE_URL;
    public static final String TOPIC = FunctionalTestBase.REGISTER_NAME;

    private static final TestKafkaCluster testKafkaCluster = new TestKafkaCluster(TOPIC);

    public static void main(String[] args) throws Throwable {
        System.setProperty("dw.zookeeperServer", "localhost:" + testKafkaCluster.getZkPort());
        System.setProperty("dw.database.url", DATABASE_URL);

        String arg1= ResourceHelpers.resourceFilePath("test-app-config.yaml");
        String[] a = {"server", arg1};

        PresentationApplication.main(a);
        Thread.currentThread().join();
    }
}
