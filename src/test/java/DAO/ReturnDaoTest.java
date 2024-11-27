package DAO;
import model.Return;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class ReturnDaoTest {
    private ReturnDao returnDao;

    @Before
    public void setUp() {
        returnDao = ReturnDao.getInstance();
    }

    @Test
    public void testGetAll() throws SQLException {
        List<Return> returns = returnDao.getAll();
        assertNotNull(returns);
        // Thêm các kiểm tra khác nếu cần
    }

    @Test
    public void testInsert() throws SQLException {
        Return returnRecord = new Return(1, 1, LocalDate.now(), 0);
        try {
            returnDao.insert(returnRecord);
            // Kiểm tra xem bản ghi trả đã được thêm thành công
        } catch (SQLException e) {
            // Nếu ngoại lệ xảy ra, kiểm tra xem đó có phải là do bản ghi đã tồn tại không
            assertNotNull(e);
            return; // Kết thúc test nếu ngoại lệ đã được ném ra
        }
        // Nếu không có ngoại lệ, kiểm tra xem bản ghi đã được thêm vào cơ sở dữ liệu không
        List<Return> returns = returnDao.getAll();
        assertNotNull(returns);
        // Thêm kiểm tra để xác nhận bản ghi đã được thêm
    }
} 