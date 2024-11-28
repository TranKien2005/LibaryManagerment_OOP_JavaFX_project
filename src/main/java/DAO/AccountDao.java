/**
 * Data Access Object (DAO) for interacting with the Account table in the database.
 * This class provides methods to perform CRUD operations and other utility functions for accounts.
 * It uses singleton design pattern to ensure a single instance.
 */

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

import model.Account;
import util.ThreadManager;

public class AccountDao {
    private static AccountDao instance;

    /**
     * Private constructor to prevent instantiation from other classes.
     */
    private AccountDao() {
    }

    /**
     * Returns the singleton instance of the AccountDao.
     * 
     * @return AccountDao instance.
     */
    public static AccountDao getInstance() {
        if (instance == null) {
            instance = new AccountDao();
        }
        return instance;
    }

    /**
     * Retrieves all accounts from the database.
     * 
     * @return List of Account objects.
     * @throws SQLException if a database access error occurs.
     */
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
                            rs.getString("AccountType"));
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

    /**
     * Inserts a new account into the database.
     * 
     * @param account The Account object to insert.
     * @throws SQLException if a database access error occurs.
     */
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

    /**
     * Updates the password of an existing account.
     * 
     * @param account The Account object containing updated password.
     * @param id      The ID of the account to update.
     * @throws SQLException if a database access error occurs.
     */
    public void update(Account account, int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String checkQuery = "SELECT * FROM Account WHERE AccountID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        String currentUsername = rs.getString("Username");
                        String currentAccountType = rs.getString("AccountType");
                        if (!currentUsername.equals(account.getUsername())
                                || !currentAccountType.equals(account.getAccountType())) {
                            throw new SQLException("Username or AccountType does not match the existing record.");
                        }
                    } else {
                        throw new SQLException("Account with the specified ID does not exist.");
                    }
                }
            }

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

    /**
     * Deletes an account from the database by its ID.
     * 
     * @param id The ID of the account to delete.
     * @throws SQLException if a database access error occurs.
     */
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

    /**
     * Retrieves an account by its ID.
     * 
     * @param id The ID of the account to retrieve.
     * @return The Account object if found, otherwise null.
     * @throws SQLException if a database access error occurs.
     */
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
                            rs.getString("AccountType"));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("An error occurred while fetching the account: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Retrieves an account by its username.
     * 
     * @param username The username of the account to retrieve.
     * @return The Account object if found, otherwise null.
     * @throws SQLException if a database access error occurs.
     */
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
                            rs.getString("AccountType"));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("An error occurred while fetching the account: " + e.getMessage(), e);
        }
        return null;
    }

    // Other methods have similar documentation and implementation patterns.

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
