package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import model.Account;
import model.Borrow;
import model.Document;
import model.Manager;
import model.Return;
import model.User;
import util.ErrorDialog;
import util.ThreadManager;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/librarymanagement";
    private static final String USER = "root";
    private static final String PASSWORD = "lan090805";
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
                ErrorDialog.showError("Database Connection Error", "Cannot connect to the database.", e.getMessage(), null);
                throw e; // Ném lại ngoại lệ để xử lý tiếp nếu cần
            }
        }
        return connection;
    }

    private void startConnectionChecker() {
        connectionChecker = new Timer(true);
        connectionChecker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (connection == null || connection.isClosed()) {
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    }
                } catch (SQLException e) {
                    ErrorDialog.showError("Database Connection Error", "Cannot connect to the database.", e.getMessage(), null);
                    e.printStackTrace();
                }
            }
        }, 0, 30000); // Kiểm tra mỗi 30 giây
    }

    public synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            if (connectionChecker != null) {
                connectionChecker.cancel();
            }
        } catch (SQLException e) {
            ErrorDialog.showError("Database Connection Error", "Error closing the database connection.", e.getMessage(), null);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Khởi tạo JavaFX Application Thread
        Platform.startup(() -> {
            try {
                // Thử AccountDao
                System.out.println("Testing AccountDao:");
                List<Account> accounts = AccountDao.getInstance().getAll();
                for (Account account : accounts) {
                    System.out.println(account);
                }

                // Thử BookDao
                System.out.println("\nTesting BookDao:");
                List<Document> books = BookDao.getInstance().getAll();
                for (Document book : books) {
                    System.out.println(book);
                }

                // Thử BorrowDao
                System.out.println("\nTesting BorrowDao:");
                List<Borrow> borrows = BorrowDao.getInstance().getAll();
                for (Borrow borrow : borrows) {
                    System.out.println(borrow);
                }

                // Thử UserDao
                System.out.println("\nTesting UserDao:");
                List<User> users = UserDao.getInstance().getAll();
                for (User user : users) {
                    System.out.println(user);
                }

                // Thử ReturnDao
                System.out.println("\nTesting ReturnDao:");
                List<Return> returns = ReturnDao.getInstance().getAll();
                for (Return returnRecord : returns) {
                    System.out.println(returnRecord);
                }

                // Thử ManagerDao
                System.out.println("\nTesting ManagerDao:");
                List<Manager> managers = ManagerDao.getInstance().getAll();
                for (Manager manager : managers) {
                    System.out.println(manager);
                }

                // Đóng ExecutorService khi ứng dụng kết thúc
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    ThreadManager.shutdown();
                }));
            } catch (Exception e) {
                ErrorDialog.showError("Error", "An unexpected error occurred.", e.getMessage(), null);
                e.printStackTrace();
            }
        });
    }
}