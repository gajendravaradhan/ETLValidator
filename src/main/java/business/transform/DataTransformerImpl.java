package business.transform;

import business.peripherals.exceptions.DataTransformationException;
import infrastructure.ETLContext;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * The type Data transformer.
 */
public class DataTransformerImpl implements DataTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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


    public long countRows(Dataset<Row> dataset) {
        if (dataset == null) {
            throw new NullPointerException("dataset is null");
        }
        return dataset.count();
    }

}
