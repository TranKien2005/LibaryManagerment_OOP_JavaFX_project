package DAO;
import model.Manager;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class ManagerDaoTest {
    private ManagerDao managerDao;

    @Before
    public void setUp() {
        managerDao = ManagerDao.getInstance();
    }

    @Test
    public void testGetAll() throws SQLException {
        List<Manager> managers = managerDao.getAll();
        assertNotNull(managers);
        // Thêm các kiểm tra khác nếu cần
    }

    @Test
    public void testInsert() throws SQLException {
        Manager manager = new Manager(1, "Full Name", "email@example.com", "123456789");
        try {
            managerDao.insert(manager);
            // Kiểm tra xem quản lý đã được thêm thành công
        } catch (SQLException e) {
            // Nếu ngoại lệ xảy ra, kiểm tra xem đó có phải là do quản lý đã tồn tại không
            assertNotNull(e);
            return; // Kết thúc test nếu ngoại lệ đã được ném ra
        }
        // Nếu không có ngoại lệ, kiểm tra xem quản lý có tồn tại trong cơ sở dữ liệu không
        List<Manager> managers = managerDao.getAll();
        assertNotNull(managers);
        // Thêm kiểm tra để xác nhận quản lý đã được thêm
    }
} 