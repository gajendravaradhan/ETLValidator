package business.transform;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.List;

/**
 * The interface Data transformer.
 */
public interface DataTransformer {

    /**
     * Calculate transaction core summary dataset.
     *
     * @param transactionCore    the transaction core
     * @param transactionCoreLot the transaction core lot
     * @return the dataset
     */
    Dataset<Row> calculateTransactionCoreSummary (Dataset<Row> transactionCore, Dataset<Row> transactionCoreLot, String calculationQuery);

    /**
     * For a given column in Dataset, replace all blanks with the given string.
     *
     * @param column      the column
     * @param temp        the temp
     * @param toReplace   the to replace
     * @param replacement the replacement
     * @return the column
     */
    public Column columnSearchAndReplace(String column, Dataset<Row> temp, String toReplace, String replacement);


    /**
     * Convert list to scala sequence.
     *
     * @param inputList the input list
     * @return the seq
     */
    public Seq<String> convertListToSeq(List<String> inputList);
}
