package uk.gov.register.presentation.resource;

import uk.gov.register.presentation.config.RegisterDomainConfiguration;

public class EmptyRegisterDomainConfiguration implements RegisterDomainConfiguration {
    @Override
    public String getRegisterDomain() { return ""; }

    @Override
    public String getRegisterScheme() { return ""; }
}
