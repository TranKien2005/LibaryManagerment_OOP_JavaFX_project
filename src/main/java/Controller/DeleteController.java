package Controller;

import model.Document;
import util.ThreadManager;
import DAO.BookDao;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import java.io.IOException;
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

    @FXML
    public void initialize() {
        tenColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        idColumn.setCellValueFactory(cellData -> {
            Document document = cellData.getValue();
            int bookId = BookDao.getInstance().getID(document);
            return new SimpleObjectProperty<>(String.valueOf(bookId));
        });
        
        
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
                    hienThiThongBao("Lỗi", "Vui lòng chọn tài liệu.");
                    return;
                }

                int documentID;
                try {
                    documentID = Integer.parseInt(selectedDocumentID);
                } catch (NumberFormatException e) {
                    hienThiThongBao("Lỗi", "ID tài liệu không hợp lệ.");
                    return;
                }
                Document documentToDelete = bookDao.get(documentID);
                if (documentToDelete == null) {
                    hienThiThongBao("Lỗi", "Tài liệu không tồn tại.");
                    return;
                }

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Xác nhận xóa");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Bạn có chắc chắn muốn xóa tài liệu này không?");
                
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        bookDao.delete(documentID);
                        hienThiThongBao("Xóa thành công", "Tài liệu đã được xóa.");
                        capNhatBangTaiLieu();
                        nameField.clear();
                    }
                });
            }

    private void capNhatBangTaiLieu() {
        SearchView.setItems(FXCollections.observableArrayList(bookDao.getAll()));
    }

    private void hienThiThongBao(String tieuDe, String noiDung) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
        }

        private void updateSuggestions(String searchText) {
       
            if (searchText == null || searchText.isEmpty()) {
            suggestionListView.setVisible(false);
            return;
            }

            ObservableList<String> suggestions = FXCollections.observableArrayList();
            for (Document doc : bookDao.getAll()) {
            if (doc.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                suggestions.add(bookDao.getID(doc) + " - " + doc.getTitle());
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
        SearchView.setItems(FXCollections.observableArrayList(bookDao.getAll()));
    }

    
   
}
