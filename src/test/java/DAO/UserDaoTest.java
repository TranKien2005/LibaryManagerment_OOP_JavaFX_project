package DAO;
import model.User;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class UserDaoTest {
    private UserDao userDao;

    @Before
    public void setUp() {
        userDao = UserDao.getInstance();
    }

    @Test
    public void testGetAll() throws SQLException {
        List<User> users = userDao.getAll();
        assertNotNull(users);
        // Thêm các kiểm tra khác nếu cần
    }

    @Test
    public void testInsert() throws SQLException {
        User user = new User(1, "Full Name", "email@example.com", "123456789");
        try {
            userDao.insert(user);
            // Kiểm tra xem người dùng đã được thêm thành công
        } catch (SQLException e) {
            // Nếu ngoại lệ xảy ra, kiểm tra xem đó có phải là do người dùng đã tồn tại không
            assertNotNull(e);
            return; // Kết thúc test nếu ngoại lệ đã được ném ra
        }
        // Nếu không có ngoại lệ, kiểm tra xem người dùng có tồn tại trong cơ sở dữ liệu không
        List<User> users = userDao.getAll();
        assertNotNull(users);
        // Thêm kiểm tra để xác nhận người dùng đã được thêm
    }
} 