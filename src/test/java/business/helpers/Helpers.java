package business.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Helpers {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private String DB_URL = "jdbc:h2:~/test";
    private String PASS = "";
    private String USER = "sa";

    public void closeConnection(Connection conn) {

        try {
            if (conn != null)
                conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public Connection connectToH2(String DB_URL, String USER, String PASS) {
        Connection conn = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection
            LOGGER.info("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
        return conn;
    }

    public void runQuery(Connection conn, String sql) {
        Statement stmt;
        try {
            //STEP 3: Execute a query
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
    }
}


