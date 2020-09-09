package application;

import business.peripherals.datahelpers.ETLDataHandlerImpl;
import business.peripherals.datahelpers.YAMLHelper;
import business.peripherals.exceptions.CustomValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.io.FilenameUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedHashMap;


public class ETLStepDefinitions extends Initializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Scenario scenario;

    public ETLStepDefinitions() {

    }

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @Then("^I compare (.*) and (.*) data by column having primary key \"(.*)\" and store it in \"(.*)\" dataset$")
    public void iCompareData_AndData_ByColumn(String source, String target, String primaryKey, String comparison) {
        Dataset compareResult = validator.compareByColumn(contextManager.getDataset(source), contextManager.getDataset(target), primaryKey);
        contextManager.setDatasetMap(comparison, compareResult);
        Dataset report = contextManager.getDataset(comparison);
        scenario.write(report.showString(5, 20, false));
        if (report.count() > 5)
            scenario.write("There are more rows to the report. Write the result to a file or database to view the entire report");
        LOGGER.info("Comparison completed");
    }

    @Then("^I compare (.*) and (.*) data by rows and store it in \"(.*)\" dataset$")
    public void iCompareExpectedAndActual(String source, String target, String comparison) {
        Dataset compareResult = validator.compareDatasets(contextManager.getDataset(source), contextManager.getDataset(target));
        contextManager.setDatasetMap(comparison, compareResult);
        Dataset report = contextManager.getDataset(comparison);
        scenario.write(report.showString(5, 20, false));
        if (report.count() > 5)
            scenario.write("There are more rows to the report. Write the result to a file or database to view the entire report");
        LOGGER.info("Comparison completed");
    }

    @When("^I load data from \"(.*)\" db using \"(.*)\" query$")
    public void iLoadDataFromDatabaseUsingQuery(String url, String query) throws IOException, CustomValidationException {
        contextManager.setDatasetMap(url.toUpperCase(), dataExtractor.loadFromDBTable(url, query));
    }

    @When("^I load data from \"(.*)\" db and \"(.*)\" table$")
    public void iLoadDataFromDatabaseUsingUrl(String url, String tableName) throws IOException, CustomValidationException {
        contextManager.setDatasetMap(tableName.toUpperCase(), dataExtractor.loadFromDBTable(url, tableName));
    }

    @Then("I perform transformation on (.*) dataset and (.*) dataset using (.*)")
    public void iPerformTransformationOnDatasetAndDatasetUsingQueriesCalculation_query(String sourceOne, String sourcetwo, String mappingQuery) {
        String query = ETLDataHandlerImpl.getInstance().traverseAndGet(mappingQuery.toUpperCase(), ETLDataHandlerImpl.getInstance().getTraverseMap());
        contextManager.setDatasetMap("RESULT", transformer.calculateTransactionCoreSummary(contextManager.getDataset(sourceOne), contextManager.getDataset(sourcetwo), query));
        LOGGER.info("transformation completed");
    }

    @And("^I print \"(.*)\" rows of \"(.*)\" dataset onto the screen$")
    public void iPrintDataToConsole(int rows, String dataset) {
        contextManager.getDataset(dataset).show(rows, false);
    }

    @When("^I read data from \"(.*)\" csv file( with a header)?$")
    public void iReadDataFromCsvFile(String path, String header) throws IOException, CustomValidationException {
        String fileType = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
        if (!fileType.startsWith("CSV")) {
            throw new CustomValidationException(path + " is not an EXCEL file");
        }
        String fileName = FilenameUtils.removeExtension(new File(path).getName());
        contextManager.setDatasetMap(fileName.toUpperCase(), dataExtractor.readcsv(path, header != null && header.length() > 0));
    }

    @When("^I read data from \"(.*)\" excel file and \"(.*)\" sheet$")
    public void iReadDataFromExcelFile(String path, String sheet) throws IOException, CustomValidationException {
        String fileType = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
        if (!fileType.startsWith("XLS")) {
            throw new CustomValidationException(path + " is not an EXCEL file");
        }
        String fileName = FilenameUtils.removeExtension(new File(path).getName());
        contextManager.setDatasetMap(fileName.toUpperCase(), dataExtractor.readExcel(path, sheet));
    }

    @When("^I read data from \"(.*)\" json file$")
    public void iReadDataFromJsonFile(String path) throws IOException, CustomValidationException {
        String fileType = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
        if (!fileType.equals("JSON")) {
            throw new CustomValidationException(path + " is not a JSON file");
        }
        String fileName = FilenameUtils.removeExtension(new File(path).getName());

        Dataset<Row> dataset = dataExtractor.readJson(path);
        contextManager.setDatasetMap(fileName.toUpperCase(), dataset);
        LOGGER.info("Read JSON completed");
    }

    @Given("I start an etl validation session")
    public void iStartAnEtlValidationSession() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        LinkedHashMap<String, Object> stringObjectLinkedHashMap = YAMLHelper.YAMLRead("src/test/resources/data/transformation.yaml");
        String dataString = mapper.writeValueAsString(stringObjectLinkedHashMap);
        ETLDataHandlerImpl.setInstance(dataString);
        LOGGER.info("Session started");
    }

    @And("^I write \"(.*)\" dataset to \"(.*)\" file in (json|csv|orc|parquet|sql) format$")
    public void iWriteDataToDataFile(String dataset, String path, String format) throws IOException, CustomValidationException {
        String fileType = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
        if (!fileType.equalsIgnoreCase(format))
            throw new CustomValidationException("file type derived from path is not the same as one specified in gherkin");
        String fileName = new File(path).getName();
        path = path.replaceAll(fileName, "");
        reporter.writeDataSet(contextManager.getDataset(dataset), path, fileName, format);
        LOGGER.info("Write completed");
    }
}