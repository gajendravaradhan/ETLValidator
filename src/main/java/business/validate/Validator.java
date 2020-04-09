package business.validate;

import business.peripherals.DataTransformationException;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.List;

/**
 * The interface Validator.
 */
public interface Validator {

    /**
     * Compare two similarly structured dataset.
     *
     * @param expected the source dataset
     * @param actual   the target dataset
     * @return the dataset
     * @throws DataTransformationException the data transformation exception
     */
    public Dataset<Row> compareDatasets(Dataset<Row> expected, Dataset<Row> actual) throws DataTransformationException;

    Dataset<Row> compareByColumn(Dataset<Row> expected, Dataset<Row> actual,  String primaryKey) throws DataTransformationException;

}
