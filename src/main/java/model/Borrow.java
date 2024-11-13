package model;

import java.time.LocalDate;

public class Borrow {
    private int accountID;
    private int bookID;
    private LocalDate borrowDate;
    private LocalDate expectedReturnDate;
    private String status;

    public Borrow(int accountID, int bookID, LocalDate borrowDate, LocalDate expectedReturnDate, String status) {
        this.accountID = accountID;
        this.bookID = bookID;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status = status;
    }

    // Getters and setters
    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}