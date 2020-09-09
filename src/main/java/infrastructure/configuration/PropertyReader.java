package infrastructure.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * The type Property reader.
 */
public class PropertyReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    /**
     * The Input stream.
     */
    private InputStream inputStream = null;
    /**
     * The Out stream.
     */
    private OutputStream outStream = null;
    private Properties properties = new Properties();

    /**
     * Instantiates a new Property reader.
     *
     * @param prop the prop
     */
    PropertyReader(String prop) throws IOException {
        loadProperties(prop);
    }

    /**
     * Loads properties from the given path.
     *
     * @param prop the prop
     */
    private void loadProperties(String prop) throws IOException {
        inputStream = new FileInputStream(prop);
        properties.load(inputStream);
    }

    /**
     * Dumps properties to the given path. Creates the file if it does not exist.
     *
     * @param map  the map containing the properties
     * @param path the file path
     */
    public void dumpProperties(Map<String, String> map, String path) {
        try {
            File f = new File(path);
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            f.createNewFile();
            outStream = new FileOutputStream(f);
            Properties properties = new Properties();
            Set<Map.Entry<String, String>> set = map.entrySet();
            for (Map.Entry<String, String> entry : set) {
                properties.put(entry.getKey(), entry.getValue());
            }
            properties.store(outStream, null);
        } catch (IOException e) {
            LOGGER.error("Error writing properties to file:" + path + "\n" + e.getMessage());
        }
    }

    public Logger getLogger() {
        return LOGGER;
    }

    /**
     * Gets properties.
     *
     * @return the properties
     */
    Properties getProperties() {
        return properties;
    }

    /**
     * Sets properties.
     *
     * @param properties the properties
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Read property string.
     *
     * @param key the key
     * @return the string
     */
    public String readProperty(String key) {
        return properties.getProperty(key);
    }
}
