package DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import model.Return;
import util.ThreadManager;

public class ReturnDao implements DaoInterface<Return> {
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

   @Override
public List<Return> getAll() {
    List<Return> returns = new ArrayList<>();
    String query = "SELECT * FROM ReturnTable";
    Future<?> future = ThreadManager.submitSqlTask(() -> {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Return returnRecord = new Return(
                    rs.getInt("BorrowID"),
                    rs.getDate("ReturnDate").toLocalDate(),
                    rs.getInt("DamagePercentage")
                );
                synchronized (returns) {
                    returns.add(returnRecord);
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
    return returns;
}

    @Override
    public void insert(Return returnRecord) {
        String query = "INSERT INTO ReturnTable (BorrowID, ReturnDate, DamagePercentage) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, returnRecord.getBorrowID());
            pstmt.setDate(2, Date.valueOf(returnRecord.getReturnDate()));
            pstmt.setInt(3, returnRecord.getDamagePercentage());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Return returnRecord, int id) {
        String query = "UPDATE ReturnTable SET ReturnDate = ?, DamagePercentage = ? WHERE BorrowID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(returnRecord.getReturnDate()));
            pstmt.setInt(2, returnRecord.getDamagePercentage());
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM ReturnTable WHERE BorrowID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Return get(int id) {
        String query = "SELECT * FROM ReturnTable WHERE BorrowID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Return(
                        rs.getInt("BorrowID"),
                        rs.getDate("ReturnDate").toLocalDate(),
                        rs.getInt("DamagePercentage")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getID(Return returnRecord) {
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
            e.printStackTrace();
        }
        return -1; // Return -1 if no ID is found
    }

    public List<Integer> getAllID() {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT BorrowID FROM ReturnTable";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ids.add(rs.getInt("BorrowID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}