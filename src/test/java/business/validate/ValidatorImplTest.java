package business.validate;

import business.extract.DataExtractor;
import business.extract.DataExtractorImpl;
import business.peripherals.exceptions.CustomValidationException;
import business.peripherals.exceptions.DataTransformationException;
import infrastructure.ETLContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class ValidatorImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Dataset<Row> actual;
    private DataExtractor dataExtractor;
    private Dataset<Row> expected;
    private Validator target;

    @BeforeTest
    public void precondition() throws IOException, CustomValidationException {
        target = new ValidatorImpl();
        dataExtractor = new DataExtractorImpl();
        expected = dataExtractor.readcsv("src/test/resources/unit-test-data/transaction_core_lot.csv", true);
        actual = dataExtractor.readcsv("src/test/resources/unit-test-data/transaction_core_lot_2.csv", true);
    }

    @BeforeMethod
    public void setUp() {

    }

    @AfterTest
    public void tearDown() {
        ETLContext.getETLContext().getSession().close();
    }

    @Test
    public void testAssertRowCount() {
        target.assertRowCount(expected, actual);
    }

    @Test
    public void testAssertRowCountActualIsNull() {
        Dataset<Row> actual = null;
        Assert.assertThrows(NullPointerException.class, () -> target.assertRowCount(expected, actual));
    }

    @Test
    public void testAssertRowCountExpectedIsEmpty() {
        Dataset<Row> expected = ETLContext.getETLContext().getSession().emptyDataFrame();
        Assert.assertThrows(AssertionError.class, () -> target.assertRowCount(expected, actual));
    }

    @Test
    public void testAssertRowCountExpectedIsNull() {
        Dataset<Row> expected = null;
        Assert.assertThrows(NullPointerException.class, () -> target.assertRowCount(expected, actual));
    }

    @Test
    public void testAssertSchemaMatches() {
        target.assertSchemaMatches(expected, actual);
    }

    @Test
    public void testAssertSchemaMatchesActualIsNull() {
        Dataset<Row> actual = null;
        Assert.assertThrows(NullPointerException.class, () -> target.assertSchemaMatches(expected, actual));
    }

    @Test
    public void testAssertSchemaMatchesExpectedIsEmpty() {
        Dataset<Row> expected = ETLContext.getETLContext().getSession().emptyDataFrame();
        Assert.assertThrows(AssertionError.class, () -> target.assertSchemaMatches(expected, actual));
    }

    @Test
    public void testAssertSchemaMatchesExpectedIsNull() {
        Dataset<Row> expected = null;
        Assert.assertThrows(NullPointerException.class, () -> target.assertSchemaMatches(expected, actual));
    }

    @Test
    public void testCompareByColumn() {
        Dataset<Row> result = target.compareByColumn(expected, actual, "transaction_unique_identifier");
        Assert.assertEquals(result.filter(result.col("test status").equalTo("Failed")).count(), 4);
    }

    @Test
    public void testCompareByColumnNullCheck() {
        Dataset<Row> expectedNull = null;
        Dataset<Row> actualNull = null;
        Dataset<Row> expectedEmpty = ETLContext.getETLContext().getSession().emptyDataFrame();
        Dataset<Row> actualEmpty = ETLContext.getETLContext().getSession().emptyDataFrame();
        Assert.assertThrows(DataTransformationException.class, () -> target.compareByColumn(expectedNull, actual, "transaction_unique_identifier"));
        Assert.assertThrows(DataTransformationException.class, () -> target.compareByColumn(expected, actualNull, "transaction_unique_identifier"));
        Assert.assertThrows(DataTransformationException.class, () -> target.compareByColumn(expectedEmpty, actual, "transaction_unique_identifier"));
        Assert.assertThrows(DataTransformationException.class, () -> target.compareByColumn(expected, actualEmpty, "transaction_unique_identifier"));
        Assert.assertThrows(DataTransformationException.class, () -> target.compareByColumn(expected, actual, null));
        Assert.assertThrows(DataTransformationException.class, () -> target.compareByColumn(expected, actual, ""));
    }

    @Test
    public void testCompareDatasets() {
        Dataset<Row> result = target.compareDatasets(expected, actual);
        Assert.assertEquals(result.filter(result.col("test status").equalTo("Failed")).count(), 4);
    }
}