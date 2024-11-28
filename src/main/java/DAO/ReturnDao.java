package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import model.Return;
import util.ThreadManager;

/**
 * Data Access Object (DAO) class for the Return entity.
 * Provides methods to interact with the ReturnTable in the database.
 * This class follows the Singleton pattern to ensure a single instance is used across the application.
 */
public class ReturnDao {
    private static ReturnDao instance;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private ReturnDao() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the singleton instance of the ReturnDao class.
     *
     * @return the singleton instance of ReturnDao
     */
    public static ReturnDao getInstance() {
        if (instance == null) {
            instance = new ReturnDao();
        }
        return instance;
    }

    /**
     * Retrieves all return records from the database.
     *
     * @return a list of Return objects
     * @throws SQLException if there is an issue accessing the database
     */
    public List<Return> getAll() throws SQLException {
        List<Return> returns = new ArrayList<>();
        String query = "SELECT * FROM ReturnTable";
        Future<?> future = ThreadManager.submitSqlTask(() -> {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Return returnRecord = new Return(
                            rs.getInt("ReturnID"),
                            rs.getInt("BorrowID"),
                            rs.getDate("ReturnDate").toLocalDate(),
                            rs.getInt("DamagePercentage"));
                    synchronized (returns) {
                        returns.add(returnRecord);
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
        return returns;
    }

    /**
     * Inserts a new return record into the database.
     *
     * @param returnRecord the Return object to be inserted
     * @throws SQLException if there is an issue with the database operation
     */
    public void insert(Return returnRecord) throws SQLException {
        String query = "INSERT INTO ReturnTable (BorrowID, ReturnDate, DamagePercentage) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, returnRecord.getBorrowID());
            pstmt.setDate(2, Date.valueOf(returnRecord.getReturnDate()));
            pstmt.setInt(3, returnRecord.getDamagePercentage());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing return record in the database.
     *
     * @param returnRecord the Return object containing updated information
     * @param id           the BorrowID of the return record to update
     * @throws SQLException if there is an issue with the database operation
     */
    public void update(Return returnRecord, int id) throws SQLException {
        String query = "UPDATE ReturnTable SET ReturnDate = ?, DamagePercentage = ? WHERE BorrowID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(returnRecord.getReturnDate()));
            pstmt.setInt(2, returnRecord.getDamagePercentage());
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a return record from the database by BorrowID.
     *
     * @param id the BorrowID of the return record to delete
     * @throws SQLException if there is an issue with the database operation
     */
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM ReturnTable WHERE BorrowID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a return record from the database by BorrowID.
     *
     * @param id the BorrowID of the return record to retrieve
     * @return the Return object, or null if no matching record is found
     * @throws SQLException if there is an issue with the database operation
     */
    public Return get(int id) throws SQLException {
        String query = "SELECT * FROM ReturnTable WHERE BorrowID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Return(
                            rs.getInt("ReturnID"),
                            rs.getInt("BorrowID"),
                            rs.getDate("ReturnDate").toLocalDate(),
                            rs.getInt("DamagePercentage"));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Retrieves the BorrowID of a specific return record based on return details.
     *
     * @param returnRecord the Return object with details to match
     * @return the BorrowID of the matching return record, or -1 if no match is found
     * @throws SQLException if there is an issue with the database operation
     */
    public int getID(Return returnRecord) throws SQLException {
        String query = "SELECT BorrowID FROM ReturnTable WHERE ReturnDate = ? AND DamagePercentage = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(returnRecord.getReturnDate()));
            pstmt.setInt(2, returnRecord.getDamagePercentage());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("BorrowID");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
        return -1; // Return -1 if no matching record is found
    }

    /**
     * Retrieves a list of all BorrowIDs for return records in the database.
     *
     * @return a list of BorrowIDs
     * @throws SQLException if there is an issue with the database operation
     */
    public List<Integer> getAllID() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT BorrowID FROM ReturnTable";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ids.add(rs.getInt("BorrowID"));
            }
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
        return ids;
    }
}
