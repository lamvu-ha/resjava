package db;

import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=RestaurantDB;encrypt=false";
    private static final String USER = "sa";
    private static final String PASS = "051120";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
