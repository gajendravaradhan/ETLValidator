package application.contextmanager;

import business.extract.DataExtractor;
import business.extract.DataExtractorImpl;
import business.peripherals.exceptions.CustomValidationException;
import infrastructure.ETLContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class ContextManagerImplTest {
    private ContextManager contextManager;
    private DataExtractor dataExtractor;
    private Dataset<Row> expected;

    @BeforeMethod
    public void precondition() {

    }

    @BeforeTest
    public void setUp() throws IOException, CustomValidationException {
        dataExtractor = new DataExtractorImpl();
        expected = dataExtractor.readJson("src/test/resources/unit-test-data/expected.json");
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testGetDataset() {
        contextManager = ContextManagerImpl.getInstance();
        String key = "first";
        contextManager.setDatasetMap(key, expected);
        Assert.assertEquals(contextManager.getDataset(key), expected);
    }

    @Test
    public void testGetDatasetMap() {
        contextManager = ContextManagerImpl.getInstance();
        String key = "first";
        contextManager.setDatasetMap(key, expected);
        Assert.assertEquals(contextManager.getDatasetMap().get(key.toUpperCase()), expected);
    }

    @Test
    public void testGetInstance() {
        contextManager = ContextManagerImpl.getInstance();
        ContextManager newInstance = ContextManagerImpl.getInstance();
        Assert.assertSame(contextManager, newInstance);
    }

    @Test
    public void testSetDatasetMap() throws InterruptedException {
        contextManager = ContextManagerImpl.getInstance();
        String key = "first";
        for (int i = 1; i < 100; i++) {
            Thread t1 = new Thread(() -> contextManager.setDatasetMap(key, expected));
            Thread t2 = new Thread(() -> contextManager.setDatasetMap(key, ETLContext.getETLContext().getSession().emptyDataFrame()));
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            Assert.assertNotNull(contextManager);
            Assert.assertNotNull(contextManager.getDataset(key));
        }
    }
}