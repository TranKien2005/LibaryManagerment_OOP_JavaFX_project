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

                BorrowReturn borrowReturn = new BorrowReturn(borrowID, member, book, borrowDate, expectedReturnDate, status, returnDate, damagePercentage, penaltyFee);
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
    public static void main(String[] args) {
        BorrowReturnDAO dao = BorrowReturnDAO.getInstance();
        List<BorrowReturn> borrowReturns = dao.getAll();
        for (BorrowReturn borrowReturn : borrowReturns) {
            System.out.println("Borrow ID: " + borrowReturn.getBorrowID());
            System.out.println("Member: " + borrowReturn.getMember());
            System.out.println("Book: " + borrowReturn.getBook());
            System.out.println("Borrow Date: " + borrowReturn.getBorrowDate());
            System.out.println("Expected Return Date: " + borrowReturn.getExpectedReturnDate());
            System.out.println("Status: " + borrowReturn.getStatus());
            System.out.println("Return Date: " + borrowReturn.getReturnDate());
            System.out.println("Damage Percentage: " + borrowReturn.getDamagePercentage());
            System.out.println("Penalty Fee: " + borrowReturn.getPenaltyFee());
            System.out.println("-------------------------------");
        }
    }
}