package business.validate;

import business.peripherals.exceptions.DataTransformationException;
import infrastructure.ETLContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.testng.Assert;
import scala.collection.JavaConversions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidatorImpl implements Validator {

    public void assertRowCount(Dataset<Row> expected, Dataset<Row> actual) {
        if (expected == null) {
            throw new NullPointerException("expected dataset is null");
        } else if (actual == null) {
            throw new NullPointerException("actual dataset is null");
        }
        Assert.assertEquals(expected.count(), actual.count());
    }

    @Override
    public void assertSchemaMatches(Dataset<Row> expected, Dataset<Row> actual) {
        if (expected == null) {
            throw new NullPointerException("expected dataset is null");
        } else if (actual == null) {
            throw new NullPointerException("actual dataset is null");
        }
        Assert.assertEquals(expected.schema().toString(), actual.schema().toString());
    }

    /**
     * Compare two dataset with same schema by columns given a common column.
     *
     * @param expected the source dataset
     * @param actual   the target dataset
     * @param primaryKey   the target dataset
     * @return the dataset
     */
    public Dataset<Row> compareByColumn(Dataset<Row> expected, Dataset<Row> actual, String primaryKey) throws DataTransformationException {
        if (expected == null || actual == null || primaryKey == null || expected.isEmpty() || actual.isEmpty() || primaryKey.length() == 0) {
            throw new DataTransformationException("One or more parameters empty or null");
        }
        Stream<Dataset<Row>> datasetStream = Arrays.stream(expected.columns()).map(column -> {
            return actual.select(primaryKey, column).exceptAll(expected.select(primaryKey, column)).withColumn("File", functions.lit("Actual")).withColumn("Test Status", functions.lit("Failed")).union(expected.select(primaryKey, column).exceptAll(actual.select(primaryKey, column)).withColumn("File", functions.lit("Expected")).withColumn("Test Status", functions.lit("Failed")));
        });
        List<Dataset<Row>> collect = datasetStream.map(diff -> {
            if (diff.count() > 0) {
                return diff;
            }
            return null;
        }).collect(Collectors.toList());
        collect.removeAll(Collections.singleton(null));
        List<String> columnsList = new ArrayList<>(0);
        columnsList.add(primaryKey);
        columnsList.add("File");
        columnsList.add("Test Status");
        Optional<Dataset<Row>> result = collect.stream().reduce((left, right) -> (left.join(right, JavaConversions.asScalaBuffer(columnsList), "outer")));
        return result.orElse(ETLContext.getETLContext().getSession().emptyDataFrame()).sort(primaryKey);
    }

    /**
     * Compare two datasets with same schema.
     *
     * @param expected the source dataset
     * @param actual   the target dataset
     * @return the dataset
     */
    @Override
    public Dataset<Row> compareDatasets(Dataset<Row> expected, Dataset<Row> actual) throws DataTransformationException {
        expected.createOrReplaceTempView("EXPECTED");
        actual.createOrReplaceTempView("ACTUAL");

        Dataset<Row> fail = actual.exceptAll(expected).withColumn("File", functions.lit("Actual")).withColumn("Test Status", functions.lit("Failed")).union(expected.exceptAll(actual).withColumn("File", functions.lit("Expected")).withColumn("Test Status", functions.lit("Failed")));
        Dataset<Row> pass = actual.intersectAll(expected).withColumn("File", functions.lit("Actual")).withColumn("Test Status", functions.lit("Passed")).union(expected.intersectAll(actual).withColumn("File", functions.lit("Expected")).withColumn("Test Status", functions.lit("Passed")));
        Dataset<Row> result = fail.union(pass);
        String runTimeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now());
        result = result.withColumn("TEST_RAN_AT", functions.lit(runTimeStamp));

        return result;
    }


}
