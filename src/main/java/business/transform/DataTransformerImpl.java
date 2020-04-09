package business.transform;

import business.peripherals.DataTransformationException;
import com.google.inject.Inject;
import infrastructure.ETLContext;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import javax.inject.Named;
import java.util.List;

public class DataTransformerImpl implements DataTransformer {

    /**
     * Calculate transaction core summary dataset using query.
     *
     * @param transactionCore    the transaction core
     * @param transactionCoreLot the transaction core lot
     * @return the dataset
     */
    @Override
    public Dataset<Row> calculateTransactionCoreSummary(Dataset<Row> transactionCore, Dataset<Row> transactionCoreLot, String calculationQuery) {
        Dataset<Row> result;
        transactionCore.createOrReplaceTempView("TRANSACTION_CORE");
        transactionCoreLot.createOrReplaceTempView("TRANSACTION_CORE_LOT");
        try {
            result = ETLContext.getETLContext().getSession().sql(calculationQuery);
        } catch (Exception e) {
            throw new DataTransformationException("Invalid Query: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Column columnSearchAndReplace(String column, Dataset<Row> temp, String toReplace, String replacement) {
        Column x = temp.col(column);
        return functions.when(x.equalTo(toReplace), replacement).otherwise(x).as(column);
    }

    @Override
    public Seq<String> convertListToSeq(List<String> inputList) {
        return JavaConverters.collectionAsScalaIterableConverter(inputList).asScala().toSeq();
    }

}
