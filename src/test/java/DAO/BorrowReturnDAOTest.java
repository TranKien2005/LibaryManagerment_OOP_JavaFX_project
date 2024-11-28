/**
 * Unit tests for the BorrowReturnDAO class.
 * This class tests methods such as retrieving all borrow-return records and fetching records by account ID
 * to ensure the BorrowReturnDAO class behaves as expected.
 */
package DAO;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import model.BorrowReturn;

public class BorrowReturnDAOTest {
    private BorrowReturnDAO borrowReturnDAO;

    /**
     * Sets up the BorrowReturnDAO instance before each test.
     */
    @Before
    public void setUp() {
        borrowReturnDAO = BorrowReturnDAO.getInstance();
    }

    /**
     * Tests the retrieval of all borrow-return records from the database.
     * Ensures that the result is not null and potentially validates the data.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testGetAll() throws SQLException {
        List<BorrowReturn> borrowReturns = borrowReturnDAO.getAll();
        assertNotNull(borrowReturns);
        // Additional checks can be added here if needed.
    }

    /**
     * Tests the retrieval of borrow-return records by account ID.
     * Verifies that the result is not null and potentially validates the data.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testGetByAccountId() throws SQLException {
        List<BorrowReturn> borrowReturns = borrowReturnDAO.getByAccountId(1);
        assertNotNull(borrowReturns);
        // Additional checks can be added here if needed.
    }
}
