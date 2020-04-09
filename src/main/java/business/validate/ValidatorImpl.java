package business.validate;

import business.extract.DataExtractor;
import business.peripherals.DataTransformationException;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import infrastructure.ETLContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import scala.collection.JavaConversions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidatorImpl implements Validator {

    /**
     * Compare two similarly structured dataset.
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


    public Dataset<Row> compareByColumn(Dataset<Row> expected, Dataset<Row> actual, String primaryKey) throws DataTransformationException {
        Stream<Dataset<Row>> datasetStream = Arrays.stream(expected.columns()).map(column -> {
            return actual.select(primaryKey,column).exceptAll(expected.select(primaryKey,column)).withColumn("File", functions.lit("Actual")).withColumn("Test Status", functions.lit("Failed")).union(expected.select(primaryKey,column).exceptAll(actual.select(primaryKey,column)).withColumn("File", functions.lit("Expected")).withColumn("Test Status", functions.lit("Failed")));
        });
        List<Dataset<Row>> collect = datasetStream.map(diff -> {
            if (diff.count() > 0) {
                return diff;
            }
            return null;
        }).collect(Collectors.toList());
        collect.removeAll(Collections.singleton(null));
        List<String> columnsList =new ArrayList<String>(0);
        columnsList.add(primaryKey);
        columnsList.add("File");
        columnsList.add("Test Status");
        Optional<Dataset<Row>> result = collect.stream().reduce((left, right) -> (left.join(right, JavaConversions.asScalaBuffer(columnsList), "outer")));
        Dataset<Row> report = result.orElse(ETLContext.getETLContext().getSession().emptyDataFrame()).sort(primaryKey);
        return report;

    }
}
