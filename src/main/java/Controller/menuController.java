package Controller;
import java.util.List;
import java.util.stream.Collectors;

import DAO.BookDao;
import DAO.BorrowDao;
import DAO.UserDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Borrow;
import model.Document;
import model.User;
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
    private DatePicker dpReturnDate;
    
    @FXML
    private DatePicker dpBorrowDate;
    
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
        
        // Populate ComboBoxes with data from UserDao and BookDao
        ThreadManager.execute(() -> {
            List<User> users = UserDao.getInstance().getAll();
            List<Document> documents = BookDao.getInstance().getAll();
            
            Platform.runLater(() -> {
                cbMembers.getItems().clear();
                if (users != null) {
                    cbMembers.getItems().addAll(users.stream().map(User::getUserId).map(String::valueOf).collect(Collectors.toList()));
                }
                
                cbDocuments.getItems().clear();
                if (documents != null) {
                    cbDocuments.getItems().addAll(documents.stream().map(Document::getName).collect(Collectors.toList()));
                }
            });
        });

        // Add AutoCompleteComboBoxListener to ComboBoxes
        new AutoCompleteComboBoxListener<>(cbMembers);
        new AutoCompleteComboBoxListener<>(cbDocuments);
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

    @FXML
    private void handleBorrowDocument() {
        String nameDocument = cbDocuments.getValue();
        String selectedMemberIdString = cbMembers.getValue();

        if (nameDocument == null || nameDocument.isEmpty()) {
            showErrorAlert("Lỗi", "Vui lòng chọn một tài liệu từ danh sách.");
            return;
        }

        if (selectedMemberIdString == null || selectedMemberIdString.isEmpty()) {
            showErrorAlert("Lỗi", "Vui lòng chọn một thành viên từ danh sách.");
            return;
        }

        Integer selectedMemberId;
        try {
            selectedMemberId = Integer.parseInt(selectedMemberIdString);
        } catch (NumberFormatException e) {
            showErrorAlert("Lỗi", "ID thành viên không hợp lệ.");
            return;
        }

        Document selectedDocument = BookDao.getInstance().getByName(nameDocument);
        if (selectedDocument == null) {
            showErrorAlert("Lỗi", "Không tìm thấy tài liệu.");
            return;
        }

        if (selectedDocument.getQuantity() <= 0) {
            showErrorAlert("Lỗi", "Tài liệu này hiện không có sẵn để mượn.");
            return;
        }

        User user = UserDao.getInstance().getAll().stream()
                .filter(u -> u.getUserId() == selectedMemberId)
                .findFirst()
                .orElse(null);

        if (user == null) {
            showErrorAlert("Lỗi", "Không tìm thấy thành viên.");
            return;
        }

        Borrow newBorrow = new Borrow(
            selectedMemberId,
            nameDocument,
            dpBorrowDate.getValue(),
            dpReturnDate.getValue()
        );

        BorrowDao.getInstance().insert(newBorrow);

        selectedDocument.setQuantity(selectedDocument.getQuantity() - 1);
        BookDao.getInstance().update(selectedDocument);

        showInfoAlert("Thành công", "Tài liệu đã được mượn thành công.");
        refreshDocumentList();
    }

    @FXML
    private void handleReturnDocument() {
        String selectedDocumentName = cbDocuments.getValue();
        String selectedMemberIdString = cbMembers.getValue();

        if (selectedDocumentName == null || selectedDocumentName.isEmpty() || selectedMemberIdString == null) {
            showErrorAlert("Lỗi", "Vui lòng chọn tài liệu và thành viên để trả.");
            return;
        }

        // Convert the selected member ID to Integer
        Integer selectedMemberId;
        try {
            selectedMemberId = Integer.parseInt(selectedMemberIdString);
        } catch (NumberFormatException e) {
            showErrorAlert("Lỗi", "ID thành viên không hợp lệ.");
            return;
        }

        // Find the document by name
        Document selectedDocument = BookDao.getInstance().getByName(selectedDocumentName);
        if (selectedDocument == null) {
            showErrorAlert("Lỗi", "Không tìm thấy tài liệu.");
            return;
        }

        // Tìm bản ghi mượn
        User user = UserDao.getInstance().getAll().stream()
                .filter(u -> u.getUserId() == selectedMemberId)
                .findFirst()
                .orElse(null);
        if (user == null) {
            showErrorAlert("Lỗi", "Không tìm thấy thành viên.");
            return;
        }
        Borrow borrowToReturn = new Borrow(selectedMemberId, selectedDocumentName, null, null);
        Borrow foundBorrow = BorrowDao.getInstance().get(borrowToReturn);

        if (foundBorrow == null) {
            showErrorAlert("Lỗi", "Không tìm thấy bản ghi mượn cho tài liệu và thành viên này.");
            return;
        }

        // Xóa bản ghi mượn khỏi JSON
        BorrowDao.getInstance().delete(foundBorrow);

        // Increase the quantity of the returned document by 1
        selectedDocument.setQuantity(selectedDocument.getQuantity() + 1);
        BookDao.getInstance().update(selectedDocument);

        showInfoAlert("Thành công", "Tài liệu đã được trả thành công.");
        refreshDocumentList();
    }

    private void refreshDocumentList() {
        ThreadManager.execute(() -> {
            List<Document> documents = BookDao.getInstance().getAll();
            Platform.runLater(() -> {
                tvDocuments.setItems(FXCollections.observableArrayList(documents));
                tvDocuments.refresh(); // Refresh the TableView to show updated quantities
            });
        });
    }

}
