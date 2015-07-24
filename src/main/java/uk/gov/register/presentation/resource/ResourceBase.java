package uk.gov.register.presentation.resource;

import uk.gov.register.presentation.app.Register;
import uk.gov.register.presentation.app.RegisterProvider;
import uk.gov.register.presentation.dao.RecentEntryIndexQueryDAO;

public abstract class ResourceBase {
    public static final int ENTRY_LIMIT = 100;

    protected final Register register;
    protected final RecentEntryIndexQueryDAO queryDAO;

    public ResourceBase(RegisterProvider registerProvider) {
        this.register = registerProvider.provide();
        this.queryDAO = register.getQueryDAO();
    }

    protected String getRegisterPrimaryKey() {
        return register.getName();
    }
}

