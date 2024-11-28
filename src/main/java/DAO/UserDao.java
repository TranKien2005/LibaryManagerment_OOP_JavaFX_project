package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import model.User;
import util.ThreadManager;

public class UserDao {
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

    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM User";
        Future<?> future;
        try {
            future = ThreadManager.submitSqlTask(() -> {
                try (Connection conn = DatabaseConnection.getInstance().getConnection();
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        User user = new User(
                                rs.getInt("AccountID"),
                                rs.getString("FullName"),
                                rs.getString("Email"),
                                rs.getString("Phone"));
                        synchronized (users) {
                            users.add(user);
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Database Error" + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            throw new SQLException("Database Error" + e.getMessage(), e);
        }
        try {
            future.get();
        }

        catch (InterruptedException | ExecutionException e) {
            throw new SQLException("Error executing SQL task: " + e.getMessage(), e);
        }
        return users;
    }

    public void insert(User user) throws SQLException {
        String query = "INSERT INTO User (AccountID, FullName, Email, Phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, user.getAccountID());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error" + e.getMessage(), e);
        }
    }

    public void update(User user, int id) throws SQLException, IllegalArgumentException {

        String query = "UPDATE User SET FullName = ?, Email = ?, Phone = ? WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error" + e.getMessage(), e);
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM User WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error" + e.getMessage(), e);
        }
    }

    public User get(int id) throws SQLException {
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
                            rs.getString("Phone"));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Database Error" + e.getMessage(), e);
        }
        return null;
    }

    public int getID(User user) throws SQLException {
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
            throw new SQLException("Database Error" + e.getMessage(), e);
        }
        return -1; // Return -1 if user not found
    }

    public List<Integer> getAllID() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT AccountID FROM User";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ids.add(rs.getInt("AccountID"));
            }
        } catch (SQLException e) {
            throw new SQLException("Database Error" + e.getMessage(), e);
        }
        return ids;
    }
}
