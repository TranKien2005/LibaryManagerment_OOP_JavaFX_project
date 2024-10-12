package BasicClass;

import java.util.Date;

public class ReturnTable {
    private int returnID; // tự động tạo khi cho vào sql
    private int borrowID;
    private Date returnDate;

    // Constructor
    public ReturnTable(int borrowID, Date returnDate) {
        this.borrowID = borrowID;
        this.returnDate = returnDate;
    }

    // Getter for returnID
    public int getReturnID() {
        return returnID;
    }

    // Getter and Setter for borrowID
    public int getBorrowID() {
        return borrowID;
    }

    public void setBorrowID(int borrowID) {
        this.borrowID = borrowID;
    }

    // Getter and Setter for returnDate
    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    // Override toString method
    @Override
    public String toString() {
        return "ReturnTable{" +
                "returnID=" + returnID +
                ", borrowID=" + borrowID +
                ", returnDate=" + returnDate +
                '}';
    }
}