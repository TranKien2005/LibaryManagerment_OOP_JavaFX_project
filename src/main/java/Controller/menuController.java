package Controller;
import java.io.IOException;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import DAO.*;
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
import model.*;

import util.ThreadManager;
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
    private TableColumn<Document, Integer> colId;
    
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
    private TableColumn<Borrow, LocalDate> colActualReturnDate;

    @FXML
    private TableColumn<Borrow, Double> colDamagePercentage;

    @FXML
    private TableColumn<Borrow, Double> colPenaltyFee;

    @FXML
    private Label scoreLabel;

    @FXML
    private TextArea reviewTextArea;

    @FXML
    private Label accuracyLabel;

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox<String> cbSearchCriteria;

    @FXML
    private void initialize() {

        instance = this;

        dpBorrowDate.setValue(LocalDate.now());

        // Khởi tạo các cột cho TableView
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setCellValueFactory(cellData -> {
            Document document = cellData.getValue();
            int bookId = BookDao.getInstance().getID(document);
            return new SimpleObjectProperty<>(bookId);
        });
        colName.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("yearPublished"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        
      
        List<Document> documents = BookDao.getInstance().getAll();
        Platform.runLater(() -> tvDocuments.setItems(FXCollections.observableArrayList(documents)));
        
        
        
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
                    newSelection.getTitle(),
                    newSelection.getAuthor(),
                    newSelection.getCategory(),
                    newSelection.getPublisher(),
                    newSelection.getYearPublished(),
                    newSelection.getAvailableCopies()
                );
                taDocumentDetails.setText(details);

                // Fetch score and review from GoogleApiBookController
                ThreadManager.execute(() -> {
               
                        String bookName = newSelection.getTitle();
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
        
       
            List<User> users = UserDao.getInstance().getAll();
           
        
            
            Platform.runLater(() -> {
                
        
                if (users != null) {
                    cbMembers.getItems().addAll(users.stream()
                        .map(user -> user.getAccountID() + " - " + user.getFullName())
                        .collect(Collectors.toList()));
                }

                if (documents != null) {
                    cbDocuments.getItems().addAll(documents.stream()
                        .map(document -> BookDao.getInstance().getID(document) + " - " + document.getTitle())
                        .collect(Collectors.toList()));
                }
            });

           
           

           
        cbMembers.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                String newText = cbMembers.getEditor().getText().toLowerCase();
                ObservableList<String> filteredList = FXCollections.observableArrayList(users.stream()
                    .map(user -> user.getAccountID() + " - " + user.getFullName())
                    .filter(name -> name.toLowerCase().contains(newText))
                    .collect(Collectors.toList()));
                cbMembers.setItems(filteredList);
                cbMembers.show();
                break;
                default:
                   
            }
        });

        cbDocuments.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    String newText = cbDocuments.getEditor().getText().toLowerCase();
                    ObservableList<String> filteredList = FXCollections.observableArrayList(documents.stream()
                        .map(document -> BookDao.getInstance().getID(document) + " - " + document.getTitle())
                        .filter(title -> title.toLowerCase().contains(newText))
                        .collect(Collectors.toList()));
                    cbDocuments.setItems(filteredList);
                    cbDocuments.show();
                    break;
                default:
                    break;
            }
        });
       
  
    

    // Initialize borrowed documents table
    colMember.setCellValueFactory(cellData -> {
        int accountId = cellData.getValue().getAccountID();
        User user = UserDao.getInstance().get(accountId);
        String fullName = user != null ? user.getFullName() : "";
        return new SimpleStringProperty(accountId + " - " + fullName);
    });
    colDocument.setCellValueFactory(cellData -> {
        int bookId = cellData.getValue().getBookID();
        Document document = BookDao.getInstance().get(bookId);
        String bookTitle = document != null ? document.getTitle() : "";
        return new SimpleStringProperty(bookId + " - " + bookTitle);
    });
    colBorrowDate.setCellValueFactory(cellData -> {
        LocalDate borrowDate = cellData.getValue().getBorrowDate();
        return new SimpleObjectProperty<>(borrowDate);
    });
    colReturnDate.setCellValueFactory(cellData -> {
        LocalDate returnDate = cellData.getValue().getExpectedReturnDate();
        return new SimpleObjectProperty<>(returnDate);
    });
    colStatus.setCellValueFactory(cellData -> {
        Borrow borrow = cellData.getValue();
        LocalDate expectedReturnDate = borrow.getExpectedReturnDate();
        LocalDate actualReturnDate = null;
        Return returnRecord = ReturnDao.getInstance().get(BorrowDao.getInstance().getID(borrow));
        if (returnRecord != null) {
            actualReturnDate = returnRecord.getReturnDate();
        }
        String status;
        if (actualReturnDate != null) {
            status = borrow.getStatus();
        } else if (LocalDate.now().isAfter(expectedReturnDate)) {
            status = "Quá hạn";
        } else {
            status = borrow.getStatus();
        }
        return new SimpleStringProperty(status);
    });

    colActualReturnDate.setCellValueFactory(cellData -> {
        Borrow borrow = cellData.getValue();
        BorrowDao borrowDao = BorrowDao.getInstance();
        int borrowID = borrowDao.getID(borrow);
        Return returnRecord = ReturnDao.getInstance().get(borrowID);
        LocalDate actualReturnDate = null;
        if (returnRecord != null) {
            actualReturnDate = returnRecord.getReturnDate();
        }
        return new SimpleObjectProperty<>(actualReturnDate);
    });

    colDamagePercentage.setCellValueFactory(cellData -> {
        Borrow borrow = cellData.getValue();
        BorrowDao borrowDao = BorrowDao.getInstance();
        int borrowID = borrowDao.getID(borrow);
        Return returnRecord = ReturnDao.getInstance().get(borrowID);
        if (returnRecord != null) {
            Integer damagePercentage = returnRecord.getDamagePercentage();
            Double damagePercentageDouble = damagePercentage != null ? damagePercentage.doubleValue() : null;
            return new SimpleObjectProperty<>(damagePercentageDouble);
        } else {
            return new SimpleObjectProperty<>(null);
        }
    });

    colPenaltyFee.setCellValueFactory(cellData -> {
        Borrow borrow = cellData.getValue();
        BorrowDao borrowDao = BorrowDao.getInstance();
        int borrowID = borrowDao.getID(borrow);
        Return returnRecord = ReturnDao.getInstance().get(borrowID);
        if (returnRecord != null) {
            LocalDate expectedReturnDate = borrow.getExpectedReturnDate();
            LocalDate actualReturnDate = returnRecord.getReturnDate();
            int damagePercentage = returnRecord.getDamagePercentage();
            double penaltyFee = 0.0;

            if (actualReturnDate != null && actualReturnDate.isAfter(expectedReturnDate)) {
                long daysLate = java.time.temporal.ChronoUnit.DAYS.between(expectedReturnDate, actualReturnDate);
                penaltyFee = daysLate * 1.0 + damagePercentage * 0.5; // Example calculation: $1 per day late + $0.5 per damage percentage
            }

            return new SimpleObjectProperty<>(penaltyFee);
        } else {
            return new SimpleObjectProperty<>(0.0);
        }
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
      
            List<Borrow> borrowedDocuments = BorrowDao.getInstance().getAll();
            Platform.runLater(() -> {
                tvBorrowedDocuments.setItems(FXCollections.observableArrayList(borrowedDocuments));
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
            if (doc.getTitle().toLowerCase().contains(filterText)) {
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
        cbDocuments.getParent().requestFocus();
        String nameDocument = cbDocuments.getValue();
        nameDocument = nameDocument.split(" - ")[0];
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
    
        int selectedDocumentId = Integer.parseInt(nameDocument);
        Document selectedDocument = BookDao.getInstance().get(selectedDocumentId);
        if (selectedDocument == null) {
            showErrorAlert("Lỗi", "Không tìm thấy tài liệu.");
            return;
        }
    
        if (selectedDocument.getAvailableCopies() <= 0) {
            showErrorAlert("Lỗi", "Tài liệu này hiện không có sẵn để mượn.");
            return;
        }
    
        User user = UserDao.getInstance().get(selectedMemberId);
        if (user == null) {
            showErrorAlert("Lỗi", "Không tìm thấy thành viên.");
            return;
        }
    
        Borrow newBorrow = new Borrow(
            selectedMemberId,
            selectedDocumentId,
            borrowDate,
            returnDate,
            "Borrowed"
        );
    
        BorrowDao.getInstance().insert(newBorrow);
       
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

        // Check if the document has already been returned
        Return existingReturnRecord = ReturnDao.getInstance().get(BorrowDao.getInstance().getID(selectedBorrow));
        if (existingReturnRecord != null) {
            showErrorAlert("Lỗi", "Tài liệu này đã được trả trước đó.");
            return;
        }

        Document selectedDocument = BookDao.getInstance().get(selectedBorrow.getBookID());
        if (selectedDocument == null) {
            showErrorAlert("Lỗi", "Không tìm thấy tài liệu.");
            return;
        }

        int damagePercentage = (int) (Math.random() * 100); // Random damage percentage between 0 and 100
        Return returnRecord = new Return(BorrowDao.getInstance().getID(selectedBorrow), 
        LocalDate.now(), damagePercentage);
        ReturnDao.getInstance().insert(returnRecord);
        showInfoAlert("Thành công", "Tài liệu đã được trả thành công.");
        refreshDocumentList();
        loadBorrowedDocuments();
        }

            private void refreshDocumentList() {
          
                List<Document> documents = BookDao.getInstance().getAll();
                Platform.runLater(() -> {
                    tvDocuments.setItems(FXCollections.observableArrayList(documents));
                    tvDocuments.refresh(); 
                });

         
        }
    
    

        @FXML
        private void handleRefreshAction() {
           
                refreshDocumentList();
                cbMembers.setValue("");
                cbDocuments.setValue("");

               
               
           
        }

        @FXML
        private void handleSearchAction() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String searchCriteria = cbSearchCriteria.getValue();

        if (searchCriteria == null || searchText.isEmpty()) {
            showErrorAlert("Lỗi", "Vui lòng nhập nội dung tìm kiếm và chọn tiêu chí tìm kiếm.");
            return;
        }

        if (searchCriteria.equals("Thành viên")) {
            ObservableList<Borrow> allBorrows = FXCollections.observableArrayList(BorrowDao.getInstance().getAll());
            ObservableList<Borrow> filteredBorrows = allBorrows.stream()
                .filter(borrow -> {
                    User user = UserDao.getInstance().get(borrow.getAccountID());
                    return user != null && user.getFullName().toLowerCase().contains(searchText);
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

            tvBorrowedDocuments.setItems(filteredBorrows);

            if (filteredBorrows.isEmpty()) {
                showInfoAlert("Không tìm thấy", "Không có thành viên nào phù hợp với từ khóa tìm kiếm.");
                tvBorrowedDocuments.setItems(allBorrows); // Đưa bảng về trạng thái ban đầu
            }
            } else if (searchCriteria.equals("Tài liệu")) {
            ObservableList<Borrow> allBorrows = FXCollections.observableArrayList(BorrowDao.getInstance().getAll());
            ObservableList<Borrow> filteredBorrows = allBorrows.stream()
                .filter(borrow -> {
                    Document document = BookDao.getInstance().get(borrow.getBookID());
                    return document != null && document.getTitle().toLowerCase().contains(searchText);
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

            tvBorrowedDocuments.setItems(filteredBorrows);

            if (filteredBorrows.isEmpty()) {
                showInfoAlert("Không tìm thấy", "Không có tài liệu nào phù hợp với từ khóa tìm kiếm.");
                tvBorrowedDocuments.setItems(allBorrows); // Đưa bảng về trạng thái ban đầu
            }
        }
    }
}
