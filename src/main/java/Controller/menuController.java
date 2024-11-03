package Controller;
import java.io.IOException;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import DAO.BookDao;
import DAO.BorrowDao;
import DAO.UserDao;
import googleAPI.GoogleApiBookController;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Borrow;
import model.Document;
import model.User;
import thread.ThreadManager;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import googleAPI.*;

public class menuController {
    private static menuController instance;

    public static menuController getInstance() {
        if (instance == null) {
            instance = new menuController();
        }
        return instance;
    }

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane documentTab;

    @FXML
    private BorderPane managementTab;

    @FXML 
    private StackPane managementStackPane;

    @FXML
    private BorderPane borrowAndReturnTab;
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
    private Label scoreLabel;

    @FXML
    private TextArea reviewTextArea;

    @FXML
    private Label accuracyLabel;

    @FXML
    private void initialize() {

        instance = this;

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

                // Fetch score and review from GoogleApiBookController
                ThreadManager.execute(() -> {
               
                        String bookName = newSelection.getName();
                        String authorName = newSelection.getAuthor();
                        System.out.println(bookName);
                        System.out.println(authorName);
                        BookInfo bookinfo = googleAPI.GoogleApiBookController.getBookInfo(bookName);
                        String score = bookinfo.getRating();
                        String review = bookinfo.getDescription();
                        String doChinhXac;
                        if (bookinfo.truly) doChinhXac = "Cao";
                        else {
                            doChinhXac = "Thấp";
                        }
                        System.out.println(review);
                        System.out.println(score);

                        Platform.runLater(() -> {
                            reviewTextArea.setText(review);
                            scoreLabel.setText(String.valueOf(score));
                            accuracyLabel.setText(doChinhXac);
                        });
                    
                });
            } else {
                taDocumentDetails.clear();
                scoreLabel.setText("");
                reviewTextArea.clear();
            }
        });
        
        // Populate ComboBoxes with data from UserDao and BookDao
        ThreadManager.execute(() -> {
            List<User> users = UserDao.getInstance().getAll();
            List<Document> documents = BookDao.getInstance().getAll();
            
            Platform.runLater(() -> {
                cbMembers.getItems().clear();
                if (users != null) {
                    cbMembers.getItems().addAll(users.stream()
                        .map((User user) -> user.getUserId() + " - " + user.getUsername())
                        .collect(Collectors.toList()));
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

        //add subscene
         // Tải và thêm các cảnh phụ vào managementStackPane
         try {
           

            FXMLLoader addBookLoader = new FXMLLoader(getClass().getResource("/view/add.fxml"));
            Parent addBookPane = addBookLoader.load();
            managementStackPane.getChildren().add(addBookPane);

            FXMLLoader deleteBookLoader = new FXMLLoader(getClass().getResource("/view/delete.fxml"));
            Parent deleteBookPane = deleteBookLoader.load();
            managementStackPane.getChildren().add(deleteBookPane);

            FXMLLoader editBookLoader = new FXMLLoader(getClass().getResource("/view/edit.fxml"));
            Parent editBookPane = editBookLoader.load();
            managementStackPane.getChildren().add(editBookPane);

            FXMLLoader manageMembersLoader = new FXMLLoader(getClass().getResource("/view/member_management.fxml"));
            Parent manageMembersPane = manageMembersLoader.load();
            managementStackPane.getChildren().add(manageMembersPane);

            // Đặt cảnh báo là pane mặc định hiển thị
            managementStackPane.getChildren().get(0).setVisible(true);
            for (int i = 1; i < managementStackPane.getChildren().size(); i++) {
                managementStackPane.getChildren().get(i).setVisible(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Lỗi", "Không thể tải subscene: " + e.getMessage());
            Platform.exit();
        }

        
        }

        @FXML
        private void showDocumentListTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        documentTab.setVisible(true);
        showPane(0);
        
        }

        @FXML
        private void showManagementTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        managementTab.setVisible(true);
        showPane(0);
        }

        @FXML
        private void showBorrowReturnTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        borrowAndReturnTab.setVisible(true);
        showPane(0);
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
    
    public void showPane(int index) {
        for (int i = 0; i < managementStackPane.getChildren().size(); i++) {
            managementStackPane.getChildren().get(i).setVisible(i == index);
        }
    }

    @FXML
    private void onAddDocument() {
        try {
            showPane(1);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Lỗi không xác định", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleReload() {
        refreshDocumentList();
        loadBorrowedDocuments();
        cbMembers.getItems().clear();
        cbDocuments.getItems().clear();
        
        ThreadManager.execute(() -> {
            List<User> users = UserDao.getInstance().getAll();
            List<Document> documents = BookDao.getInstance().getAll();
            
            Platform.runLater(() -> {
                if (users != null) {
                    cbMembers.getItems().addAll(users.stream()
                        .map((User user) -> user.getUserId() + " - " + user.getUsername())
                        .collect(Collectors.toList()));
                }
                
                if (documents != null) {
                    cbDocuments.getItems().addAll(documents.stream().map(Document::getName).collect(Collectors.toList()));
                }
            });
        });
    }
    
    public void refreshTableView() {
        ThreadManager.execute(() -> {
            try {
                List<Document> documents = BookDao.getInstance().getAll();
                Platform.runLater(() -> {
                    tvDocuments.getItems().clear();
                    tvDocuments.getItems().addAll(documents);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showErrorAlert("Lỗi cập nhật", "Không thể cập nhật danh sách tài liệu: " + e.getMessage()));
            }
        });
    }
    
    @FXML
    private void onDeleteDocument() {
        showPane(2);
    }

    
    @FXML
    private void onEditDocument() {
     
            showPane(3);
           
            
      
    }
    
   @FXML
private void onManageMembers() {
    showPane(4);
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
            tvDocuments.setItems(allDocuments); // Đưa bảng về trạng thái ban đầu
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
        if (selectedMemberIdString != null && selectedMemberIdString.contains(" - ")) {
            selectedMemberIdString = selectedMemberIdString.split(" - ")[0];
        }

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

        Document selectedDocument = BookDao.getInstance().getByName(selectedBorrow.getBookname());
        if (selectedDocument == null) {
            showErrorAlert("Lỗi", "Không tìm thấy tài liệu.");
            return;
        }

        BorrowDao.getInstance().delete(selectedBorrow);
        
        // Update the document quantity
        selectedDocument.setQuantity(selectedDocument.getQuantity() + 1);
        BookDao.getInstance().update(selectedDocument);

        showInfoAlert("Thành công", "Tài liệu đã được trả thành công.");
        refreshDocumentList();
        loadBorrowedDocuments();
    }

    private void refreshDocumentList() {
        ThreadManager.execute(() -> {
            List<Document> documents = BookDao.getInstance().getAll();
            Platform.runLater(() -> {
                tvDocuments.setItems(FXCollections.observableArrayList(documents));
                tvDocuments.refresh(); 
            });
        });
    }

}
