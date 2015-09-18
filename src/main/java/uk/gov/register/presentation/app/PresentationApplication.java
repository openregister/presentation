package uk.gov.register.presentation.app;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.java8.jdbi.DBIFactory;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.MutableServletContextHandler;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ServerProperties;
import org.skife.jdbi.v2.DBI;
import uk.gov.register.presentation.ContentSecurityPolicyFilter;
import uk.gov.register.presentation.EntryConverter;
import uk.gov.register.presentation.config.FieldsConfiguration;
import uk.gov.register.presentation.config.PresentationConfiguration;
import uk.gov.register.presentation.dao.RecentEntryIndexQueryDAO;
import uk.gov.register.presentation.representations.CsvWriter;
import uk.gov.register.presentation.representations.ExtraMediaType;
import uk.gov.register.presentation.representations.TsvWriter;
import uk.gov.register.presentation.representations.TurtleWriter;
import uk.gov.register.presentation.representations.YamlWriter;
import uk.gov.register.presentation.resource.NotFoundExceptionMapper;
import uk.gov.register.presentation.resource.RequestContext;
import uk.gov.register.presentation.resource.ThrowableExceptionMapper;
import uk.gov.register.presentation.view.ViewFactory;
import uk.gov.register.thymeleaf.ThymeleafViewRenderer;

import javax.inject.Singleton;
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
        bootstrap.addBundle(new ViewBundle<>(ImmutableList.of(new ThymeleafViewRenderer("HTML5", "/templates/", ".html", false))));
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                ));
        bootstrap.addBundle(new AssetsBundle("/assets"));
    }

    @Override
    public void run(PresentationConfiguration configuration, Environment environment) throws Exception {
        DBIFactory dbiFactory = new DBIFactory();
        DBI jdbi = dbiFactory.build(environment, configuration.getDatabase(), "postgres");
        RecentEntryIndexQueryDAO queryDAO = jdbi.onDemand(RecentEntryIndexQueryDAO.class);

        JerseyEnvironment jerseyEnvironment = environment.jersey();
        DropwizardResourceConfig resourceConfig = jerseyEnvironment.getResourceConfig();

        ImmutableMap<String, MediaType> representations = ImmutableMap.of(
                "csv", ExtraMediaType.TEXT_CSV_TYPE,
                "tsv", ExtraMediaType.TEXT_TSV_TYPE,
                "ttl", ExtraMediaType.TEXT_TTL_TYPE,
                "json", MediaType.APPLICATION_JSON_TYPE,
                "yaml", ExtraMediaType.TEXT_YAML_TYPE
        );
        resourceConfig.property(ServerProperties.MEDIA_TYPE_MAPPINGS, representations);

        jerseyEnvironment.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(configuration);
                bind(queryDAO).to(RecentEntryIndexQueryDAO.class);
                bind(FieldsConfiguration.class).to(FieldsConfiguration.class).in(Singleton.class);
                bind(RequestContext.class).to(RequestContext.class);
                bind(ViewFactory.class).to(ViewFactory.class).in(Singleton.class);
                bind(EntryConverter.class).to(EntryConverter.class).in(Singleton.class);
            }
        });

        resourceConfig.packages("uk.gov.register.presentation.resource");

        jerseyEnvironment.register(CsvWriter.class);
        jerseyEnvironment.register(TsvWriter.class);
        jerseyEnvironment.register(TurtleWriter.class);
        jerseyEnvironment.register(YamlWriter.class);

        jerseyEnvironment.register(NotFoundExceptionMapper.class);
        jerseyEnvironment.register(ThrowableExceptionMapper.class);

        MutableServletContextHandler applicationContext = environment.getApplicationContext();

        setCorsPreflight(applicationContext);
        jerseyEnvironment.register(ContentSecurityPolicyFilter.class);
    }

    private void setCorsPreflight(MutableServletContextHandler applicationContext) {
        FilterHolder filterHolder = applicationContext
                .addFilter(CrossOriginFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
    }
}
