// package Controller;
// import model.Document;
// import org.junit.Before;
// import org.junit.Test;
// import static org.junit.Assert.*;

// public class EditControllerTest {

//     private EditController editController;

//     @Before
//     public void setUp() {
//         editController = new EditController();
//     }

//     @Test
//     public void testLoadDocumentDetails() {
//         Document doc = new Document(1, "Title", "Author", "Category", "Publisher", 2023, 10);
//         editController.loadDocumentDetails(doc.getBookID());
//         assertEquals("Title", editController.getTitleField().getText());
//         assertEquals("Author", editController.getAuthorField().getText());
//     }

//     @Test
//     public void testHandleEditDocument() {
//         // Set up fields with valid data
//         editController.getTitleField().setText("New Title");
//         editController.getAuthorField().setText("New Author");
//         editController.getCategoryField().setText("New Category");
//         editController.getPublisherField().setText("New Publisher");
//         editController.getYearField().setText("2023");
//         editController.getQuantityField().setText("5");

//         // Call the method
//         editController.handleEditDocument();

//         // Verify the document was updated correctly
//         // (Assuming you have a way to verify the update)
//     }

//     @Test
//     public void testHandleCancel() {
//         editController.handleCancel();
//         assertEquals("", editController.getTitleField().getText());
//         assertEquals("", editController.getAuthorField().getText());
//     }
// } 