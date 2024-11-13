package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import model.Manager;
import util.ThreadManager;

public class ManagerDao implements DaoInterface<Manager> {
    private static ManagerDao instance;

    private ManagerDao() {
        // Private constructor to prevent instantiation
    }

    public static ManagerDao getInstance() {
        if (instance == null) {
            instance = new ManagerDao();
        }
        return instance;
    }

    @Override
public List<Manager> getAll() {
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
                    rs.getString("Phone")
                );
                synchronized (managers) {
                    managers.add(manager);
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
    return managers;
}

    @Override
    public void insert(Manager manager) {
        String query = "INSERT INTO Manager (AccountID, FullName, Email, Phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, manager.getAccountID());
            pstmt.setString(2, manager.getFullName());
            pstmt.setString(3, manager.getEmail());
            pstmt.setString(4, manager.getPhone());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Manager manager, int id) {
        String query = "UPDATE Manager SET FullName = ?, Email = ?, Phone = ? WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, manager.getFullName());
            pstmt.setString(2, manager.getEmail());
            pstmt.setString(3, manager.getPhone());
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM Manager WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Manager get(int id) {
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
                        rs.getString("Phone")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getID(Manager manager) {
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
            e.printStackTrace();
        }
        return -1; // Return -1 if no matching record is found
    }

    public List<Integer> getAllID() {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT AccountID FROM Manager";
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