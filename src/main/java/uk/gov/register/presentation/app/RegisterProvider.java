package uk.gov.register.presentation.app;

import uk.gov.register.presentation.config.PresentationConfiguration;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class RegisterProvider {

    private final static Map<String, Register> registerMap = new HashMap<>();

    private final ExecutorService executorService;
    private final DBProvider dbProvider;
    private final PresentationConfiguration configuration;

    @Context
    protected HttpServletRequest httpServletRequest;

    @Inject
    public RegisterProvider(ExecutorService executorService, DBProvider dbProvider, PresentationConfiguration configuration) {
        this.executorService = executorService;
        this.dbProvider = dbProvider;
        this.configuration = configuration;
    }

    //Note: copied the logic to fetch primary key from alpha register.
    //Note: We might need to change the logic of extracting register primary key for beta registers
    private String extractRegisterName() {
        try {
            String host = new URI(httpServletRequest.getRequestURL().toString()).getHost();

            //hack for functional tests
            if (host.startsWith("localhost")) return "ft_presentation";
            else return host.replaceAll("([^\\.]+)\\.(openregister)\\..*", "$1");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Register provide() {
        String name = extractRegisterName();
        if (registerMap.get(name) == null) {
            registerMap.put(name, initializeNewRegister(name));
        }
        return registerMap.get(name);
    }

    private Register initializeNewRegister(String name) {
        Register register = new Register(dbProvider, name);
        try {
            executorService.execute(new ConsumerRunnable(configuration, name, register.getUpdateDAO()));
        } catch (Throwable e) {
            //eat all errors and continue with the register without Consumer
            //We might delete Consumer soon
            e.printStackTrace();
        }
        return register;
    }

}
