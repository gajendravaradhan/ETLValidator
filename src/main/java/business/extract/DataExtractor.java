package business.extract;

import business.peripherals.CustomValidationException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;

/**
 * The interface Data extractor.
 */
public interface DataExtractor {

    /**
     * Readcsv dataset.
     *
     * @param path   the path
     * @param header the header
     * @return the dataset
     * @throws IOException               the io exception
     * @throws CustomValidationException the custom validation exception
     */
    Dataset<Row> readcsv(String path, Boolean header) throws IOException, CustomValidationException;

    /**
     * Read json dataset.
     *
     * @param path   the path
     * @return the dataset
     * @throws IOException               the io exception
     * @throws CustomValidationException the custom validation exception
     */
    Dataset<Row> readJson(String path) throws IOException, CustomValidationException;

    /**
     * Read excel dataset.
     *
     * @param path      the path
     * @param sheetName the sheet name
     * @return the dataset
     * @throws IOException               the io exception
     * @throws CustomValidationException the custom validation exception
     */
    Dataset<Row> readExcel(String path, String sheetName) throws IOException, CustomValidationException;

    /**
     * Load from db table dataset.
     *
     * @param url       the url
     * @param tableName the table name
     * @return the dataset
     * @throws IOException               the io exception
     * @throws CustomValidationException the custom validation exception
     */
    Dataset<Row> loadFromDBTable(String url,String tableName) throws IOException, CustomValidationException;

    /**
     * Load from db table using query dataset.
     *
     * @param url   the url
     * @param query the query
     * @return the dataset
     * @throws IOException               the io exception
     * @throws CustomValidationException the custom validation exception
     */
    Dataset<Row> loadFromDBTableUsingQuery(String url,String query) throws IOException, CustomValidationException;

}
