package Controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DAO.BookDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Document;
import util.ErrorDialog;
import util.ThreadManager;

public class DeleteController extends menuController {
    @FXML
    private TextField nameField;
    @FXML
    private TableView<Document> SearchView;
    @FXML
    private TableColumn<Document, String> tenColumn;
    @FXML
    private TableColumn<Document, String> idColumn;
    @FXML
    private ListView<String> suggestionListView;

    private final BookDao bookDao;

    public void setNameField(Document book) {
        nameField.setText(book.getBookID() + " - " + book.getTitle());
    }

    public DeleteController() {
        this.bookDao = BookDao.getInstance();
    }

    public List<Document> bookList = new ArrayList<>();

    @SuppressWarnings("unused")
    @FXML
    public void initialize() {
        tenColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));

        capNhatBangTaiLieu();

        final long[] lastTypingTime = { System.currentTimeMillis() };
        final long typingDelay = 100;

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            lastTypingTime[0] = System.currentTimeMillis();

            ThreadManager.execute(() -> {
                try {
                    Thread.sleep(typingDelay);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                    ErrorDialog.showError("Lỗi", e.getMessage(), (Stage) nameField.getScene().getWindow());
                }
                if (System.currentTimeMillis() - lastTypingTime[0] >= typingDelay) {
                    updateSuggestions(newValue);
                }
            });
        });

        suggestionListView.setOnMouseClicked(event -> {
            String selectedDocumentID = suggestionListView.getSelectionModel().getSelectedItem();
            if (selectedDocumentID != null) {
                nameField.setText(selectedDocumentID);
                suggestionListView.setVisible(false);
                nameField.getParent().requestFocus();
            }
        });
    }

    @FXML
    public void xoaTaiLieu() {
        String selectedDocumentID = nameField.getText().trim();
        String[] parts = selectedDocumentID.split(" - ");
        if (parts.length > 0) {
            selectedDocumentID = parts[0];
        }
        if (selectedDocumentID.isEmpty()) {

            util.ErrorDialog.showError("Lỗi", "Vui lòng chọn tài liệu.", null);
            return;
        }

        int documentID = Integer.parseInt(selectedDocumentID);

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn xóa tài liệu này không?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    bookDao.delete(documentID);
                    util.ErrorDialog.showSuccess("Xóa thành công", "Tài liệu đã được xóa.", null);
                    capNhatBangTaiLieu();
                    nameField.clear();
                } catch (SQLException e) {
                    e.printStackTrace();
                    util.ErrorDialog.showError("Lỗi", e.getMessage(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                    util.ErrorDialog.showError("Lỗi", e.getMessage(), null);
                }
            }
        });
    }

    private void capNhatBangTaiLieu() {
        try {
            bookList = BookDao.getInstance().getAll();
            SearchView.setItems(FXCollections.observableArrayList(bookList));
        } catch (SQLException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Lỗi", e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Lỗi", e.getMessage(), null);
        }
    }

    private void updateSuggestions(String searchText) {

        if (searchText == null || searchText.isEmpty()) {
            suggestionListView.setVisible(false);
            return;
        }

        ObservableList<String> suggestions = FXCollections.observableArrayList();
        for (Document doc : bookList) {
            if (doc.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                suggestions.add(doc.getBookID() + " - " + doc.getTitle());
            }
        }

        javafx.application.Platform.runLater(() -> {
            suggestionListView.setItems(suggestions);
            suggestionListView.setVisible(!suggestions.isEmpty());
        });

    }

    @FXML
    private void handleCancel() {
        nameField.clear();
        suggestionListView.setVisible(false);
        SearchView.setItems(FXCollections.observableArrayList(bookList));
    }

    @Override
    public void reload() {
        handleCancel();
    }

    public List<Document> getBookList() {
        return bookList;
    }

    public void setBookList(List<Document> bookList) {
        this.bookList = bookList;
    }

}
