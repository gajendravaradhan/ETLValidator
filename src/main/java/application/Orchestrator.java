package application;

import business.extract.DataExtractor;
import business.peripherals.CustomValidationException;
import business.report.Reporter;
import business.transform.DataTransformer;
import business.validate.Validator;
import com.google.inject.Inject;

import com.google.inject.name.Named;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;

public class Orchestrator {

    @Inject
    @Named("extractor")
    private DataExtractor dataExtractor;

    @Inject
    @Named("transformer")
    private DataTransformer transformer;

    @Inject
    @Named("reporter")
    private Reporter reporter;

    @Inject
    @Named("validator")
    private Validator validator;

    @Inject
    @Named("transaction_core_path")
    private String transactionCorePath;

    @Inject
    @Named("transaction_core_lot_path")
    private String transactionCoreLotPath;

    @Inject
    @Named("outputpath")
    private String outputpath;

    @Inject
    @Named("calculationQuery")
    private String calculationQuery;

    public void initializeJob() throws IOException, CustomValidationException {
        Dataset<Row> transactionCore = dataExtractor.readJson(transactionCorePath);
        Dataset<Row> transactionCoreLot = dataExtractor.readcsv(transactionCoreLotPath,true);

        Dataset<Row> expected = transformer.calculateTransactionCoreSummary(transactionCore,transactionCoreLot,calculationQuery);
        reporter.writeDataSet(expected,outputpath, "result.json","json");

        Dataset<Row> left = dataExtractor.readJson("src/main/resources/expected.json");
        Dataset<Row> right = dataExtractor.readJson("src/main/resources/output/result.json");
        Dataset<Row> result =validator.compareByColumn(left,right, "transaction_unique_identifier");

    }

}
