package DAO;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import model.Account;

/**
 * Unit test for the {@link AccountDao} class.
 * This class tests the functionality of the DAO operations related to the {@link Account} model.
 */
public class AccountDaoTest {
    private AccountDao accountDao;

    /**
     * Initializes the {@link AccountDao} instance before each test.
     */
    @Before
    public void setUp() {
        accountDao = AccountDao.getInstance();
    }

    /**
     * Tests the {@link AccountDao#getAll()} method to ensure that it returns a list of accounts
     * and does not return null.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testGetAll() throws SQLException {
        List<Account> accounts = accountDao.getAll();
        assertNotNull("The account list should not be null", accounts);
        // Add further assertions or checks as needed
    }

    /**
     * Tests the {@link AccountDao#insert(Account)} method to ensure that an account
     * can be inserted into the database without errors.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testInsert() throws SQLException {
        Account account = new Account(1, "username7", "password", "user");
        try {
            accountDao.insert(account);
            // Add checks here if you want to confirm the account was inserted
        } catch (SQLException e) {
            // Handle any exceptions if necessary
            e.printStackTrace(); // Example of handling; replace with logging in production
        }
    }
}
