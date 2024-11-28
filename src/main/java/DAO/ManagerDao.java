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

import model.Manager;
import util.ThreadManager;

/**
 * Data Access Object (DAO) class for the Manager entity.
 * Provides methods to interact with the Manager table in the database.
 * This class follows the Singleton pattern to ensure a single instance is used across the application.
 */
public class ManagerDao {
    private static ManagerDao instance;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private ManagerDao() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the singleton instance of the ManagerDao class.
     *
     * @return the singleton instance of ManagerDao
     */
    public static ManagerDao getInstance() {
        if (instance == null) {
            instance = new ManagerDao();
        }
        return instance;
    }

    /**
     * Retrieves all managers from the database.
     *
     * @return a list of Manager objects
     * @throws SQLException if there is an issue accessing the database
     */
    public List<Manager> getAll() throws SQLException {
        List<Manager> managers = new ArrayList<>();
        String query = "SELECT * FROM Manager";
        Future<?> future = ThreadManager.submitSqlTask(() -> {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Manager manager = new Manager(
                            rs.getInt("AccountID"),
                            rs.getString("FullName"),
                            rs.getString("Email"),
                            rs.getString("Phone"));
                    synchronized (managers) {
                        managers.add(manager);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        try {
            future.get(); // Wait until the task is completed
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        }
        return managers;
    }

    /**
     * Inserts a new manager into the database.
     *
     * @param manager the Manager object to be inserted
     * @throws SQLException if there is an issue with the database operation
     */
    public void insert(Manager manager) throws SQLException {
        String query = "INSERT INTO Manager (AccountID, FullName, Email, Phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, manager.getAccountID());
            pstmt.setString(2, manager.getFullName());
            pstmt.setString(3, manager.getEmail());
            pstmt.setString(4, manager.getPhone());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing manager in the database.
     *
     * @param manager the Manager object containing updated information
     * @param id      the AccountID of the manager to update
     * @throws SQLException if there is an issue with the database operation
     */
    public void update(Manager manager, int id) throws SQLException {
        String query = "UPDATE Manager SET FullName = ?, Email = ?, Phone = ? WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, manager.getFullName());
            pstmt.setString(2, manager.getEmail());
            pstmt.setString(3, manager.getPhone());
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a manager from the database by their AccountID.
     *
     * @param id the AccountID of the manager to delete
     * @throws SQLException if there is an issue with the database operation
     */
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM Manager WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a manager from the database by their AccountID.
     *
     * @param id the AccountID of the manager to retrieve
     * @return the Manager object, or null if no matching record is found
     * @throws SQLException if there is an issue with the database operation
     */
    public Manager get(int id) throws SQLException {
        String query = "SELECT * FROM Manager WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Manager(
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
     * Retrieves the AccountID of a specific manager based on their details.
     *
     * @param manager the Manager object with details to match
     * @return the AccountID of the matching manager, or -1 if no match is found
     * @throws SQLException if there is an issue with the database operation
     */
    public int getID(Manager manager) throws SQLException {
        String query = "SELECT AccountID FROM Manager WHERE FullName = ? AND Email = ? AND Phone = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, manager.getFullName());
            pstmt.setString(2, manager.getEmail());
            pstmt.setString(3, manager.getPhone());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("AccountID");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
        return -1; // Return -1 if no matching record is found
    }

    /**
     * Retrieves a list of all AccountIDs for managers in the database.
     *
     * @return a list of AccountIDs
     * @throws SQLException if there is an issue with the database operation
     */
    public List<Integer> getAllID() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT AccountID FROM Manager";
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
