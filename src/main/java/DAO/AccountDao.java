package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import model.Account;
import util.*;

public class AccountDao  {
    private static AccountDao instance;

    private AccountDao() {
        // Private constructor to prevent instantiation
    }

    public static AccountDao getInstance() {
        if (instance == null) {
            instance = new AccountDao();
        }
        return instance;
    }

    public List<Account> getAll() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM Account";
        Future<?> future = ThreadManager.submitSqlTask(() -> {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Account account = new Account(
                        rs.getInt("AccountID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("AccountType")
                    );
                    synchronized (accounts) {
                        accounts.add(account);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("An error occurred while fetching accounts: " + e.getMessage(), e);
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("An error occurred while fetching accounts: " + e.getMessage(), e);
        
        } 
        return accounts;
    }

    public void insert(Account account) throws SQLException {
        String query = "INSERT INTO Account (Username, Password, AccountType) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, account.getUsername());
            pstmt.setString(2, account.getPassword());
            pstmt.setString(3, account.getAccountType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("An error occurred while inserting the account: " + e.getMessage(), e);
        }
    }

    public void update(Account account, int id) throws SQLException {
        String query = "UPDATE Account SET Username = ?, Password = ?, AccountType = ? WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Check if the current username and account type match the existing record
            String checkQuery = "SELECT * FROM Account WHERE AccountID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        String currentUsername = rs.getString("Username");
                        String currentAccountType = rs.getString("AccountType");
                        if (!currentUsername.equals(account.getUsername()) || !currentAccountType.equals(account.getAccountType())) {
                            throw new SQLException("Username or AccountType does not match the existing record.");
                        }
                    } else {
                        throw new SQLException("Account with the specified ID does not exist.");
                    }
                }
            }

            // Update only the password
            String updateQuery = "UPDATE Account SET Password = ? WHERE AccountID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, account.getPassword());
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLException("An error occurred while updating the account: " + e.getMessage(), e);
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM Account WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("An error occurred while deleting the account: " + e.getMessage(), e);
        }
    }

    public Account get(int id) throws SQLException {
        String query = "SELECT * FROM Account WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getInt("AccountID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("AccountType")
                    );
                }
            }
        } catch (SQLException e) {
            throw new SQLException("An error occurred while fetching the account: " + e.getMessage(), e);
        }
        return null;
    }

    public Account getByUsername(String username) throws SQLException {
        String query = "SELECT * FROM Account WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getInt("AccountID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("AccountType")
                    );
                }
            }
        } catch (SQLException e) {
            throw new SQLException("An error occurred while fetching the account: " + e.getMessage(), e);
        }
        return null;
    }

    public void updatePassword(int id, String newPassword) throws SQLException {
        String query = "UPDATE Account SET Password = ? WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("An error occurred while updating the password: " + e.getMessage(), e);
        }
    }

    public List<Integer> getAllID() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT AccountID FROM Account";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ids.add(rs.getInt("AccountID"));
            }
        } catch (SQLException e) {
            throw new SQLException("An error occurred while fetching account IDs: " + e.getMessage(), e);
        }
        return ids;
    }

    public int getID(Account account) throws SQLException {
        String query = "SELECT AccountID FROM Account WHERE Username = ? AND Password = ? AND AccountType = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, account.getUsername());
            pstmt.setString(2, account.getPassword());
            pstmt.setString(3, account.getAccountType());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("AccountID");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("An error occurred while fetching the account ID: " + e.getMessage(), e);
        }
        return -1; // Return -1 if the account ID is not found
    }

  
}
