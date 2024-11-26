package model;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;

public class ReturnTest {

    @Test
    public void testReturnCreation() {
        Return returnRecord = new Return(1, LocalDate.now(), 10);
        assertNotNull(returnRecord);
        assertEquals(1, returnRecord.getBorrowID());
        assertEquals(10, returnRecord.getDamagePercentage());
    }

    @Test
    public void testSetDamagePercentage() {
        Return returnRecord = new Return(1, LocalDate.now(), 10);
        returnRecord.setDamagePercentage(20);
        assertEquals(20, returnRecord.getDamagePercentage());
    }

    @Test
    public void testGetReturnID() {
        Return returnRecord = new Return(1, 1, LocalDate.now(), 10);
        assertEquals(1, returnRecord.getReturnID());
    }

    @Test
    public void testSetBorrowID() {
        Return returnRecord = new Return(1, LocalDate.now(), 10);
        returnRecord.setBorrowID(2);
        assertEquals(2, returnRecord.getBorrowID());
    }

    @Test
    public void testGetReturnDate() {
        LocalDate date = LocalDate.now();
        Return returnRecord = new Return(1, date, 10);
        assertEquals(date, returnRecord.getReturnDate());
    }

    @Test
    public void testSetReturnDate() {
        Return returnRecord = new Return(1, LocalDate.now(), 10);
        LocalDate newDate = LocalDate.of(2023, 1, 1);
        returnRecord.setReturnDate(newDate);
        assertEquals(newDate, returnRecord.getReturnDate());
    }
} 