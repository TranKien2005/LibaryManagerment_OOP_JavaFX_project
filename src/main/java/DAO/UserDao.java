package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import model.User;
import util.ThreadManager;

/**
 * Data Access Object (DAO) class for the User entity.
 * Provides methods to interact with the User table in the database.
 * This class follows the Singleton pattern to ensure a single instance is used across the application.
 */
public class UserDao {
    private static UserDao instance;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private UserDao() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the singleton instance of the UserDao class.
     *
     * @return the singleton instance of UserDao
     */
    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }

    /**
     * Retrieves all user records from the database.
     *
     * @return a list of User objects
     * @throws SQLException if there is an issue accessing the database
     */
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
                    throw new RuntimeException("Database Error: " + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new SQLException("Error executing SQL task: " + e.getMessage(), e);
        }
        return users;
    }

    /**
     * Inserts a new user record into the database.
     *
     * @param user the User object to be inserted
     * @throws SQLException if there is an issue with the database operation
     */
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
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing user record in the database.
     *
     * @param user the User object containing updated information
     * @param id   the AccountID of the user record to update
     * @throws SQLException if there is an issue with the database operation
     * @throws IllegalArgumentException if the provided ID does not exist
     */
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
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user record from the database by AccountID.
     *
     * @param id the AccountID of the user record to delete
     * @throws SQLException if there is an issue with the database operation
     */
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM User WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a user record from the database by AccountID.
     *
     * @param id the AccountID of the user record to retrieve
     * @return the User object, or null if no matching record is found
     * @throws SQLException if there is an issue with the database operation
     */
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
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Retrieves the AccountID of a specific user based on user details.
     *
     * @param user the User object with details to match
     * @return the AccountID of the matching user, or -1 if no match is found
     * @throws SQLException if there is an issue with the database operation
     */
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
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
        return -1; // Return -1 if user not found
    }

    /**
     * Retrieves a list of all AccountIDs for user records in the database.
     *
     * @return a list of AccountIDs
     * @throws SQLException if there is an issue with the database operation
     */
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
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
        return ids;
    }
}
