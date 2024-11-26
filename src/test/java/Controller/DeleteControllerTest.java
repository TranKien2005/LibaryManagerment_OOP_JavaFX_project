package Controller;
import model.Document;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeleteControllerTest {

    private DeleteController deleteController;

    @Before
    public void setUp() {
        deleteController = new DeleteController();
    }

    @Test
    public void testXoaTaiLieu() {
        Document doc = new Document(1, "Title", "Author", "Category", "Publisher", 2023, 10);
        deleteController.getNameField().setText("1 - Title");
        deleteController.xoaTaiLieu();

        // Verify that the document is deleted
        // (Assuming you have a way to verify the deletion)
    }

    @Test
    public void testCapNhatBangTaiLieu() {
        deleteController.capNhatBangTaiLieu();
        // Verify that the table view is updated with the latest documents
    }
} 