package business.report;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;

/**
 * The interface Reporter.
 */
public interface Reporter {

    /**
     * Write to json.
     *
     * @param dataset  the dataset
     * @param path     the path
     * @param fileName the file name
     * @param fileType
     * @throws IOException the io exception
     */
    void writeDataSet(Dataset<Row> dataset, String path, String fileName, String fileType) throws IOException;


    /**
     * Rename partition files.
     *
     * @param path the path
     * @param src  the src
     * @param dst  the dst
     * @throws IOException the io exception
     */
    void renamePartitionFiles(String path, String src, String dst) throws IOException;
}
