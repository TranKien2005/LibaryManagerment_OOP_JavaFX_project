package DAO;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;

public class DatabaseConnectionTest {

    @Test
    public void testGetConnection() throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        assertNotNull(connection);
        // Kiểm tra xem kết nối có hợp lệ không
    }
} 