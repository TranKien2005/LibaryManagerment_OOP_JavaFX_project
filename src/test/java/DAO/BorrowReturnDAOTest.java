package DAO;
import model.BorrowReturn;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class BorrowReturnDAOTest {
    private BorrowReturnDAO borrowReturnDAO;

    @Before
    public void setUp() {
        borrowReturnDAO = BorrowReturnDAO.getInstance();
    }

    @Test
    public void testGetAll() throws SQLException {
        List<BorrowReturn> borrowReturns = borrowReturnDAO.getAll();
        assertNotNull(borrowReturns);
        // Thêm các kiểm tra khác nếu cần
    }

    @Test
    public void testGetByAccountId() throws SQLException {
        List<BorrowReturn> borrowReturns = borrowReturnDAO.getByAccountId(1);
        assertNotNull(borrowReturns);
        // Thêm các kiểm tra khác nếu cần
    }
} 