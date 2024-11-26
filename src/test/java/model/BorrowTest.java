package model;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;

public class BorrowTest {

    @Test
    public void testBorrowCreation() {
        Borrow borrow = new Borrow(1, 1, LocalDate.now(), LocalDate.now().plusDays(7), "Borrowed");
        assertNotNull(borrow);
        assertEquals(1, borrow.getAccountID());
        assertEquals(1, borrow.getBookID());
    }

    @Test
    public void testSetStatus() {
        Borrow borrow = new Borrow(1, 1, LocalDate.now(), LocalDate.now().plusDays(7), "Borrowed");
        borrow.setStatus("Returned");
        assertEquals("Returned", borrow.getStatus());
    }

    @Test
    public void testGettersAndSetters() {
        Borrow borrow = new Borrow(1, 1, LocalDate.now(), LocalDate.now().plusDays(7), "Borrowed");
        
        assertEquals(1, borrow.getAccountID());
        assertEquals(1, borrow.getBookID());
        assertNotNull(borrow.getBorrowDate());
        assertNotNull(borrow.getExpectedReturnDate());
        assertEquals("Borrowed", borrow.getStatus());

        borrow.setAccountID(2);
        borrow.setBookID(3);
        borrow.setBorrowDate(LocalDate.now().minusDays(1));
        borrow.setExpectedReturnDate(LocalDate.now().plusDays(10));
        borrow.setStatus("Returned");

        assertEquals(2, borrow.getAccountID());
        assertEquals(3, borrow.getBookID());
        assertNotNull(borrow.getBorrowDate());
        assertNotNull(borrow.getExpectedReturnDate());
        assertEquals("Returned", borrow.getStatus());
    }

    @Test
    public void testBorrowWithID() {
        Borrow borrow = new Borrow(1, 1, 1, LocalDate.now(), LocalDate.now().plusDays(7), "Borrowed");
        assertNotNull(borrow);
        assertEquals(1, borrow.getBorrowID());
        assertEquals(1, borrow.getAccountID());
        assertEquals(1, borrow.getBookID());
    }
} 