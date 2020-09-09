package application;

import application.contextmanager.ContextManager;
import application.contextmanager.ContextManagerImpl;
import business.extract.DataExtractor;
import business.extract.DataExtractorImpl;
import business.report.Reporter;
import business.report.ReporterImpl;
import business.transform.DataTransformer;
import business.transform.DataTransformerImpl;
import business.validate.Validator;
import business.validate.ValidatorImpl;

public class Initializer {

    protected DataExtractor dataExtractor;
    protected Reporter reporter;
    protected DataTransformer transformer;
    protected Validator validator;
    ContextManager contextManager;

    public Initializer() {
        contextManager = new ContextManagerImpl();
        dataExtractor = new DataExtractorImpl();
        transformer = new DataTransformerImpl();
        reporter = new ReporterImpl();
        validator = new ValidatorImpl();
    }
}
