package business.peripherals.datahelpers;

import business.peripherals.exceptions.CustomValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The type Test data handler.
 */

public class ETLDataHandlerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Object lock = new Object();
    private static ETLDataHandlerImpl etlDataHandlerImpl = null;
    private LinkedHashMap<String, Object> traverseMap;

    private ETLDataHandlerImpl(String jsonString) {
        try {
            traverseMap = getTestDataMap(jsonString);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private LinkedHashMap<String, Object> getTestDataMap(String jsonString) throws Exception {
        if (null == jsonString) {
            throw new CustomValidationException("Error reading json data files");
        } else if (!isJSONValid(jsonString)) {
            throw new CustomValidationException("Invalid JSON String being passed");
        }
        ObjectMapper mapper = new ObjectMapper();
        return (LinkedHashMap<String, Object>) mapper.readValue(jsonString, LinkedHashMap.class);
    }

    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static ETLDataHandlerImpl getInstance() {
        if (etlDataHandlerImpl != null) {
            return etlDataHandlerImpl;
        }
        throw new RuntimeException("DataHandler Instance not setup");
    }

    /**
     * Initialize all test data based on data type set in properties (supports yaml,excel,json files)
     *
     * @return the string
     */
    public static ETLDataHandlerImpl setInstance(String jsonString) {
        if (etlDataHandlerImpl == null) {
            synchronized (lock) {
                if (etlDataHandlerImpl == null) {
                    etlDataHandlerImpl = new ETLDataHandlerImpl(jsonString);
                }
            }
        }
        return etlDataHandlerImpl;

    }

    public Logger getLogger() {
        return LOGGER;
    }

    public LinkedHashMap<String, Object> getTraverseMap() {
        return traverseMap;
    }

    /**
     * Traverse and get string.
     *
     * @param key       the key
     * @param searchMap the search map
     * @return the string
     */
    public String traverseAndGet(String key, LinkedHashMap<String, Object> searchMap) {

        searchMap = traverseToParentMap(key, searchMap);
        key = key.substring(key.lastIndexOf(".") + 1);
        return (String) searchMap.get(key);
    }

    /**
     * Traverse to map.
     *
     * @param key       the key
     * @param searchMap the search map
     * @return the map
     */
    public LinkedHashMap<String, Object> traverseToMap(String key, LinkedHashMap<String, Object> searchMap) {

        key = key.toUpperCase();

        String children = key.substring(key.lastIndexOf(".") + 1);
        searchMap = traverseToParentMap(key, searchMap);
        if (searchMap != null && searchMap.get(children) instanceof Map)
            return (LinkedHashMap<String, Object>) searchMap.get(children);
        return null;
    }

    /**
     * Traverse to parent map.
     *
     * @param key       the key
     * @param searchMap the search map
     * @return the map
     */
    private LinkedHashMap<String, Object> traverseToParentMap(String key, LinkedHashMap<String, Object> searchMap) {
        //traverse through the complex key to the last leaf
        if (key.contains(".")) {
            //before period
            String parent = key.substring(0, key.indexOf("."));
            //after period
            key = key.substring(key.indexOf(".") + 1);
            if (searchMap.containsKey(parent))
                searchMap = traverseToParentMap(key, (LinkedHashMap<String, Object>) searchMap.get(parent));
            else
                LOGGER.error("invalid key for the Map " + key);
        }

        return searchMap;
    }

}