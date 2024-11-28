package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/librarymanagement";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    private Connection connection;
    private Timer connectionChecker;

    // Thể hiện duy nhất của lớp
    private static DatabaseConnection instance;

    // Hàm khởi tạo riêng tư để ngăn chặn việc tạo thể hiện từ bên ngoài
    private DatabaseConnection() {
        startConnectionChecker();
    }

    // Phương thức tĩnh để truy cập thể hiện duy nhất
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

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
        }, 0, 30000); // Kiểm tra mỗi 30 giây
    }

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