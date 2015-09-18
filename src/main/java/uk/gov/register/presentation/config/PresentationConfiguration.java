package uk.gov.register.presentation.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public class PresentationConfiguration extends Configuration implements RegisterDomainConfiguration {
    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database;

    @Valid
    @JsonProperty
    private String registerDomain;

    public DataSourceFactory getDatabase() {
        return database;
    }

    @Override
    public String getRegisterDomain() {
        return Optional.ofNullable(registerDomain).orElse("beta.openregister.org");
    }
}
