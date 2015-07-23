package uk.gov.register.presentation.app;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class RegisterProvider {
    private final static Map<String, Register> registerMap = new HashMap<>();

    private final DBProvider dbProvider;

    @Context
    protected HttpServletRequest httpServletRequest;

    @Inject
    public RegisterProvider(DBProvider dbProvider) {
        this.dbProvider = dbProvider;
    }

    //Note: copied the logic to fetch primary key from alpha register.
    //Note: We might need to change the logic of extracting register primary key for beta registers
    private String extractRegisterName() {
        try {
            String host = new URI(httpServletRequest.getRequestURL().toString()).getHost();

            //hack for functional tests
            if (host.startsWith("localhost")) return "ft_test_pkey";
            else return host.replaceAll("([^\\.]+)\\.(openregister)\\..*", "$1");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Register provide() {
        String name = extractRegisterName();
        if (registerMap.get(name) == null) {
            registerMap.put(name, new Register(dbProvider, name));
        }
        return registerMap.get(name);
    }

}
