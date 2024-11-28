package model;

import java.util.Date;

public class BorrowReturn {
    private final int borrowID;
    private final String member;
    private final String book;
    private final Date borrowDate;
    private final Date expectedReturnDate;
    private final String status;
    private final Date returnDate;
    private final int damagePercentage;
    private final int penaltyFee;

    // Constructor
    public BorrowReturn(int borrowID, String member, String book, Date borrowDate, Date expectedReturnDate,
            String status, Date returnDate, int damagePercentage, int penaltyFee) {
        this.borrowID = borrowID;
        this.member = member;
        this.book = book;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status = status;
        this.returnDate = returnDate;
        this.damagePercentage = damagePercentage;
        this.penaltyFee = penaltyFee;
    }

    // Getters and Setters
    public int getBorrowID() {
        return borrowID;
    }

    public String getMember() {
        return member;
    }

    public String getBook() {
        return book;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public String getStatus() {
        return status;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public int getDamagePercentage() {
        return damagePercentage;
    }

    public int getPenaltyFee() {
        return penaltyFee;
    }

}