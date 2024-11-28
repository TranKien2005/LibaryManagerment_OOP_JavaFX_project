package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import util.*;
import model.*;

public class BorrowReturnDAO {

    // Phương thức để lấy tất cả các bản ghi từ view BorrowReturnList
    private static BorrowReturnDAO instance;

    private BorrowReturnDAO() {
        // private constructor to prevent instantiation
    }

    @SuppressWarnings("DoubleCheckedLocking")
    public static BorrowReturnDAO getInstance() {
        if (instance == null) {
            synchronized (BorrowReturnDAO.class) {
                if (instance == null) {
                    instance = new BorrowReturnDAO();
                }
            }
        }
        return instance;
    }

    public List<BorrowReturn> getAll() {
        List<BorrowReturn> borrowReturns = new ArrayList<>();
        String query = "SELECT * FROM BorrowReturnList";
        Future<?> future = ThreadManager.submitSqlTask(() -> {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int borrowID = rs.getInt("BorrowID");
                    String member = rs.getString("Member");
                    String book = rs.getString("Book");
                    Date borrowDate = rs.getDate("BorrowDate");
                    Date expectedReturnDate = rs.getDate("ExpectedReturnDate");
                    String status = rs.getString("Status");
                    Date returnDate = rs.getDate("ReturnDate");
                    int damagePercentage = rs.getInt("DamagePercentage");
                    int penaltyFee = rs.getInt("PenaltyFee");

                    BorrowReturn borrowReturn = new BorrowReturn(borrowID, member, book, borrowDate, expectedReturnDate,
                            status, returnDate, damagePercentage, penaltyFee);
                    borrowReturns.add(borrowReturn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error retrieving borrow/return records" + e.getMessage(), e);
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing SQL task: " + e.getMessage(), e);
        }

        return borrowReturns;
    }

    public List<BorrowReturn> getByAccountId(int accountId) {
        List<BorrowReturn> borrowReturns = new ArrayList<>();
        String query = "SELECT * FROM BorrowReturnList WHERE SUBSTRING_INDEX(Member, ' - ', 1) = ?";
        Future<?> future = ThreadManager.submitSqlTask(() -> {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, accountId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int borrowID = rs.getInt("BorrowID");
                        String member = rs.getString("Member");
                        String book = rs.getString("Book");
                        Date borrowDate = rs.getDate("BorrowDate");
                        Date expectedReturnDate = rs.getDate("ExpectedReturnDate");
                        String status = rs.getString("Status");
                        Date returnDate = rs.getDate("ReturnDate");
                        int damagePercentage = rs.getInt("DamagePercentage");
                        int penaltyFee = rs.getInt("PenaltyFee");

                        BorrowReturn borrowReturn = new BorrowReturn(borrowID, member, book, borrowDate,
                                expectedReturnDate, status, returnDate, damagePercentage, penaltyFee);
                        borrowReturns.add(borrowReturn);
                    }
                }
            } catch (SQLException e) {

                throw new RuntimeException("Error retrieving borrow/return records by account ID: " + e.getMessage(),
                        e);
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {

            throw new RuntimeException("Error executing SQL task: " + e.getMessage(), e);
        }

        return borrowReturns;
    }

    public boolean isBorrowed(int accountId, int bookId) {
        String query = "SELECT * FROM BorrowReturnList WHERE SUBSTRING_INDEX(Member, ' - ', 1) = ? AND SUBSTRING_INDEX(Book, ' - ', 1) = ? AND Status = 'Borrowed'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, accountId);
            statement.setInt(2, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getID(int accountId, int bookId) {
        String query = "SELECT BorrowID FROM BorrowReturnList WHERE SUBSTRING_INDEX(Member, ' - ', 1) = ? AND SUBSTRING_INDEX(Book, ' - ', 1) = ? AND Status = 'Borrowed'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, accountId);
            statement.setInt(2, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("BorrowID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}