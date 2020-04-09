package application;

import business.peripherals.CustomValidationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import infrastructure.AppModule;

import java.io.IOException;

public class ETLEngine {

    public static void main(String[] args) throws IOException, CustomValidationException {
        Injector injector = Guice.createInjector(new AppModule("application.properties"));
        Orchestrator orchestrator = injector.getInstance(Orchestrator.class);
        orchestrator.initializeJob();
    }
}
