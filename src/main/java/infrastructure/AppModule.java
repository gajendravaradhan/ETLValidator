package infrastructure;

import business.extract.DataExtractor;
import business.extract.DataExtractorImpl;
import business.report.Reporter;
import business.report.ReporterImpl;
import business.transform.DataTransformer;
import business.transform.DataTransformerImpl;
import business.validate.Validator;
import business.validate.ValidatorImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class AppModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppModule.class);
    private String propertiesLocation;
    private Properties properties;


    public AppModule(String propertiesLocation) {
        this.propertiesLocation = propertiesLocation;
    }

    @Override
    protected void configure() {
        properties = new Properties();
        ClassLoader loader = AppModule.class.getClassLoader();
        try {
            URL url = loader.getResource(propertiesLocation);
            properties.load(url.openStream());
        } catch (Exception e) {
            LOGGER.error("Error reading properties file");
        }
        Names.bindProperties(binder(), properties);

        bind(DataExtractor.class).annotatedWith(Names.named("extractor")).to(DataExtractorImpl.class);
        bind(DataTransformer.class).annotatedWith(Names.named("transformer")).to(DataTransformerImpl.class);
        bind(Validator.class).annotatedWith(Names.named("validator")).to(ValidatorImpl.class);
        bind(Reporter.class).annotatedWith(Names.named("reporter")).to(ReporterImpl.class);
    }

}
