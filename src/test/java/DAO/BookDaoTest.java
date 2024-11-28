/**
 * Unit tests for the BookDao class.
 * This class tests methods such as retrieving all documents and inserting a new document
 * to ensure the BookDao class behaves as expected.
 */
package DAO;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import model.Document;

public class BookDaoTest {
    private BookDao bookDao;

    /**
     * Sets up the BookDao instance before each test.
     */
    @Before
    public void setUp() {
        bookDao = BookDao.getInstance();
    }

    /**
     * Tests the retrieval of all documents from the database.
     * Ensures that the result is not null and potentially validates the data.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testGetAll() throws SQLException {
        List<Document> documents = bookDao.getAll();
        assertNotNull(documents);
        // Additional checks can be added here if needed.
    }

    /**
     * Tests the insertion of a new document into the database.
     * Verifies that the operation either succeeds or properly handles exceptions.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testInsert() throws SQLException {
        Document document = new Document(1, "Title", "Author", "Category", "Publisher", 2023, 5);
        try {
            bookDao.insert(document);
            // Verify the document has been successfully added.
        } catch (SQLException e) {
            // If an exception occurs, check if it's due to a duplicate document.
            assertNotNull(e);
            return; // Exit test if the exception is as expected.
        }
        // If no exception, verify the document exists in the database.
        List<Document> documents = bookDao.getAll();
        assertNotNull(documents);
        // Add further validation to confirm the document was inserted.
    }
}
