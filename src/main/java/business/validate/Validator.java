package business.validate;

import business.peripherals.exceptions.DataTransformationException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * The interface Validator.
 */
public interface Validator {

    /**
     * Assert row count matches the expected dataset.
     *
     * @param expected the expected
     * @param actual   the actual
     */
    void assertRowCount(Dataset<Row> expected, Dataset<Row> actual);

    /**
     * Assert that the two dataset schemas match
     *
     * @param expected source dataset
     * @param actual target dataset
     */
    void assertSchemaMatches(Dataset<Row> expected, Dataset<Row> actual);

    /**
     * Compare two dataset with same schema by columns given a common column.
     *
     * @param expected   the source dataset
     * @param actual     the target dataset
     * @param primaryKey the target dataset
     * @return the dataset
     */
    Dataset<Row> compareByColumn(Dataset<Row> expected, Dataset<Row> actual, String primaryKey) throws DataTransformationException;

    /**
     * Compare two similarly structured dataset.
     *
     * @param expected the source dataset
     * @param actual   the target dataset
     * @return the dataset
     * @throws DataTransformationException the data transformation exception
     */
    Dataset<Row> compareDatasets(Dataset<Row> expected, Dataset<Row> actual) throws DataTransformationException;
}
