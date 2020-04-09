package infrastructure;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;

public class ETLContext {
    private static ETLContext sparkInstance = null;

    public SparkSession getSession() {
        return session;
    }

    public ETLContext setSession(SparkSession session) {
        this.session = session;
        return this;
    }

    private SparkSession session;

    /**
     * Instantiates a new Spark etl validator.
     */
    private ETLContext() {
        String APPLICATION="Test";
        if(System.getProperty("os.name").contains("Windows"))
            System.setProperty("hadoop.home.dir", System.getenv("HADOOP_HOME"));
        else
            System.setProperty("hadoop.home.dir", "/");

        SparkSession.Builder builder = SparkSession.builder().appName(APPLICATION);
        session = builder.master("local").getOrCreate();
        session.sparkContext().setLogLevel("WARN");
    }


    /**
     * Instantiates a new Spark etl validator singleton.
     */
    public static ETLContext getETLContext() {
        if (sparkInstance == null)
            sparkInstance = new ETLContext();
        return sparkInstance;
    }

}
