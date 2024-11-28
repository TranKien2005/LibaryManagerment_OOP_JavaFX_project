package Controller;

import java.sql.SQLException;

import javafx.scene.control.Label;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import DAO.*;
import QR.QRScanner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import model.*;
import util.ErrorDialog;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class menuUserController {
    private HomeController homeController;
    private MyAccountController myAccountController;
    private static menuUserController instance;
    private static int accountID;
    public static List<Document> bookList = new ArrayList<>();
    public static List<BorrowReturn> borrowReturnList = new ArrayList<>();
    public Image defaulImage = new Image(getClass().getResourceAsStream("/images/menu/coverArtUnknown.png"));

    public static int getAccountID() {
        return accountID;
    }

    public static void setAccountID(int accountID) {
        menuUserController.accountID = accountID;
    }

    public static menuUserController getInstance() {
        if (instance == null) {
            instance = new menuUserController();
        }
        return instance;
    }

    public void resetList() {
        try {
            bookList = BookDao.getInstance().getAll();
            borrowReturnList = BorrowReturnDAO.getInstance().getByAccountId(accountID);
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error", e.getMessage(), null);
        } catch (Exception e) {
            util.ErrorDialog.showError("Error", e.getMessage(), null);
        }
    }

    @FXML
    private Label userName;

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane borrowAndReturnTab;

    @FXML
    private TextField cbMembers;

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
    private TextField tfSearch;

    @FXML
    private ComboBox<String> cbSearchCriteria;

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
                        cbMembers.setText(currentUser.getAccountID() + " - " + currentUser.getFullName());
                    }
                } else {
                    Manager currentUser = ManagerDao.getInstance().get(accountID);
                    if (currentUser != null) {
                        userName.setText("Manager: " + currentUser.getFullName());
                        cbMembers.setText(currentUser.getAccountID() + " - " + currentUser.getFullName());
                    }
                }

            }
            resetList();
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error", e.getMessage(), null);
        } catch (Exception e) {
            util.ErrorDialog.showError("Error", e.getMessage(), null);
        }

        // Set text for cbMembers to accountID - fullName of the current user

        Platform.runLater(() -> {

            if (bookList != null) {
                cbDocuments.getItems().addAll(bookList.stream()
                        .map(document -> document.getBookID() + " - " + document.getTitle())
                        .collect(Collectors.toList()));
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

        // add subscene
        // Tải và thêm các cảnh phụ vào managementStackPane
        try {

            FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
            Parent homePane = homeLoader.load();
            stackPane.getChildren().add(homePane);
            homeController = homeLoader.getController();
            FXMLLoader myAccountLoader = new FXMLLoader(getClass().getResource("/view/myAccount.fxml"));
            Parent myAccountPane = myAccountLoader.load();
            stackPane.getChildren().add(myAccountPane);
            myAccountController = myAccountLoader.getController();

            for (int i = 0; i < stackPane.getChildren().size(); i++) {
                stackPane.getChildren().get(i).setVisible(false);
            }
            // Đặt cảnh báo là pane mặc định hiển thị
            stackPane.getChildren().get(1).setVisible(true);

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

    @FXML
    private void showHomeTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        stackPane.getChildren().get(1).setVisible(true);
        homeController.undoDetail();
    }

    @FXML
    private void showBorrowReturnTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        borrowAndReturnTab.setVisible(true);
    }

    @FXML
    private void showMyAccountTab() {
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        stackPane.getChildren().get(2).setVisible(true);
        myAccountController.initialize();
    }

    @FXML
    private void handleReload() {

        try {
            resetList();
            homeController.reload();
            myAccountController.initialize();
            loadBorrowedDocuments();

            cbDocuments.getItems().clear();

            cbDocuments.getEditor().clear();
            Account account = AccountDao.getInstance().get(accountID);
            if (account != null) {
                if (account.getAccountType().equals("User")) {
                    User currentUser = UserDao.getInstance().get(accountID);
                    if (currentUser != null) {
                        userName.setText("User: " + currentUser.getFullName());
                        cbMembers.setText(currentUser.getAccountID() + " - " + currentUser.getFullName());
                    }
                } else {
                    Manager currentUser = ManagerDao.getInstance().get(accountID);
                    if (currentUser != null) {
                        userName.setText("Manager: " + currentUser.getFullName());
                        cbMembers.setText(currentUser.getAccountID() + " - " + currentUser.getFullName());
                    }
                }
            }
            if (bookList != null) {
                cbDocuments.getItems().addAll(bookList.stream()
                        .map(document -> document.getBookID() + " - " + document.getTitle())
                        .collect(Collectors.toList()));
            }
            dpBorrowDate.setValue(LocalDate.now());

            dpReturnDate.getEditor().clear();
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error", e.getMessage(), null);
            e.printStackTrace();
        } catch (Exception e) {
            util.ErrorDialog.showError("Error", e.getMessage(), null);
            e.printStackTrace();
        }

    }

    public void reload() {
        handleReload();
    }

    @FXML
    private void handleBorrowDocument() {
        cbDocuments.getParent().requestFocus();
        String nameDocument = cbDocuments.getValue();
        nameDocument = nameDocument.split(" - ")[0];
        String selectedMemberIdString = cbMembers.getText();
        if (selectedMemberIdString != null && selectedMemberIdString.contains(" - ")) {
            selectedMemberIdString = selectedMemberIdString.split(" - ")[0];
        }

        if (nameDocument == null || nameDocument.isEmpty()) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng chọn một tài liệu từ danh sách.",
                    (Stage) cbDocuments.getScene().getWindow());
            return;
        }

        if (selectedMemberIdString == null || selectedMemberIdString.isEmpty()) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng chọn một thành viên từ danh sách.",
                    (Stage) cbMembers.getScene().getWindow());
            return;
        }

        LocalDate borrowDate = dpBorrowDate.getValue();
        LocalDate returnDate = dpReturnDate.getValue();

        if (borrowDate == null || returnDate == null) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng chọn ngày mượn và ngày trả.",
                    (Stage) dpBorrowDate.getScene().getWindow());
            return;
        }

        if (borrowDate.isAfter(returnDate)) {
            util.ErrorDialog.showError("Lỗi", "Ngày mượn không thể sau ngày trả.",
                    (Stage) dpBorrowDate.getScene().getWindow());
            return;
        }

        Integer selectedMemberId;
        try {
            selectedMemberId = Integer.parseInt(selectedMemberIdString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Lỗi", "ID thành viên không hợp lệ.", (Stage) cbMembers.getScene().getWindow());
            return;
        }

        try {
            int selectedDocumentId = Integer.parseInt(nameDocument);
            Borrow newBorrow = new Borrow(
                    selectedMemberId,
                    selectedDocumentId,
                    borrowDate,
                    returnDate,
                    "Borrowed");

            BorrowDao.getInstance().insert(newBorrow);
            util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được mượn thành công.",
                    (Stage) cbDocuments.getScene().getWindow());

            handleReload();
        } catch (SQLException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Database Error", e.getMessage(), (Stage) cbDocuments.getScene().getWindow());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", e.getMessage(), (Stage) cbDocuments.getScene().getWindow());
        }

    }

    @FXML
    private void handleReturnDocument() {
        try {
            Borrow selectedBorrow = BorrowDao.getInstance()
                    .get(tvBorrowedDocuments.getSelectionModel().getSelectedItem().getBorrowID());

            if (selectedBorrow == null) {
                util.ErrorDialog.showError("Lỗi", "Vui lòng chọn một tài liệu đã mượn từ bảng để trả.",
                        (Stage) tvBorrowedDocuments.getScene().getWindow());
                return;
            }

            // Check if the document has already been returned
            Return existingReturnRecord = ReturnDao.getInstance().get(selectedBorrow.getBorrowID());
            if (existingReturnRecord != null) {
                util.ErrorDialog.showError("Lỗi", "Tài liệu này đã được trả trước đó.",
                        (Stage) tvBorrowedDocuments.getScene().getWindow());
                return;
            }

            Document selectedDocument = BookDao.getInstance().get(selectedBorrow.getBookID());
            if (selectedDocument == null) {
                util.ErrorDialog.showError("Lỗi", "Không tìm thấy tài liệu.",
                        (Stage) tvBorrowedDocuments.getScene().getWindow());
                return;
            }

            int damagePercentage = (int) (Math.random() * 100); // Random damage percentage between 0 and 100
            Return returnRecord = new Return(BorrowDao.getInstance().getID(selectedBorrow),
                    LocalDate.now(), damagePercentage);
            ReturnDao.getInstance().insert(returnRecord);
            util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được trả thành công.",
                    (Stage) tvBorrowedDocuments.getScene().getWindow());
            handleReload();
        } catch (SQLException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Database Error", e.getMessage(),
                    (Stage) tvBorrowedDocuments.getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", e.getMessage(), (Stage) tvBorrowedDocuments.getScene().getWindow());
        }

    }

    @FXML
    private void handleSearchAction() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String searchCriteria = cbSearchCriteria.getValue();

        if (searchCriteria == null || searchText.isEmpty()) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng nhập nội dung tìm kiếm và chọn tiêu chí tìm kiếm.",
                    (Stage) tfSearch.getScene().getWindow());
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
                util.ErrorDialog.showError("Không tìm thấy", "Không có thành viên nào phù hợp với từ khóa tìm kiếm.",
                        (Stage) tvBorrowedDocuments.getScene().getWindow());
                tvBorrowedDocuments.setItems(allBorrows); // Đưa bảng về trạng thái ban đầu
            }
        } else if (searchCriteria.equals("Tài liệu")) {
            filteredBorrows = allBorrows.stream()
                    .filter(borrow -> borrow.getBook().toLowerCase().contains(searchText))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            tvBorrowedDocuments.setItems(filteredBorrows);

            if (filteredBorrows.isEmpty()) {
                util.ErrorDialog.showError("Không tìm thấy", "Không có tài liệu nào phù hợp với từ khóa tìm kiếm.",
                        (Stage) tvBorrowedDocuments.getScene().getWindow());
                tvBorrowedDocuments.setItems(allBorrows); // Đưa bảng về trạng thái ban đầu
            }
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) borrowAndReturnTab.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            if (loader.getLocation() == null) {
                System.err.println("Error: FXML file not found!");
                return;
            }
            Scene scene = new Scene(loader.load());
            stage.setTitle("Đăng nhập");
            stage.setScene(scene);
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.getScene().getRoot().setStyle("-fx-border-color: black; -fx-border-width: 2px;");
            stage.centerOnScreen();

            // Sử dụng đường dẫn tuyệt đối cho tệp hình ảnh
            Image icon = new Image(getClass().getResourceAsStream("/images/login/logo.png"));
            if (icon.isError()) {
                System.err.println("Error: Image file not found!");
                return;
            }
            stage.getIcons().add(icon);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean ishandlingQR = false;
    public QRScanner qrScanner = QRScanner.getInstance();

    private boolean isValidQRCodeFormat(String qrCodeText) {
        if (qrCodeText == null || !qrCodeText.startsWith("BookID:")) {
            return false;
        }
        try {
            Integer.valueOf(qrCodeText.substring("BookID:".length()).trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void handleQRCodeScan() {
        boolean isscanning = (qrScanner != null && qrScanner.isRunning());
        if (isscanning || ishandlingQR) {
            return;
        }
        qrScanner.startQRScanner(qrCodeText -> {
            Platform.runLater(() -> {

                try {
                    if (ishandlingQR) {
                        return;
                    }
                    if (!isValidQRCodeFormat(qrCodeText)) {
                        ErrorDialog.showError("QR Code Error", "Invalid QR Code format", null);
                        return;
                    }
                    int bookID = Integer.parseInt(qrCodeText.substring("BookID:".length()).trim());
                    Document document = BookDao.getInstance().get(bookID);
                    ishandlingQR = true;
                    if (borrowAndReturnTab.isVisible()) {
                        cbDocuments.setValue(bookID + " - " + document.getTitle());
                    } else {
                        showHomeTab();
                        homeController.openBookDetailTab(document);
                    }
                    qrScanner.stopQRScanner();
                    ishandlingQR = false;

                } catch (NumberFormatException e) {
                    ErrorDialog.showError("QR Code Error", "Invalid account ID", null);
                    e.printStackTrace();
                    ishandlingQR = false;
                } catch (RuntimeException | SQLException e) {
                    ErrorDialog.showError("QR Code Error", e.getMessage(), null);
                    e.printStackTrace();
                    ishandlingQR = false;
                }

            });
        });
    }
}
