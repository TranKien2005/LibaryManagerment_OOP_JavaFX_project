package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import model.Account;
import util.*;

public class AccountDao implements DaoInterface<Account> {
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

    @Override
public List<Account> getAll() {
    List<Account> accounts = new ArrayList<>();
    String query = "SELECT * FROM Account";
    Future<?> future = ThreadManager.submitSqlTask(() -> {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Account account = new Account(
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getString("AccountType")
                );
                synchronized (accounts) {
                    accounts.add(account);
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
    return accounts;
}

    @Override
    public void insert(Account account) {
        String query = "INSERT INTO Account (Username, Password, AccountType) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, account.getUsername());
            pstmt.setString(2, account.getPassword());
            pstmt.setString(3, account.getAccountType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Account account, int id) {
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
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM Account WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Account get(int id) {
        String query = "SELECT * FROM Account WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("AccountType")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account getByUsername(String username) {
        String query = "SELECT * FROM Account WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("AccountType")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getID(Account account) {
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
            e.printStackTrace();
        }
        return -1; // Return -1 if no matching account is found
    }

    public void updatePassword(int id, String newPassword) {
        String query = "UPDATE Account SET Password = ? WHERE AccountID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getAllID() {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT AccountID FROM Account";
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
    public static void main(String[] args) {
        // Lấy thể hiện duy nhất của AccountDao
        AccountDao accountDao = AccountDao.getInstance();

        // Tạo một tài khoản mới và chèn vào cơ sở dữ liệu
        Account newAccount = new Account("newuser", "password123", "user");
        accountDao.insert(newAccount);
        System.out.println("Inserted new account: " + newAccount.getUsername());

        // Lấy tất cả các tài khoản từ cơ sở dữ liệu và in ra
        List<Account> accounts = accountDao.getAll();
        System.out.println("All accounts:");
        for (Account account : accounts) {
            System.out.println("Username: " + account.getUsername() + ", Password: " + account.getPassword() + ", AccountType: " + account.getAccountType());
        }
    }
}