package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import model.User;
import util.ThreadManager;

public class UserDao implements DaoInterface<User> {
    private static UserDao instance;

    private UserDao() {
        // Private constructor to prevent instantiation
    }

    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }

    @Override
public List<User> getAll() {
    List<User> users = new ArrayList<>();
    String query = "SELECT * FROM User";
    Future<?> future = ThreadManager.submitSqlTask(() -> {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("AccountID"),
                    rs.getString("FullName"),
                    rs.getString("Email"),
                    rs.getString("Phone")
                );
                synchronized (users) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    });
    try {
        future.get(); // Đợi cho đến khi tác vụ hoàn thành
    } catch (Exception e) {
        e.printStackTrace();
    }
    return users;
}

    @Override
    public void insert(User user) {
        String query = "INSERT INTO User (AccountID, FullName, Email, Phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, user.getAccountID());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user, int id) throws IllegalArgumentException {
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!isValidPhone(user.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        String query = "UPDATE User SET FullName = ?, Email = ?, Phone = ? WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^[0-9]{10}$";
        return phone.matches(phoneRegex);
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM User WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User get(int id) {
        String query = "SELECT * FROM User WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("AccountID"),
                        rs.getString("FullName"),
                        rs.getString("Email"),
                        rs.getString("Phone")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getID(User user) {
        String query = "SELECT AccountID FROM User WHERE FullName = ? AND Email = ? AND Phone = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("AccountID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if user not found
    }

    public List<Integer> getAllID() {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT AccountID FROM User";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ids.add(rs.getInt("AccountID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}