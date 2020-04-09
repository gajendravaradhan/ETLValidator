package business.extract;

import business.peripherals.CustomValidationException;
import business.peripherals.MetaValidator;
import infrastructure.ETLContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The type Data extractor.
 */

public class DataExtractorImpl implements DataExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataExtractorImpl.class);


    public Dataset<Row> readcsv(String path, Boolean header) throws IOException, CustomValidationException {
        MetaValidator.validateFile(path,"csv");
        LOGGER.debug("File Meta Validation completed!");
        return ETLContext.getETLContext().getSession()
                .read()
                .format("csv")
                .option("header",header)
                .load(path);
    }

    public Dataset<Row> readJson(String path) throws IOException, CustomValidationException {
        MetaValidator.validateFile(path,"json");
        LOGGER.debug("File Meta Validation completed!");
        return ETLContext.getETLContext().getSession()
                .read()
                .json(path);
    }

    public Dataset<Row> readExcel(String path, String sheetName) throws IOException, CustomValidationException {
        MetaValidator.validateFile(path,"excel");
        LOGGER.debug("File Meta Validation completed!");
        return null;
    }

    public Dataset<Row> loadFromDBTable(String url, String tableName) throws IOException, CustomValidationException {
        return ETLContext.getETLContext().getSession()
                .read()
                .format("jdbc")
                .option("url", url)
                .option("dbtable", tableName)
                .load();
    }

    public Dataset<Row> loadFromDBTableUsingQuery(String url, String query) throws IOException, CustomValidationException {
        return ETLContext.getETLContext().getSession()
                .read()
                .format("jdbc")
                .option("url", url)
                .option("query", query)
                .load();
    }
}
