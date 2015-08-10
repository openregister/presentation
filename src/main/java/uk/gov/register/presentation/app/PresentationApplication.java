package uk.gov.register.presentation.app;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jetty.MutableServletContextHandler;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.dropwizard.views.mustache.MustacheViewRenderer;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.ServerProperties;
import uk.gov.register.presentation.config.PresentationConfiguration;
import uk.gov.register.presentation.dao.RecentEntryIndexQueryDAO;
import uk.gov.register.presentation.dao.RecentEntryIndexUpdateDAO;
import uk.gov.register.presentation.representations.CsvWriter;
import uk.gov.register.presentation.representations.ExtraMediaType;
import uk.gov.register.presentation.representations.TsvWriter;
import uk.gov.register.presentation.representations.TurtleWriter;
import uk.gov.register.presentation.resource.DataResource;
import uk.gov.register.presentation.resource.HomePageResource;
import uk.gov.register.presentation.resource.SearchResource;
import uk.gov.register.presentation.representations.TurtleWriter;

import javax.servlet.DispatcherType;
import javax.ws.rs.core.MediaType;
import java.util.EnumSet;

public class PresentationApplication extends Application<PresentationConfiguration> {

    public static void main(String[] args) throws Exception {
        new PresentationApplication().run(args);
    }

    @Override
    public String getName() {
        return "presentation";
    }

    @Override
    public void initialize(Bootstrap<PresentationConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<>(ImmutableList.of(new MustacheViewRenderer())));
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                ));
        bootstrap.addBundle(new AssetsBundle("/assets"));
    }

    @Override
    public void run(PresentationConfiguration configuration, Environment environment) throws Exception {
        DropwizardResourceConfig resourceConfig = environment.jersey().getResourceConfig();
        resourceConfig.register(new ApplicationBinder(configuration, environment));
        resourceConfig.packages("uk.gov.register.presentation.resource");

        resourceConfig.property(ServerProperties.MEDIA_TYPE_MAPPINGS, ImmutableMap.of(
                "csv", ExtraMediaType.TEXT_CSV_TYPE,
                "tsv", ExtraMediaType.TEXT_TSV_TYPE,
                "ttl", ExtraMediaType.TEXT_TTL_TYPE,
                "json", MediaType.APPLICATION_JSON_TYPE));
        environment.jersey().register(new CsvWriter());
        environment.jersey().register(new TsvWriter());
        environment.jersey().register(new TurtleWriter());

        MutableServletContextHandler applicationContext = environment.getApplicationContext();

        setCorsPreflight(applicationContext);
    }

    private void setCorsPreflight(MutableServletContextHandler applicationContext) {
        FilterHolder filterHolder = applicationContext
                .addFilter(CrossOriginFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
    }
}
