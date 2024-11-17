package model;
import java.util.Date;

public class BorrowReturn {
    private int borrowID;
    private String member;
    private String book;
    private Date borrowDate;
    private Date expectedReturnDate;
    private String status;
    private Date returnDate;
    private int damagePercentage;
    private int penaltyFee;

    // Constructor
    public BorrowReturn(int borrowID, String member, String book, Date borrowDate, Date expectedReturnDate, String status, Date returnDate, int damagePercentage, int penaltyFee) {
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