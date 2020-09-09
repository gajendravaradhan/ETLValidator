package application.contextmanager;

import org.apache.spark.sql.Dataset;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The global Context manager.
 */
public class ContextManagerImpl implements ContextManager {

    private static final Object lock = new Object();
    private static volatile ContextManagerImpl contextManagerImpl;
    private AtomicReference<Map<String, Dataset>> datasetMap = new AtomicReference<>(null);

    public ContextManagerImpl() {
    }

    /**
     * Double-checked lock implementation of singleton for getting Context Manager instance.
     *
     * @return the instance
     */
    public static ContextManagerImpl getInstance() {
        if (contextManagerImpl == null) {
            synchronized (lock) {
                if (contextManagerImpl == null) {
                    contextManagerImpl = new ContextManagerImpl();
                }
            }
        }
        return contextManagerImpl;
    }

    @Override
    public Dataset getDataset(String key) {
        return datasetMap.get().get(key.toUpperCase());
    }

    /**
     * Gets dataset map.
     *
     * @return the dataset map
     */
    public Map<String, Dataset> getDatasetMap() {
        return datasetMap.get();
    }

    /**
     * Sets dataset map atomically.
     *
     * @param key   the key
     * @param value the value
     */
    public void setDatasetMap(String key, Dataset value) {
        Map<String, Dataset> result = datasetMap.get();
        if (result == null) {
            synchronized (lock) {
                result = new ConcurrentHashMap<>();
                datasetMap.compareAndSet(null, result);
            }
        }
        datasetMap.get().putIfAbsent(key.toUpperCase(), value);
    }
}
