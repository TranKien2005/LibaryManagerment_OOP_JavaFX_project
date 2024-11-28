package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Singleton class for managing the database connection.
 * Ensures a single shared connection to the database and performs periodic checks to maintain it.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/librarymanagement";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    private Connection connection;
    private Timer connectionChecker;

    // Singleton instance
    private static DatabaseConnection instance;

    /**
     * Private constructor to prevent instantiation from outside.
     * Initializes the connection checker to monitor the database connection.
     */
    private DatabaseConnection() {
        startConnectionChecker();
    }

    /**
     * Gets the singleton instance of the DatabaseConnection class.
     *
     * @return the singleton instance of DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Retrieves the current database connection. If the connection is closed or null,
     * a new connection is created.
     *
     * @return the active database connection
     * @throws SQLException if unable to establish a connection to the database
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                throw new SQLException("Cannot connect to the database.", e);
            }
        }
        return connection;
    }

    /**
     * Starts a periodic connection checker that ensures the database connection remains open.
     * If the connection is closed, a new one is established.
     */
    private void startConnectionChecker() throws RuntimeException {
        connectionChecker = new Timer(true);
        connectionChecker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (connection == null || connection.isClosed()) {
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Cannot connect to the database.", e);
                }
            }
        }, 0, 30000); // Check every 30 seconds
    }

    /**
     * Closes the current database connection and stops the connection checker.
     */
    public synchronized void closeConnection() throws RuntimeException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            if (connectionChecker != null) {
                connectionChecker.cancel();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot close the database connection.", e);
        }
    }
}
