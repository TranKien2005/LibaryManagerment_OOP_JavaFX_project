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

import model.Borrow;
import util.ThreadManager;

/**
 * Data Access Object (DAO) for the Borrow entity.
 * Handles all database operations related to the Borrow table.
 */
public class BorrowDao {
    private static BorrowDao instance;

    private BorrowDao() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the singleton instance of BorrowDao.
     *
     * @return the singleton instance
     */
    public static BorrowDao getInstance() {
        if (instance == null) {
            instance = new BorrowDao();
        }
        return instance;
    }

    /**
     * Retrieves all borrow records from the database.
     *
     * @return a list of Borrow objects
     * @throws SQLException if a database access error occurs
     */
    public List<Borrow> getAll() throws SQLException {
        List<Borrow> borrows = new ArrayList<>();
        String query = "SELECT * FROM Borrow";
        Future<?> future = ThreadManager.submitSqlTask(() -> {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Borrow borrow = new Borrow(
                            rs.getInt("BorrowID"),
                            rs.getInt("AccountID"),
                            rs.getInt("BookID"),
                            rs.getDate("BorrowDate").toLocalDate(),
                            rs.getDate("ExpectedReturnDate").toLocalDate(),
                            rs.getString("Status"));
                    synchronized (borrows) {
                        borrows.add(borrow);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        try {
            future.get(); // Wait for the task to complete
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        }
        return borrows;
    }

    /**
     * Inserts a new borrow record into the database.
     *
     * @param borrow the Borrow object to insert
     * @throws SQLException if a database access error occurs
     */
    public void insert(Borrow borrow) throws SQLException {
        String query = "INSERT INTO Borrow (AccountID, BookID, BorrowDate, ExpectedReturnDate, Status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, borrow.getAccountID());
            pstmt.setInt(2, borrow.getBookID());
            pstmt.setDate(3, Date.valueOf(borrow.getBorrowDate()));
            pstmt.setDate(4, Date.valueOf(borrow.getExpectedReturnDate()));
            pstmt.setString(5, borrow.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing borrow record in the database.
     *
     * @param borrow the Borrow object containing updated data
     * @param id     the ID of the record to update
     * @throws SQLException if a database access error occurs
     */
    public void update(Borrow borrow, int id) throws SQLException {
        String query = "UPDATE Borrow SET AccountID = ?, BookID = ?, BorrowDate = ?, ExpectedReturnDate = ?, Status = ? WHERE BorrowID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, borrow.getAccountID());
            pstmt.setInt(2, borrow.getBookID());
            pstmt.setDate(3, Date.valueOf(borrow.getBorrowDate()));
            pstmt.setDate(4, Date.valueOf(borrow.getExpectedReturnDate()));
            pstmt.setString(5, borrow.getStatus());
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a borrow record from the database.
     *
     * @param id the ID of the record to delete
     * @throws SQLException if a database access error occurs
     */
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM Borrow WHERE BorrowID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a borrow record by its ID.
     *
     * @param id the ID of the record to retrieve
     * @return the Borrow object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public Borrow get(int id) throws SQLException {
        String query = "SELECT * FROM Borrow WHERE BorrowID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Borrow(
                            rs.getInt("BorrowID"),
                            rs.getInt("AccountID"),
                            rs.getInt("BookID"),
                            rs.getDate("BorrowDate").toLocalDate(),
                            rs.getDate("ExpectedReturnDate").toLocalDate(),
                            rs.getString("Status"));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Retrieves the ID of a specific borrow record.
     *
     * @param borrow the Borrow object to search for
     * @return the ID of the record, or -1 if not found
     * @throws SQLException if a database access error occurs
     */
    public int getID(Borrow borrow) throws SQLException {
        String query = "SELECT BorrowID FROM Borrow WHERE AccountID = ? AND BookID = ? AND BorrowDate = ? AND ExpectedReturnDate = ? AND Status = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, borrow.getAccountID());
            pstmt.setInt(2, borrow.getBookID());
            pstmt.setDate(3, Date.valueOf(borrow.getBorrowDate()));
            pstmt.setDate(4, Date.valueOf(borrow.getExpectedReturnDate()));
            pstmt.setString(5, borrow.getStatus());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("BorrowID");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Database Error: " + e.getMessage(), e);
        }
        return -1; // Return -1 if no ID is found
    }

    /**
     * Retrieves all borrow record IDs from the database.
     *
     * @return a list of borrow IDs
     * @throws SQLException if a database access error occurs
     */
    public List<Integer> getAllID() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT BorrowID FROM Borrow";
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
