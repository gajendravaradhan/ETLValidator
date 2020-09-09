package business.report;

import infrastructure.ETLContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.DataFrameWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

import java.io.IOException;

public class ReporterImpl implements Reporter {
    /**
     * Write to json.
     *
     * @param dataset  the dataset
     * @param path     the path
     * @param fileName the file name
     * @param fileType the file type
     * @throws IOException the io exception
     */
    public void writeDataSet(Dataset<Row> dataset, String path, String fileName, String fileType) throws IOException {
        DataFrameWriter<Row> dataFrameWriter = dataset.repartition(1)
                .write()
                .mode(SaveMode.Overwrite);
        switch (fileType.toUpperCase()) {
            case "JSON":
                dataFrameWriter.json(path);
                break;
            case "CSV":
                dataFrameWriter.csv(path);
                break;
            case "ORC":
                dataFrameWriter.orc(path);
                break;
            case "PARQUET":
                dataFrameWriter.parquet(path);
                break;
            case "SQL":
                dataset.write()
                        .format("jdbc")
                        .option("url", path)
                        .option("dbtable", fileName)
                        .save();

                break;
            default:
                dataFrameWriter.format(fileType).save(path + "/" + fileName);
        }
        if (!fileType.equalsIgnoreCase("SQL"))
            renamePartitionFiles(path, "part-0000*." + fileType, fileName);
    }

    /**
     * Cleans up the written path of all the crc files and renames the file to desired value
     *
     * @param path the path
     * @param src  the file to be renamed
     * @param dst  the new name
     * @throws IOException the io exception
     */
    public void renamePartitionFiles(String path, String src, String dst) throws IOException {
        FileSystem fs = FileSystem.get(ETLContext.getETLContext().getSession().sparkContext().hadoopConfiguration());
        String name = fs.globStatus(new Path(path + src))[0].getPath().getName();
        fs.rename(new Path(path + name), new Path(path + dst));
        fs.delete(new Path(path + "._SUCCESS.crc"), true);
        fs.delete(new Path(path + "." + dst + ".crc"), true);
        fs.delete(new Path(path + "._SUCCESS"), true);
    }
}
