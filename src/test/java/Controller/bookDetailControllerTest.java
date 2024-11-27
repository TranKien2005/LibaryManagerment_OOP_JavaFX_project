package Controller;
import model.Document;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class bookDetailControllerTest {

    private BookDetailController bookDetailController;

    @Before
    public void setUp() {
        bookDetailController = new BookDetailController();
    }

    @Test
    public void testSetBookDetails() {
        Document book = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        BookDetailController.setBookDetails(book);
        assertEquals("Title", bookDetailController.getBookTitleLabel().getText());
        assertEquals("Author", bookDetailController.getBookAuthorLabel().getText());
    }
} 