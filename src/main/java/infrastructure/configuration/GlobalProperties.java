package infrastructure.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;

public class GlobalProperties {
    private static final String CONFIGPATH = "resources";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static GlobalProperties globalProperties = null;
    private Map<String, String> configProperties;

    //Auto-instantiation of global properties
    private GlobalProperties() {
        PropertyReader propertyReader;
        try {
            propertyReader = new PropertyReader(CONFIGPATH);
            configProperties = (Map) propertyReader.getProperties();
        } catch (java.io.IOException e) {
            LOGGER.error("Error reading properties file" + e.getMessage());
        }

        LOGGER.debug("-------------------------------------------------------------------------------------------------------------------------");
        LOGGER.debug(configProperties.toString());
        LOGGER.debug(" -------------------------------------------------------------------------------------------------------------------------");

    }

    public static GlobalProperties getConfigProperties() {
        if (globalProperties == null) {
            globalProperties = new GlobalProperties();
        }
        return globalProperties;
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public String getProperty(String propertyName) {
        return configProperties.get(propertyName);
    }

    public void setProperty(String propertyName, String propertyValue) {
        configProperties.put(propertyName, propertyValue);
    }

}
