package Controller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;

import javafx.scene.control.Label;
import java.time.LocalDate;
import java.util.ArrayList;
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
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.*;
import util.ErrorDialog;
import util.ThreadManager;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import googleAPI.*;

public class menuController {
    private static menuController instance;
    private static int accountID;
    public static List<Document> bookList = new ArrayList<>();
    public static List<User> userList = new ArrayList<>();
    public static List<BorrowReturn> borrowReturnList = new ArrayList<>();
    public static List<Account> accountList = new ArrayList<>();
    public Image defaulImage = new Image(getClass().getResourceAsStream("/images/menu/coverArtUnknown.png"));
    public static int getAccountID() {
        return accountID;
    }

    public static void setAccountID(int accountID) {
        menuController.accountID = accountID;
    }

    public static menuController getInstance() {
        if (instance == null) {
            instance = new menuController();
        }
        return instance;
    }

    public void resetList() {
        try {
            bookList = BookDao.getInstance().getAll();
            userList = UserDao.getInstance().getAll();
            borrowReturnList = BorrowReturnDAO.getInstance().getAll();
            accountList = AccountDao.getInstance().getAll();
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error",  e.getMessage(), null);
        } catch (Exception e) {
            util.ErrorDialog.showError("Error", e.getMessage(), null);
        }
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
    private TableView<BorrowReturn> tvBorrowedDocuments;

    @FXML 
    private TableColumn<BorrowReturn, Integer> colBorrowId;
    
    @FXML
    private TableColumn<BorrowReturn, String> colMember;
    
    @FXML
    private TableColumn<BorrowReturn, String> colDocument;
    
    @FXML
    private TableColumn<BorrowReturn, LocalDate> colBorrowDate;
    
    @FXML
    private TableColumn<BorrowReturn, LocalDate> colReturnDate;
    
    @FXML
    private TableColumn<BorrowReturn, String> colStatus;

    @FXML
    private TableColumn<BorrowReturn, LocalDate> colActualReturnDate;

    @FXML
    private TableColumn<BorrowReturn, Double> colDamagePercentage;

    @FXML
    private TableColumn<BorrowReturn, Double> colPenaltyFee;

    @FXML
    private Label scoreLabel;

    @FXML
    private TextArea reviewTextArea;

    @FXML
    private Label reviewCountLabel;

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox<String> cbSearchCriteria;

    @FXML
    private ImageView bookCoverImageView;

    @FXML
    private Label userName;
   

    @FXML
    private void initialize() {

        instance = this;
        
        try {
            Account account = AccountDao.getInstance().get(accountID);
            if (account != null) {
            if (account.getAccountType().equals("User")) {
                User currentUser = UserDao.getInstance().get(accountID);
                if (currentUser != null) {
                userName.setText("User: " + currentUser.getFullName());
                }
            } else {
                Manager currentUser = ManagerDao.getInstance().get(accountID);
                if (currentUser != null) {
                userName.setText("Manager: " + currentUser.getFullName());
                }
            }
            }
            resetList();
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error",  e.getMessage(), null);
        } catch (Exception e) {
            util.ErrorDialog.showError("Error",  e.getMessage(), null);
        }

        dpBorrowDate.setValue(LocalDate.now());

        // Khởi tạo các cột cho TableView
        colId.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("yearPublished"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        
        Platform.runLater(() -> tvDocuments.setItems(FXCollections.observableArrayList(bookList)));
        
        
        
        // Thêm listener cho việc chọn tài liệu
        tvDocuments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            taDocumentDetails.clear();
            reviewTextArea.clear();
            scoreLabel.setText("");
            reviewCountLabel.setText("");
            bookCoverImageView.setImage(null);
            if (newSelection != null) {
                
                

              
               
                    String bookName = newSelection.getTitle();
                    String authorName = newSelection.getAuthor();
                    String doChinhXac;
                    InputStream imageStream = newSelection.getCoverImage();
                    
                   
                        reviewTextArea.setText(newSelection.getDescription());
                        scoreLabel.setText(String.valueOf(newSelection.getRating()));
                        reviewCountLabel.setText(String.valueOf(newSelection.getReviewCount()));
                        if (imageStream != null) {
                            Image image = new Image(imageStream);
                            try {
                                imageStream.reset();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            bookCoverImageView.setImage(image);

                        }
                        else {
                            bookCoverImageView.setImage(defaulImage);
                        }
            } 
            
        });
        
       
           
           
        
            
            Platform.runLater(() -> {
                
        
                if (userList != null) {
                    cbMembers.getItems().addAll(userList.stream()
                        .map(user -> user.getAccountID() + " - " + user.getFullName())
                        .collect(Collectors.toList()));
                }

                if (bookList != null) {
                    cbDocuments.getItems().addAll(bookList.stream()
                            .map(document -> document.getBookID() + " - " + document.getTitle())
                        .collect(Collectors.toList()));
                }
            });

           
           

           
        cbMembers.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                String newText = cbMembers.getEditor().getText().toLowerCase();
                ObservableList<String> filteredList = FXCollections.observableArrayList(userList.stream()
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
                    ObservableList<String> filteredList = FXCollections.observableArrayList(bookList.stream()
                        .map(document -> document.getBookID() + " - " + document.getTitle())
                        .filter(title -> title.toLowerCase().contains(newText))
                        .collect(Collectors.toList()));
                    cbDocuments.setItems(filteredList);
                    cbDocuments.show();
                    break;
                default:
                break;
            }
        });
       
  
    
       
    loadBorrowedDocuments();
    // Initialize borrowed documents table
    colBorrowId.setCellValueFactory(new PropertyValueFactory<>("borrowID"));
    colMember.setCellValueFactory(new PropertyValueFactory<>("member"));
    colDocument.setCellValueFactory(new PropertyValueFactory<>("book"));
    colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
    colReturnDate.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
    colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    colActualReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
    colDamagePercentage.setCellValueFactory(new PropertyValueFactory<>("damagePercentage"));
    colPenaltyFee.setCellValueFactory(new PropertyValueFactory<>("penaltyFee"));

