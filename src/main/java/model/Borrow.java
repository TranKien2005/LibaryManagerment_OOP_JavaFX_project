package model;
import java.time.LocalDate;

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
}
