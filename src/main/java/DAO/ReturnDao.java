package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import model.Return;
import util.ThreadManager;

public class ReturnDao {
    private static ReturnDao instance;

    private ReturnDao() {
        // Private constructor to prevent instantiation
    }

    public static ReturnDao getInstance() {
        if (instance == null) {
            instance = new ReturnDao();
        }
        return instance;
    }

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
            future.get(); // Đợi cho đến khi tác vụ hoàn thành
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        }
        return returns;
    }

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
        return -1; // Return -1 if no ID is found
    }

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
