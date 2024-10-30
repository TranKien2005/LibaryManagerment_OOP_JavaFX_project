package Controller;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Document;
import DAO.BookDao;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.function.Consumer;

public class AddController extends menuController{
    
    @FXML
    private TextField titleField;
    
    @FXML
    private TextField authorField;
    
    @FXML
    private TextField categoryField;
    
    @FXML
    private TextField publisherField;
    
    @FXML
    private TextField yearField;
    
    @FXML
    private TextField quantityField;
    
    @FXML
    private Button addButton;
    
    private Consumer<Document> onAddListener;

    public void setOnAddListener(Consumer<Document> listener) {
        this.onAddListener = listener;
    }
    
    @FXML
    private void handleAddDocument() {
        // Lấy dữ liệu từ các trường nhập liệu
        String title = titleField.getText();
        String author = authorField.getText();
        String category = categoryField.getText();
        String publisher = publisherField.getText();
        
        int year;
        int quantity;
        
        try {
            year = Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Năm không đúng định dạng. Vui lòng nhập số nguyên.");
            return;
        }
        
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số lượng không đúng định dạng. Vui lòng nhập số nguyên.");
            return;
        }

        // Tạo đối tượng Document mới
        Document newDocument = new Document(title, author, category, publisher, year, quantity);

        if (onAddListener != null) {
            try {
                onAddListener.accept(newDocument);
                // Tiếp tục với việc thêm tài liệu nếu không có lỗi
            } catch (IllegalArgumentException e) {
                showAlert("Lỗi nhập liệu", e.getMessage());
                return;
            }
        }
        
        // Lưu tài liệu mới vào cơ sở dữ liệu
        boolean success = saveDocument(newDocument);

        if (success) {
            showAlert("Thành công", "Tài liệu đã được thêm thành công.");
            clearFields();
            closeWindow();
        } else {
            showAlert("Lỗi", "Không thể thêm tài liệu. Vui lòng thử lại.");
        }
    }
    
    private boolean saveDocument(Document document) {
        return BookDao.getInstance().add(document);
    }
    
    private void clearFields() {
        titleField.clear();
        authorField.clear();
        categoryField.clear();
        publisherField.clear();
        yearField.clear();
        quantityField.clear();
    }
    
    private void showAlert(String title, String content) {
        new LoginController().showAlert(title, content);
    }
    
    private void closeWindow() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void initialize() {
        System.out.println("AddController đã được khởi tạo");
    }

    @FXML
    private Button backButton;

    @FXML
    private void handleBackToMenu() {
        try {
            // Load the menu.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/menu.fxml"));
            Parent menuRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) backButton.getScene().getWindow();

            stage.getScene().setRoot(menuRoot);
            stage.setMaximized(true);
            // Buộc cập nhật lại bố cục sau khi thay đổi nội dung
           stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the main menu.");
        }
    }
}
