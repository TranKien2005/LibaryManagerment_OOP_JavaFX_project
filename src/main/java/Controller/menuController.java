package Controller;
import java.util.List;

import DAO.BookDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Document;
import thread.ThreadManager;

public class menuController {
    @FXML
    private TableView<Document> tvDocuments;
    
    @FXML
    private TableColumn<Document, String> colName;
    
    @FXML
    private TableColumn<Document, String> colAuthor;
    
    @FXML
    private TableColumn<Document, String> colCategory;
    
    @FXML
    private TableColumn<Document, String> colPublisher;
    
    @FXML
    private TableColumn<Document, Integer> colYear;
    
    @FXML
    private TableColumn<Document, Integer> colQuantity;
    
    @FXML
    private TextArea taDocumentDetails;
    
    @FXML
    private ComboBox<String> cbMembers;
    
    @FXML
    private ComboBox<String> cbDocuments;
    
    @FXML
    private TextField tfFilter;
    
    @FXML
    private void initialize() {
        // Khởi tạo các cột cho TableView
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        ThreadManager.execute(() -> {
            List<Document> documents = BookDao.getInstance().getAll();
            Platform.runLater(() -> tvDocuments.setItems(FXCollections.observableArrayList(documents)));
        });
        
        
        // Thêm listener cho việc chọn tài liệu
        tvDocuments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String details = String.format(
                    "Tên: %s\n" +
                    "Tác giả: %s\n" +
                    "Thể loại: %s\n" +
                    "Nhà xuất bản: %s\n" +
                    "Năm xuất bản: %d\n" +
                    "Số lượng: %d",
                    newSelection.getName(),
                    newSelection.getAuthor(),
                    newSelection.getCategory(),
                    newSelection.getPublisher(),
                    newSelection.getYear(),
                    newSelection.getQuantity()
                );
                taDocumentDetails.setText(details);
            } else {
                taDocumentDetails.clear();
            }
        });
    }
    
    
    
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void onAddDocument() {
        // Xử lý thêm tài liệu
    }
    
    @FXML
    private void onDeleteDocument() {
        // Xử lý xóa tài liệu
    }
    
    @FXML
    private void onEditDocument() {
        // Xử lý sửa tài liệu
    }
    
    @FXML
    private void onManageMembers() {
        // Xử lý quản lý thành viên
    }
    
    @FXML
    private void onBorrowDocument() {
        // Xử lý mượn tài liệu
    }
    
    @FXML
    private void onReturnDocument() {
        // Xử lý trả tài liệu
    }
    
    @FXML
    public void handleFilterAction() {
        String filterText = tfFilter.getText().toLowerCase().trim();
        ObservableList<Document> allDocuments = FXCollections.observableArrayList(BookDao.getInstance().getAll());
        ObservableList<Document> filteredDocuments = FXCollections.observableArrayList();

        for (Document doc : allDocuments) {
            if (doc.getName().toLowerCase().contains(filterText)) {
                filteredDocuments.add(doc);
            }
        }

        tvDocuments.setItems(filteredDocuments);

        if (filteredDocuments.isEmpty()) {
            showInfoAlert("Không tìm thấy", "Không có tài liệu nào phù hợp với từ khóa tìm kiếm.");
        }
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
