package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {
    private static final String username = "sa";
    private static final String password = "correcthorsebatterystaple";
    private static final String database = "ia130010";
    private static final int port = 1433;
    private static final String serverName = "localhost";

    private static final String connectionString = "jdbc:sqlserver://" + serverName + ":" + port + ";" +
            "database=" + database + ";user=" + username + ";password=" + password;

    private Connection connection;

    private DBUtils() {
        try {
            connection = DriverManager.getConnection(connectionString);
        } catch (SQLException ex) {
            System.out.println("Failed to retrieve SQL connection.");
            ex.printStackTrace();
        }
    }

    private static DBUtils dbUtils = null;

    public static DBUtils getInstance() {
        if (dbUtils == null) {
            dbUtils = new DBUtils();
        }
        return dbUtils;
    }

    public Connection getConnection() {
        return connection;
    }
}
