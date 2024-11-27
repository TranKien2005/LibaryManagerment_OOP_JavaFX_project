package DAO;
import model.Account;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class AccountDaoTest {
    private AccountDao accountDao;

    @Before
    public void setUp() {
        accountDao = AccountDao.getInstance();
    }

    @Test
    public void testGetAll() throws SQLException {
        List<Account> accounts = accountDao.getAll();
        assertNotNull(accounts);
        // Thêm các kiểm tra khác nếu cần
    }

    @Test
    public void testInsert() throws SQLException {
        Account account = new Account(1, "username7", "password", "user");
        try {
            accountDao.insert(account);
            // Kiểm tra xem tài khoản đã được thêm thành công
        } catch (SQLException e) {
            // Xử lý ngoại lệ nếu cần
        }
    }
} 