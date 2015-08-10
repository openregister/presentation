package uk.gov.register.presentation.app;

import uk.gov.register.presentation.config.PresentationConfiguration;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
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

    private String extractRegisterName() {
        String host = httpServletRequest.getHeader("Host");

        if (host.contains(".")) {
            return host.substring(0, host.indexOf("."));
        } else {
            return host;
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
