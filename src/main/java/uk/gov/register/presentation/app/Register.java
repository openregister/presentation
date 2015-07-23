package uk.gov.register.presentation.app;

import org.skife.jdbi.v2.DBI;
import uk.gov.register.presentation.dao.RecentEntryIndexQueryDAO;

public class Register {

    private final RecentEntryIndexQueryDAO queryDAO;

    private final String name;

    public Register(DBProvider dbProvider, String name) {
        this.name = name;
        DBI jdbi = dbProvider.getJdbi(name);
        queryDAO = jdbi.onDemand(RecentEntryIndexQueryDAO.class);
    }

    public RecentEntryIndexQueryDAO getQueryDAO() {
        return queryDAO;
    }

    public String getName() {
        return name;
    }
}
