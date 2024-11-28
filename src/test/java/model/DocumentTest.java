package model;
import org.junit.Test;
import static org.junit.Assert.*;

public class DocumentTest {

    @Test
    public void testDocumentCreation() {
        Document doc = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        assertNotNull(doc);
        assertEquals("Title", doc.getTitle());
        assertEquals("Author", doc.getAuthor());
    }

    @Test
    public void testSetTitle() {
        Document doc = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        doc.setTitle("New Title");
        assertEquals("New Title", doc.getTitle());
    }

    @Test
    public void testSetAuthor() {
        Document doc = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        doc.setAuthor("New Author");
        assertEquals("New Author", doc.getAuthor());
    }

    @Test
    public void testSetCategory() {
        Document doc = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        doc.setCategory("New Category");
        assertEquals("New Category", doc.getCategory());
    }

    @Test
    public void testSetPublisher() {
        Document doc = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        doc.setPublisher("New Publisher");
        assertEquals("New Publisher", doc.getPublisher());
    }

    @Test
    public void testSetYearPublished() {
        Document doc = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        doc.setYearPublished(2024);
        assertEquals(2024, doc.getYearPublished());
    }

    @Test
    public void testSetAvailableCopies() {
        Document doc = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        doc.setAvailableCopies(15);
        assertEquals(15, doc.getAvailableCopies());
    }

    @Test
    public void testSetDescription() {
        Document doc = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        doc.setDescription("This is a test description.");
        assertEquals("This is a test description.", doc.getDescription());
    }

    @Test
    public void testSetRating() {
        Document doc = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        doc.setRating(4.5);
        assertEquals(4.5, doc.getRating(), 0.01);
    }

    @Test
    public void testSetReviewCount() {
        Document doc = new Document("Title", "Author", "Category", "Publisher", 2023, 10);
        doc.setReviewCount(5);
        assertEquals(5, doc.getReviewCount());
    }
} 