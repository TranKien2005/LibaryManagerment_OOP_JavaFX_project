package Controller;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import DAO.BookDao;
import DAO.BorrowDao;
import DAO.UserDao;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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
    private TableView<Borrow> tvBorrowedDocuments;
    
    @FXML
    private TableColumn<Borrow, String> colMember;
    
    @FXML
    private TableColumn<Borrow, String> colDocument;
    
    @FXML
    private TableColumn<Borrow, LocalDate> colBorrowDate;
    
    @FXML
    private TableColumn<Borrow, LocalDate> colReturnDate;
    
    @FXML
    private TableColumn<Borrow, String> colStatus;

    @FXML
    private void initialize() {
        dpBorrowDate.setValue(LocalDate.now());
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

        // Initialize borrowed documents table
        colMember.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getUser_id())));
        colDocument.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBookname()));
        colBorrowDate.setCellValueFactory(cellData -> {
            LocalDate borrowDate = cellData.getValue().getBorrow_date();
            return new SimpleObjectProperty<>(borrowDate);
        });
        colReturnDate.setCellValueFactory(cellData -> {
            LocalDate returnDate = cellData.getValue().getReturn_date();
            return new SimpleObjectProperty<>(returnDate);
        });
        colStatus.setCellValueFactory(cellData -> {
            LocalDate returnDate = cellData.getValue().getReturn_date();
            LocalDate now = LocalDate.now();
            if (returnDate == null) {
                return new SimpleStringProperty("Đang mượn");
            }
            return new SimpleStringProperty(returnDate.isBefore(now) ? "Quá hạn" : "Đang mượn");
        });

        loadBorrowedDocuments();
    }

    private void loadBorrowedDocuments() {
        ThreadManager.execute(() -> {
            List<Borrow> borrowedDocuments = BorrowDao.getInstance().getAll();
            Platform.runLater(() -> {
                tvBorrowedDocuments.setItems(FXCollections.observableArrayList(borrowedDocuments));
            });
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/member_management.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tvDocuments.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading member_management.fxml: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Error", "Unable to load the member management view. Please try again.");
        }
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

        LocalDate borrowDate = dpBorrowDate.getValue();
        LocalDate returnDate = dpReturnDate.getValue();
        
        if (borrowDate == null || returnDate == null) {
            showErrorAlert("Lỗi", "Vui lòng chọn ngày mượn và ngày trả.");
            return;
        }
        
        if (borrowDate.isAfter(returnDate)) {
            showErrorAlert("Lỗi", "Ngày mượn không thể sau ngày trả.");
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
        loadBorrowedDocuments(); // Refresh the borrowed documents list
    }

    @FXML
    private void handleReturnDocument() {
        Borrow selectedBorrow = tvBorrowedDocuments.getSelectionModel().getSelectedItem();
        
        if (selectedBorrow == null) {
            showErrorAlert("Lỗi", "Vui lòng chọn một tài liệu đã mượn từ bảng để trả.");
            return;
        }

        // Find the document by name
        Document selectedDocument = BookDao.getInstance().getByName(selectedBorrow.getBookname());
        if (selectedDocument == null) {
            showErrorAlert("Lỗi", "Không tìm thấy tài liệu.");
            return;
        }

        // Delete the borrow record
        BorrowDao.getInstance().delete(selectedBorrow);
        
        // Update the document quantity
        selectedDocument.setQuantity(selectedDocument.getQuantity() + 1);
        BookDao.getInstance().update(selectedDocument);

        showInfoAlert("Thành công", "Tài liệu đã được trả thành công.");
        refreshDocumentList();
        loadBorrowedDocuments(); // Refresh the borrowed documents list
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
