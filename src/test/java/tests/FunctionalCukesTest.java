package tests;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;
import infrastructure.auth.Authenticator;
import org.junit.AfterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;


@CucumberOptions(tags = {"@test"},
        plugin = {"pretty", "html:target/reports/cucumberHtmlReport", "json:target/reports/cucumberJSONReport.json"},
        features = {"src/test/resources"},
        glue = {"application", "business"}
)
@Test
public class FunctionalCukesTest extends AbstractTestNGCucumberTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @AfterClass
    public static void afterScenario() {
        LOGGER.info("After tests");
    }

    @BeforeClass
    public static void test() throws Exception {
        String licenseStatus = Authenticator.validateKey();
        if (!licenseStatus.equalsIgnoreCase("Valid license")) {
            LOGGER.error(licenseStatus);
            throw new Exception(licenseStatus);

        }
    }

    public Logger getLogger() {
        return LOGGER;
    }


}


