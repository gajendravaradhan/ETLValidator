package business.transform;

import business.extract.DataExtractor;
import business.extract.DataExtractorImpl;
import business.peripherals.exceptions.CustomValidationException;
import infrastructure.ETLContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class DataTransformerImplTest {
    private static SparkSession session;

    private DataExtractor dataExtractor;
    private Dataset<Row> expected;
    private DataTransformer target;

    @BeforeTest
    public void setUp() throws IOException, CustomValidationException {
        session = ETLContext.getETLContext().getSession();
        target = new DataTransformerImpl();
        dataExtractor = new DataExtractorImpl();
        expected = dataExtractor.readJson("src/test/resources/unit-test-data/expected.json");
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testConvertListToSeq() {
    }

    @Test
    public void testCountRows() {
    }
}