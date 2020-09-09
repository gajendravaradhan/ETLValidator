package application.contextmanager;

import org.apache.spark.sql.Dataset;

import java.util.Map;

/**
 * The interface ContextManager.
 */
public interface ContextManager {

    Dataset getDataset(String key);

    Map<String, Dataset> getDatasetMap();

    void setDatasetMap(String key, Dataset value);

}
