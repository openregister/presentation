package uk.gov.register.presentation.app;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import uk.gov.register.presentation.config.PresentationConfiguration;

import javax.inject.Inject;

class DBProvider {

    private final DBIFactory dbiFactory;

    private final Environment environment;

    private final PresentationConfiguration configuration;

    @Inject
    public DBProvider(Environment environment, PresentationConfiguration configuration) {
        this.environment = environment;
        this.configuration = configuration;
        this.dbiFactory = createDBIFactory();
    }

    public DBI getJdbi(String registerName) {
        DataSourceFactory database = configuration.getDatabase();
        database.setUrl(database.getUrl().replace("$REGISTER_NAME$", registerName));
        return dbiFactory.build(environment, database, "postgres");
    }

    protected DBIFactory createDBIFactory() {
        return new DBIFactory();
    }
}
