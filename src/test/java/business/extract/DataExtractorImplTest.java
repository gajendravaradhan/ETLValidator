package business.extract;

import business.helpers.Helpers;
import business.peripherals.exceptions.CustomValidationException;
import infrastructure.ETLContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.Connection;

public class DataExtractorImplTest {

    private static SparkSession session;
    private Connection conn = null;
    private Helpers helpers;
    private DataExtractor target;

    @Test
    public void checkSessionNotNull() {
        Assert.assertNotNull(session);
        Assert.assertSame(session, ETLContext.getETLContext().getSession());
    }

    @BeforeTest
    public void precondition() {
        target = new DataExtractorImpl();
        helpers = new Helpers();
        session = ETLContext.getETLContext().getSession();
    }

    @BeforeMethod
    public void setUp() {

    }

    @AfterTest
    public void tearDown() {
        if (conn != null) {
            String sql = "DROP TABLE IF EXISTS REGISTRATION";
            helpers.runQuery(conn, sql);
            helpers.closeConnection(conn);
        }
        if (session != null)
            session.close();
    }

    @Test
    public void testFailedMIMETypeValidation() {
        Assert.assertThrows(CustomValidationException.class, () -> target.readJson("src/test/resources/unit-test-data/spuriousDataTest.json"));
    }

    @Test
    public void testFailedPathValidation() {
        Assert.assertThrows(CustomValidationException.class, () -> target.readJson("unit-data/null.json"));
    }

    @Test
    public void testLoadFromDBTable() throws IOException, CustomValidationException {
        prepTable();
        Dataset<Row> result = target.loadFromDBTable("jdbc:h2:~/test;USER=sa;PASSWORD=", "registration");
        Assert.assertEquals(result.count(), 2);
    }

    @Test
    public void testLoadFromDBTableUsingQuery() throws IOException, CustomValidationException {
        prepTable();
        Dataset<Row> result = target.loadFromDBTableUsingQuery("jdbc:h2:~/test;USER=sa;PASSWORD=", "SELECT * FROM REGISTRATION");
        Assert.assertEquals(result.count(), 2);
    }

    @Test
    public void testLoadFromDBTableWithCredentials() throws IOException, CustomValidationException {
        prepTable();
        Dataset<Row> result = target.loadFromDBTableWithCredentials("jdbc:h2:~/test", "registration", "sa", "");
        Assert.assertEquals(result.count(), 2);
    }

    private void prepTable() {
        conn = helpers.connectToH2("jdbc:h2:~/test", "sa", "");
        String sql = "DROP TABLE IF EXISTS REGISTRATION";
        helpers.runQuery(conn, sql);
        sql = "CREATE TABLE REGISTRATION " +
                "(id INTEGER not NULL, " +
                " first VARCHAR(255), " +
                " last VARCHAR(255), " +
                " age INTEGER, " +
                " PRIMARY KEY ( id ))";
        helpers.runQuery(conn, sql);
        sql = "INSERT INTO Registration " + "VALUES (100, 'Zara', 'Ali', 18)";
        helpers.runQuery(conn, sql);
        sql = "INSERT INTO Registration " + "VALUES (101, 'Mahnaz', 'Fatma', 25)";
        helpers.runQuery(conn, sql);
    }

    @Test
    public void testReadExcel() throws IOException, CustomValidationException {
        Dataset<Row> result = target.readExcel("src/test/resources/unit-test-data/testData.xls", "testSheet");
        Assert.assertNotEquals(result.count(), 0);
    }

    @Test
    public void testReadExcelFailedMetaValidation() {
        Assert.assertThrows(CustomValidationException.class, () -> target.readExcel("src/test/resources/unit-test-data/Data.xls", "testSheet"));
    }

    @Test
    public void testReadJson() throws IOException, CustomValidationException {
        Dataset<Row> result = target.readJson("src/test/resources/unit-test-data/expected.json");
        Assert.assertNotEquals(result.count(), 0);
    }

    @Test
    public void testReadJsonFailedMetaValidation() {
        Assert.assertThrows(CustomValidationException.class, () -> target.readJson("src/test/resources/unit/expected.json"));
    }

    @Test
    public void testReadcsv() throws IOException, CustomValidationException {
        Dataset<Row> result = target.readcsv("src/test/resources/unit-test-data/transaction_core_lot.csv", true);
        Assert.assertNotEquals(result.count(), 0);
    }

    @Test
    public void testReadcsvFailedMetaValidation() {
        Assert.assertThrows(CustomValidationException.class, () -> target.readcsv("src/test/resources/unit/transaction_core_lot.csv", true));
    }
}