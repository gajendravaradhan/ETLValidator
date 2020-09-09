package business.extract;

import business.peripherals.MetaValidator;
import business.peripherals.exceptions.CustomValidationException;
import infrastructure.ETLContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

/**
 * The type Data extractor.
 */

public class DataExtractorImpl implements DataExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public Dataset<Row> loadFromDBTableWithCredentials(String url, String tableName, String username, String password) {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", username);
        connectionProperties.put("password", password);
        return ETLContext.getETLContext().getSession()
                .read()
                .jdbc(url, tableName, connectionProperties);
    }

    public Dataset<Row> readExcel(String path, String sheetName) throws IOException, CustomValidationException {
        if (!MetaValidator.validateFile(path, "application/vnd")) {
            String error = "File Meta Validation failed!";
            LOGGER.error(error);
            throw new CustomValidationException(error);
        } else {
            LOGGER.debug("File Meta Validation completed!");
            return ETLContext.getETLContext().getSession().read()
                    .format("com.crealytics.spark.excel")
                    .option("sheetName", sheetName)
                    .option("useHeader", "true")
                    .option("treatEmptyValuesAsNulls", "true")
                    .option("inferSchema", "true")
                    .option("addColorColumns", "False")
                    .load(path);
        }
    }

    public Dataset<Row> readJson(String path) throws IOException, CustomValidationException {
        if (!MetaValidator.validateFile(path, "json")) {
            String error = "File Meta Validation failed!";
            LOGGER.error(error);
            throw new CustomValidationException(error);
        } else {
            LOGGER.debug("File Meta Validation completed!");
            return ETLContext.getETLContext().getSession()
                    .read()
                    .json(path);
        }
    }

    @Override
    public Dataset<Row> readORC(String path) {
        return ETLContext.getETLContext().getSession()
                .read()
                .format("orc")
                .load(path);
    }

    @Override
    public Dataset<Row> readParquet(String path) {
        return ETLContext.getETLContext().getSession()
                .read()
                .parquet(path);
    }

    public Dataset<Row> loadFromDBTable(String url, String tableName) {
        return ETLContext.getETLContext().getSession()
                .read()
                .format("jdbc")
                .option("url", url)
                .option("dbtable", tableName)
                .load();
    }

    public Dataset<Row> readcsv(String path, Boolean header) throws IOException, CustomValidationException {
        if (!MetaValidator.validateFile(path, "csv")) {
            String error = "File Meta Validation failed!";
            LOGGER.error(error);
            throw new CustomValidationException(error);
        } else {
            LOGGER.debug("File Meta Validation completed!");
            return ETLContext.getETLContext().getSession()
                    .read()
                    .format("csv")
                    .option("header", header)
                    .load(path);
        }
    }

    public Dataset<Row> loadFromDBTableUsingQuery(String url, String query) {
        return ETLContext.getETLContext().getSession()
                .read()
                .format("jdbc")
                .option("url", url)
                .option("query", query)
                .load();
    }
}
