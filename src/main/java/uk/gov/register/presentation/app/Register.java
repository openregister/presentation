package uk.gov.register.presentation.app;

import org.skife.jdbi.v2.DBI;
import uk.gov.register.presentation.dao.RecentEntryIndexQueryDAO;
import uk.gov.register.presentation.dao.RecentEntryIndexUpdateDAO;

public class Register {

    private final RecentEntryIndexQueryDAO queryDAO;
    private final RecentEntryIndexUpdateDAO updateDAO;

    private final String name;

    public Register(DBProvider dbProvider, String name) {
        this.name = name;
        DBI jdbi = dbProvider.getJdbi(name);
        queryDAO = jdbi.onDemand(RecentEntryIndexQueryDAO.class);
        updateDAO = jdbi.onDemand(RecentEntryIndexUpdateDAO.class);
    }

    public RecentEntryIndexQueryDAO getQueryDAO() {
        return queryDAO;
    }

    RecentEntryIndexUpdateDAO getUpdateDAO() {
        return updateDAO;
    }

    public String getName() {
        return name;
    }
}
