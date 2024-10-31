package Controller;

import DAO.BookDao;
import model.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.util.function.Consumer;

public class EditController extends menuController {
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> suggestionListView;
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

    private BookDao bookDao;
    private Consumer<Void> onEditSuccess; // Callback

    public EditController() {
        this.bookDao = BookDao.getInstance();
    }

    public void setOnEditSuccess(Consumer<Void> onEditSuccess) {
        this.onEditSuccess = onEditSuccess;
    }

    @FXML
    public void initialize() {
        suggestionListView.setVisible(false);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSuggestions(newValue);
        });

        suggestionListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadDocumentDetails(newSelection);
            }
        });
    }

    private void loadDocumentDetails(String documentName) {
        Document document = bookDao.get(new Document(documentName, null, null, null, 0, 0));
        if (document != null) {
            titleField.setText(document.getName());
            authorField.setText(document.getAuthor());
            categoryField.setText(document.getCategory());
            publisherField.setText(document.getPublisher());
            yearField.setText(String.valueOf(document.getYear()));
                quantityField.setText(String.valueOf(document.getQuantity()));
            }
        }

        @FXML
        private void handleSearch() {
            String searchText = searchField.getText().trim();
            updateSuggestions(searchText);
        }

        private void updateSuggestions(String searchText) {
            ObservableList<String> suggestions = FXCollections.observableArrayList();
            for (Document doc : bookDao.getAll()) {
                if (doc.getName().toLowerCase().contains(searchText.toLowerCase())) {
                    suggestions.add(doc.getName());
                }
            }

            suggestionListView.setItems(suggestions);
            suggestionListView.setVisible(true);
        }

        @FXML
        private void handleEditDocument() {
        try {
            // Lấy thông tin từ các trường và cập nhật tài liệu
            String title = titleField.getText();
            String author = authorField.getText();
            String category = categoryField.getText();
            String publisher = publisherField.getText();
            int year = Integer.parseInt(yearField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            Document updatedDocument = new Document(title, author, category, publisher, year, quantity);
            bookDao.update(updatedDocument);
            showAlert("Thành công", "Tài liệu đã được cập nhật.");

            if (onEditSuccess != null) {
            onEditSuccess.accept(null); // Gọi callback
            }
            handleBackToMenu();
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Năm và số lượng phải là số nguyên hợp lệ.");
        } catch (Exception e) {
            showAlert("Lỗi", "Đã xảy ra lỗi khi cập nhật tài liệu.");
        }
        }

        private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

     @FXML
    private void handleBackToMenu() {
        try {
            // Load the menu.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/menu.fxml"));
            Parent menuRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) searchField.getScene().getWindow();

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
