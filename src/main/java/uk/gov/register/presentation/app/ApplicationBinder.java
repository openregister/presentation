package uk.gov.register.presentation.app;

import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import uk.gov.register.presentation.config.PresentationConfiguration;

class ApplicationBinder extends AbstractBinder {
    private final PresentationConfiguration configuration;
    private final Environment environment;

    public ApplicationBinder(PresentationConfiguration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    @Override
    protected void configure() {
        bind(environment).to(Environment.class);
        bind(configuration).to(PresentationConfiguration.class);
        bind(DBProvider.class).to(DBProvider.class);
        bind(RegisterProvider.class).to(RegisterProvider.class);
    }
}