    Platform.runLater(() -> tvBorrowedDocuments.setItems(FXCollections.observableArrayList(borrowReturnList)));


    

   
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
            util.ErrorDialog.showError("Lỗi", "Không thể tải subscene: " + e.getMessage(), null);
            Platform.exit();
        }

        
        }

        private void loadBorrowedDocuments() {
            ObservableList<BorrowReturn> observableBorrowedDocuments = FXCollections.observableArrayList(borrowReturnList);
            tvBorrowedDocuments.setItems(observableBorrowedDocuments);
        }

        private void refreshDocumentList() {
            ObservableList<Document> updatedBookList = FXCollections.observableArrayList(bookList);
            tvDocuments.setItems(updatedBookList);
        }

        @FXML
        private void showDocumentListTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        documentTab.setVisible(true);
        showPane(0);
        handleReload();
        
        }

        @FXML
        private void showManagementTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        managementTab.setVisible(true);
        showPane(0);
        handleReload();
        }

        @FXML
        private void showBorrowReturnTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        borrowAndReturnTab.setVisible(true);
        showPane(0);
        handleReload();
        }

        

        
    
    
    public void showPane(int index) {
        for (int i = 0; i < managementStackPane.getChildren().size(); i++) {
            managementStackPane.getChildren().get(i).setVisible(i == index);
        }
    }

    @FXML
    private void onAddDocument() {
            try {
                FXMLLoader addBookLoader = new FXMLLoader(getClass().getResource("/view/add.fxml"));
                Parent addBookPane = addBookLoader.load();
                managementStackPane.getChildren().set(1, addBookPane);
            } catch (IOException e) {
                e.printStackTrace();
                util.ErrorDialog.showError("Lỗi", "Không thể tải subscene: " + e.getMessage(), null);
                Platform.exit();
            }
            showPane(1);
    }

    
    

    
    @FXML
    private void onDeleteDocument() {
        try {
            FXMLLoader deleteBookLoader = new FXMLLoader(getClass().getResource("/view/delete.fxml"));
            Parent deleteBookPane = deleteBookLoader.load();
            managementStackPane.getChildren().set(2, deleteBookPane);
        } catch (IOException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Lỗi", "Không thể tải subscene: " + e.getMessage(), null);
            Platform.exit();
        }
        showPane(2);
    }

    
    @FXML
    private void onEditDocument() {
            try {
                FXMLLoader editBookLoader = new FXMLLoader(getClass().getResource("/view/edit.fxml"));
                Parent editBookPane = editBookLoader.load();
                managementStackPane.getChildren().set(3, editBookPane);
            } catch (IOException e) {
                e.printStackTrace();
                util.ErrorDialog.showError("Lỗi", "Không thể tải subscene: " + e.getMessage(), null);
                Platform.exit();
            }
            showPane(3);
    }
    
   @FXML
    private void onManageMembers() {
        try {
            FXMLLoader manageMembersLoader = new FXMLLoader(getClass().getResource("/view/member_management.fxml"));
            Parent manageMembersPane = manageMembersLoader.load();
            managementStackPane.getChildren().set(4, manageMembersPane);
        } catch (IOException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Lỗi", "Không thể tải subscene: " + e.getMessage(), null);
            Platform.exit();
        }
        showPane(4);
    }

    @FXML
    private void handleReload() {
        resetList();
        refreshDocumentList();
        loadBorrowedDocuments();
        tvDocuments.getSelectionModel().clearSelection();
        taDocumentDetails.clear();
        reviewTextArea.clear();
        scoreLabel.setText("");
        reviewCountLabel.setText("");
        bookCoverImageView.setImage(null);
        tfSearch.clear();
        cbMembers.getItems().clear();
        cbDocuments.getItems().clear();
        cbMembers.getEditor().clear();
        cbDocuments.getEditor().clear();
        if (userList != null) {
            cbMembers.getItems().addAll(userList.stream()
            .map(user -> user.getAccountID() + " - " + user.getFullName())
            .collect(Collectors.toList()));
        }

        if (bookList != null) {
            cbDocuments.getItems().addAll(bookList.stream()
            .map(document -> document.getBookID() + " - " + document.getTitle())
            .collect(Collectors.toList()));
        }
        dpBorrowDate.getEditor().clear();
        dpReturnDate.getEditor().clear(); 
    }

    
    
    @FXML
    private void handleChangeCover() {
    // Mở cửa sổ chọn tệp
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Chọn ảnh bìa sách");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
    );

    

    // Lấy cửa sổ hiện tại
    Stage stage = (Stage) tvBorrowedDocuments.getScene().getWindow(); 

    // Hiển thị cửa sổ chọn tệp và lấy tệp được chọn
    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
        try {
            // Gọi phương thức setBookImage để cập nhật ảnh bìa cho sách
            BookDao bookDao = BookDao.getInstance();
            int bookId = tvDocuments.getSelectionModel().getSelectedItem().getBookID();
            bookDao.setBookImage(bookId, selectedFile.getAbsolutePath());
            util.ErrorDialog.showSuccess("Thành công", "Ảnh bìa đã được thay đổi thành công.", (Stage) tvDocuments.getScene().getWindow());
            handleReload();
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error",  e.getMessage(), (Stage) tvDocuments.getScene().getWindow());
        } catch (Exception e) {
            util.ErrorDialog.showError("Error",  e.getMessage(), (Stage) tvDocuments.getScene().getWindow());
        }
    }
    else {
         ErrorDialog.showError("Filee Error", "Error load file", "select another file", null);
    }
    
}
    @FXML
    private void handleChangeDescription() {
        Document selectedDocument = tvDocuments.getSelectionModel().getSelectedItem();
        if (selectedDocument == null) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng chọn một tài liệu từ danh sách.", (Stage) tvDocuments.getScene().getWindow());
            return;
        }

        String newDescription = taDocumentDetails.getText().trim();
        if (newDescription.isEmpty()) {
            util.ErrorDialog.showError("Lỗi", "Mô tả không được để trống.", (Stage) taDocumentDetails.getScene().getWindow());
            return;
        }

        try {
            BookDao.getInstance().setDescription(selectedDocument.getBookID(), newDescription);
            util.ErrorDialog.showSuccess("Thành công", "Mô tả đã được thay đổi thành công.", (Stage) taDocumentDetails.getScene().getWindow());
            handleReload();
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error",  e.getMessage(), (Stage) taDocumentDetails.getScene().getWindow());
        } catch (Exception e) {
            util.ErrorDialog.showError("Error",  e.getMessage(), (Stage) taDocumentDetails.getScene().getWindow());
        }
    }

    @FXML
    private void handleFetchIncorrectInfo() {
    ThreadManager.execute(() -> {
        String bookName = tvDocuments.getSelectionModel().getSelectedItem().getTitle();
        BookInfo bookinfo = googleAPI.GoogleApiBookController.getBookInfo(bookName);
        String score = bookinfo.getRating();
        String review = bookinfo.getDescription();
        String reviewCount = bookinfo.getReviewCount();
        
        String imageString = bookinfo.getImageUrl();

        Platform.runLater(() -> {
            reviewTextArea.setText(review);
            scoreLabel.setText(String.valueOf(score));
            if (review != null) {
                reviewTextArea.setText(review);
            } else {
                reviewTextArea.setText("No review available.");
            }

            if (score != null) {
                scoreLabel.setText(score);
            } else {
                scoreLabel.setText("No rating available.");
            }

            if (reviewCount != null) {
                reviewCountLabel.setText(reviewCount);
            } else {
                reviewCountLabel.setText("No review count available.");
            }
            Image image = new Image(imageString);
            bookCoverImageView.setImage(image);
           
        });
    });
    
    }
    
    @FXML
    public void handleFilterAction() {
        String filterText = tfFilter.getText().toLowerCase().trim();
        ObservableList<Document> allDocuments = FXCollections.observableArrayList(bookList);
        ObservableList<Document> filteredDocuments = FXCollections.observableArrayList();
       

        for (Document doc : allDocuments) {
            if (doc.getTitle().toLowerCase().contains(filterText)) {
                filteredDocuments.add(doc);
            }
        }

        tvDocuments.setItems(filteredDocuments);

        if (filteredDocuments.isEmpty()) {
            util.ErrorDialog.showError("Không tìm thấy", "Không có tài liệu nào phù hợp với từ khóa tìm kiếm.", (Stage) tvDocuments.getScene().getWindow());
            tvDocuments.setItems(allDocuments); // Đưa bảng về trạng thái ban đầu
        }
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
            util.ErrorDialog.showError("Lỗi", "Vui lòng chọn một tài liệu từ danh sách.", (Stage) cbDocuments.getScene().getWindow());
            return;
        }
    
        if (selectedMemberIdString == null || selectedMemberIdString.isEmpty()) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng chọn một thành viên từ danh sách.", (Stage) cbMembers.getScene().getWindow());
            return;
        }
    
        LocalDate borrowDate = dpBorrowDate.getValue();
        LocalDate returnDate = dpReturnDate.getValue();
        
        if (borrowDate == null || returnDate == null) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng chọn ngày mượn và ngày trả.", (Stage) dpBorrowDate.getScene().getWindow());
            return;
        }
        
        if (borrowDate.isAfter(returnDate)) {
            util.ErrorDialog.showError("Lỗi", "Ngày mượn không thể sau ngày trả.", (Stage) dpBorrowDate.getScene().getWindow());
            return;
        }
    
        Integer selectedMemberId;
        try {
            selectedMemberId = Integer.parseInt(selectedMemberIdString);
        } catch (NumberFormatException e) {
            util.ErrorDialog.showError("Lỗi", "ID thành viên không hợp lệ.", (Stage) cbMembers.getScene().getWindow());
            return;
        }
    
        try {
            int selectedDocumentId = Integer.parseInt(nameDocument);
            Document selectedDocument = BookDao.getInstance().get(selectedDocumentId);
        
            User user = UserDao.getInstance().get(selectedMemberId);
        
            Borrow newBorrow = new Borrow(
            selectedMemberId,
            selectedDocumentId,
            borrowDate,
            returnDate,
            "Borrowed"
            );
        
            BorrowDao.getInstance().insert(newBorrow);
            util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được mượn thành công.", (Stage) cbDocuments.getScene().getWindow());
        
            handleReload();
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error",  e.getMessage(), (Stage) cbDocuments.getScene().getWindow());
        } catch (Exception e) {
            util.ErrorDialog.showError("Error",  e.getMessage(), (Stage) cbDocuments.getScene().getWindow());
        }
       
        
        }

        @FXML
        private void handleReturnDocument() {
        try {
            Borrow selectedBorrow = BorrowDao.getInstance().get(tvBorrowedDocuments.getSelectionModel().getSelectedItem().getBorrowID());
            
            if (selectedBorrow == null) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng chọn một tài liệu đã mượn từ bảng để trả.", (Stage) tvBorrowedDocuments.getScene().getWindow());
            return;
            }

            // Check if the document has already been returned
            Return existingReturnRecord = ReturnDao.getInstance().get(selectedBorrow.getBorrowID());
            if (existingReturnRecord != null) {
            util.ErrorDialog.showError("Lỗi", "Tài liệu này đã được trả trước đó.", (Stage) tvBorrowedDocuments.getScene().getWindow());
            return;
            }

            Document selectedDocument = BookDao.getInstance().get(selectedBorrow.getBookID());
            if (selectedDocument == null) {
            util.ErrorDialog.showError("Lỗi", "Không tìm thấy tài liệu.", (Stage) tvBorrowedDocuments.getScene().getWindow());
            return;
            }

            int damagePercentage = (int) (Math.random() * 100); // Random damage percentage between 0 and 100
            Return returnRecord = new Return(BorrowDao.getInstance().getID(selectedBorrow), 
            LocalDate.now(), damagePercentage);
            ReturnDao.getInstance().insert(returnRecord);
            util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được trả thành công.", (Stage) tvBorrowedDocuments.getScene().getWindow());
            handleReload();
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error",  e.getMessage(), (Stage) tvBorrowedDocuments.getScene().getWindow());
        } catch (Exception e) {
            util.ErrorDialog.showError("Error",  e.getMessage(), (Stage) tvBorrowedDocuments.getScene().getWindow());
        }
        
        }

        
    @FXML
