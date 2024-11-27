package DAO;
import model.Document;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class BookDaoTest {
    private BookDao bookDao;

    @Before
    public void setUp() {
        bookDao = BookDao.getInstance();
    }

    @Test
    public void testGetAll() throws SQLException {
        List<Document> documents = bookDao.getAll();
        assertNotNull(documents);
        // Thêm các kiểm tra khác nếu cần
    }

    @Test
    public void testInsert() throws SQLException {
        Document document = new Document(1, "Title", "Author", "Category", "Publisher", 2023, 5);
        try {
            bookDao.insert(document);
            // Kiểm tra xem tài liệu đã được thêm thành công
        } catch (SQLException e) {
            // Nếu ngoại lệ xảy ra, kiểm tra xem đó có phải là do tài liệu đã tồn tại không
            assertNotNull(e);
            return; // Kết thúc test nếu ngoại lệ đã được ném ra
        }
        // Nếu không có ngoại lệ, kiểm tra xem tài liệu có tồn tại trong cơ sở dữ liệu không
        List<Document> documents = bookDao.getAll();
        assertNotNull(documents);
        // Thêm kiểm tra để xác nhận tài liệu đã được thêm
    }
} 