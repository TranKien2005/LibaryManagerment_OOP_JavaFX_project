package model;
import java.time.LocalDate;
import java.util.Objects;

public final  class Borrow {
    private int user_id;
    private String bookname;
    private LocalDate borrow_date;
    private LocalDate return_date;

    public Borrow(int user_id, String bookname, LocalDate borrow_date, LocalDate return_date) {
        this.user_id = user_id;
        this.bookname = bookname;
        this.borrow_date = borrow_date;
        this.return_date = return_date;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBorrow_date(LocalDate borrow_date) {
        this.borrow_date = borrow_date;
    }

    public LocalDate getBorrow_date() {
        return borrow_date;
    }

    public void setReturn_date(LocalDate return_date) {
        this.return_date = return_date;
    }

    public LocalDate getReturn_date() {
        return return_date;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Borrow borrow = (Borrow) obj;
        return user_id == borrow.user_id &&
               bookname.equals(borrow.bookname) &&
               borrow_date.equals(borrow.borrow_date) &&
               return_date.equals(borrow.return_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, bookname, borrow_date, return_date);
    }
}
