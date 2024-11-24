package Controller;

import model.Document;
import util.ErrorDialog;
import util.ThreadManager;
import DAO.BookDao;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.testing.json.AbstractJsonParserTest.E;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

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

    private BookDao bookDao;

    public DeleteController() {
        this.bookDao = BookDao.getInstance();
    }
    public List<Document> bookList = new ArrayList<>();
    @FXML
    public void initialize() {
        tenColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        
        
        capNhatBangTaiLieu();
        
        

        final long[] lastTypingTime = {System.currentTimeMillis()};
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
                            util.ErrorDialog.showError("Lỗi",  e.getMessage(), null);
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
            util.ErrorDialog.showError("Lỗi",  e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Lỗi",  e.getMessage(), null);
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

        public void reload() {
        handleCancel();
        }
    
   
}