private void handleSearchAction() {
    String searchText = tfSearch.getText().toLowerCase().trim();
    String searchCriteria = cbSearchCriteria.getValue();

    if (searchCriteria == null || searchText.isEmpty()) {
        util.ErrorDialog.showError("Lỗi", "Vui lòng nhập nội dung tìm kiếm và chọn tiêu chí tìm kiếm.", (Stage) tfSearch.getScene().getWindow());
        return;
    }

    ObservableList<BorrowReturn> allBorrows = FXCollections.observableArrayList(borrowReturnList);
    ObservableList<BorrowReturn> filteredBorrows;

    if (searchCriteria.equals("Thành viên")) {
        filteredBorrows = allBorrows.stream()
            .filter(borrow -> borrow.getMember().toLowerCase().contains(searchText))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        tvBorrowedDocuments.setItems(filteredBorrows);

        if (filteredBorrows.isEmpty()) {
            util.ErrorDialog.showError("Không tìm thấy", "Không có thành viên nào phù hợp với từ khóa tìm kiếm.", (Stage) tvBorrowedDocuments.getScene().getWindow());
            tvBorrowedDocuments.setItems(allBorrows); // Đưa bảng về trạng thái ban đầu
        }
    } else if (searchCriteria.equals("Tài liệu")) {
        filteredBorrows = allBorrows.stream()
            .filter(borrow -> borrow.getBook().toLowerCase().contains(searchText))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        tvBorrowedDocuments.setItems(filteredBorrows);

        if (filteredBorrows.isEmpty()) {
            util.ErrorDialog.showError("Không tìm thấy", "Không có tài liệu nào phù hợp với từ khóa tìm kiếm.", (Stage) tvBorrowedDocuments.getScene().getWindow());
            tvBorrowedDocuments.setItems(allBorrows); // Đưa bảng về trạng thái ban đầu
        }
    }
}
       
}
