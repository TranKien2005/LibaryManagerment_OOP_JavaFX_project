package model;

import java.time.LocalDate;

public class Return {
    private int borrowID;
    private LocalDate returnDate;
    private int damagePercentage;

    public Return(int borrowID, LocalDate returnDate, int damagePercentage) {
        this.borrowID = borrowID;
        this.returnDate = returnDate;
        this.damagePercentage = damagePercentage;
    }

    // Getters and setters
    public int getBorrowID() {
        return borrowID;
    }

    public void setBorrowID(int borrowID) {
        this.borrowID = borrowID;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public int getDamagePercentage() {
        return damagePercentage;
    }

    public void setDamagePercentage(int damagePercentage) {
        this.damagePercentage = damagePercentage;
    }
}