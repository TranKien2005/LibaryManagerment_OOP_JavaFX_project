package test;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private void initialize() {
        // Khởi tạo các cột cho TableView
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        // Tạo dữ liệu mẫu
        Thread thread = new Thread(() -> {
            ObservableList<Document> documents = Database.getInstance().getAllBooks();
            tvDocuments.setItems(documents);
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Thêm listener cho việc chọn tài liệu
        tvDocuments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                taDocumentDetails.setText(newSelection.toString());
            }
        });
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
}

