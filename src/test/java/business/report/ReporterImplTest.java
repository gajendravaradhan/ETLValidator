package business.report;

import business.extract.DataExtractor;
import business.extract.DataExtractorImpl;
import business.helpers.Helpers;
import business.peripherals.exceptions.CustomValidationException;
import infrastructure.ETLContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.util.Arrays;

public class ReporterImplTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Connection conn = null;
    private DataExtractor dataExtractor;
    private Dataset<Row> expected;
    private FileSystem fs;
    private Helpers helpers;
    private Reporter target;

    @BeforeTest
    public void precondition() throws IOException, CustomValidationException {
        target = new ReporterImpl();
        fs = FileSystem.get(ETLContext.getETLContext().getSession().sparkContext().hadoopConfiguration());
        dataExtractor = new DataExtractorImpl();
        helpers = new Helpers();
        prepTable();
        expected = dataExtractor.readJson("src/test/resources/unit-test-data/expected.json");
    }

    @AfterMethod
    public void tearDown() {
        forceDeleteFiles("src/test/resources/unit-test-data/output/");
        prepTable();
    }

    private void forceDeleteFiles(String path) {
        File fin = new File(path);
        Arrays.stream(fin.listFiles()).forEach(file -> {
            try {
                System.gc();
                boolean delete = file.delete();
                if (!delete) {
                    Thread.sleep(100);
                    System.gc();
                    delete = file.delete();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                LOGGER.error("Unable to delete file:" + file);
            }
        });
    }

    private void prepTable() {
        conn = helpers.connectToH2("jdbc:h2:~/test", "sa", "");
        String sql = "DROP TABLE IF EXISTS OUTPUT";
        helpers.runQuery(conn, sql);
    }

    @Test
    public void testWriteDataSetCsv() throws IOException, CustomValidationException {
        target.writeDataSet(expected, "src/test/resources/unit-test-data/output/", "output.csv", "csv");
        Dataset<Row> actual = dataExtractor.readcsv("src/test/resources/unit-test-data/output/output.csv", true);
        Assert.assertNotNull(actual);
    }

    @Test
    public void testWriteDataSetJson() throws IOException, CustomValidationException {
        target.writeDataSet(expected, "src/test/resources/unit-test-data/output/", "output.json", "json");
        Dataset<Row> actual = dataExtractor.readJson("src/test/resources/unit-test-data/output/output.json");
        Assert.assertNotNull(actual);
    }

    @Test
    public void testWriteDataSetOrc() throws IOException, CustomValidationException {
        target.writeDataSet(expected, "src/test/resources/unit-test-data/output/", "output.orc", "orc");
        Dataset<Row> actual = dataExtractor.readORC("src/test/resources/unit-test-data/output/output.orc");
        Assert.assertNotNull(actual);
    }

    @Test
    public void testWriteDataSetParquet() throws IOException, CustomValidationException {
        target.writeDataSet(expected, "src/test/resources/unit-test-data/output/", "output.parquet", "parquet");
        Dataset<Row> actual = dataExtractor.readParquet("src/test/resources/unit-test-data/output/output.parquet");
        Assert.assertNotNull(actual);
    }

    @Test
    public void testWriteDataSetSQL() throws IOException, CustomValidationException {
        target.writeDataSet(expected, "jdbc:h2:~/test;USER=sa;PASSWORD=", "output", "SQL");
        Dataset<Row> actual = dataExtractor.loadFromDBTable("jdbc:h2:~/test;USER=sa;PASSWORD=", "output");
        Assert.assertNotNull(actual);
    }
}