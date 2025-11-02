package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConnection {
    private static Properties props = new Properties();
    private static boolean initialized = false;

    private static void initializeProperties() throws SQLException {
        try {
            props.load(new FileInputStream("config/database.properties"));
            initialized = true;
        } catch (IOException e) {
            throw new SQLException("Could not load database configuration: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initializeProperties();
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.");
        }
    }
}
