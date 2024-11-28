package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import model.Borrow;
import util.ThreadManager;

public class BorrowDao {
    private static BorrowDao instance;

    private BorrowDao() {
        // Private constructor to prevent instantiation
    }

    public static BorrowDao getInstance() {
        if (instance == null) {
            instance = new BorrowDao();
        }
        return instance;
    }

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
            future.get(); // Đợi cho đến khi tác vụ hoàn thành
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        }
        return borrows;
    }

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
