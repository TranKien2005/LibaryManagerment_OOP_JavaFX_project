package Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import DAO.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.util.function.Consumer;
import util.*;
import googleAPI.*;

public class AddController extends menuController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField publisherField;

    @FXML
    private TextField yearField;

    @FXML
    private TextField quantityField;

    @FXML
    private Button addButton;

    @FXML
    private TextField isbnField;

    private Consumer<Document> onAddListener;

    public void setOnAddListener(Consumer<Document> listener) {
        this.onAddListener = listener;
    }

    @FXML
    private void handleAddDocument() {
        // Lấy dữ liệu từ các trường nhập liệu
        String title = titleField.getText();
        String author = authorField.getText();
        String category = categoryField.getText();
        String publisher = publisherField.getText();

        int year;
        int quantity;

        try {
            year = Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            util.ErrorDialog.showError("Lỗi", "Năm không đúng định dạng. Vui lòng nhập số nguyên.",
                    (Stage) addButton.getScene().getWindow());
            return;
        }

        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            util.ErrorDialog.showError("Lỗi", "Số lượng không đúng định dạng. Vui lòng nhập số nguyên.",
                    (Stage) addButton.getScene().getWindow());
            return;
        }

        // Tạo đối tượng Document mới
        Document newDocument = new Document(title, author, category, publisher, year, quantity);

        try {
            BookDao.getInstance().insert(newDocument);
            util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được thêm thành công.",
                    (Stage) addButton.getScene().getWindow());
            clearFields();
            if (onAddListener != null) {
                onAddListener.accept(newDocument);
            }

        } catch (SQLException e) {
            util.ErrorDialog.showError("Lỗi SQL", "Không thể thêm tài liệu do lỗi cơ sở dữ liệu: " + e.getMessage(),
                    (Stage) addButton.getScene().getWindow());
        } catch (Exception e) {
            util.ErrorDialog.showError("Lỗi", "Không thể thêm tài liệu. Vui lòng thử lại: " + e.getMessage(),
                    (Stage) addButton.getScene().getWindow());
        }
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        categoryField.clear();
        publisherField.clear();
        yearField.clear();
        quantityField.clear();
    }

    @SuppressWarnings("unused")
    private void closeWindow() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() {
        System.out.println("AddController đã được khởi tạo");
    }

    @FXML
    private Button backButton;

    @FXML
    private void handleCancel() {
        clearFields();
        isbnField.clear();
    }

    public void reload() {
        clearFields();
        isbnField.clear();
    }

    @FXML
    private Button addByIsbnButton;

    @FXML
    private void handleAddByIsbn() {
        String isbn = isbnField.getText();
        if (isbn.isEmpty()) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng nhập ISBN.", (Stage) addByIsbnButton.getScene().getWindow());

            return;
        }

        util.ThreadManager.submitSqlTask(() -> {

            try {
                Document document = GoogleApiBookController.getBookInfoByISBN(isbn);

                BookDao.getInstance().insert(document);
                Platform.runLater(() -> {
                    util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được thêm thành công.",
                            (Stage) addByIsbnButton.getScene().getWindow());
                    clearFields();
                    isbnField.clear();
                });
                Document existingDocument = BookDao.getInstance().get(BookDao.getInstance().getID(document));
                if (existingDocument != null) {
                    boolean updated = false;
                    if (existingDocument.getDescription() == null || existingDocument.getDescription().isEmpty()) {
                        existingDocument.setDescription(document.getDescription());
                        updated = true;
                    }
                    if (existingDocument.getCoverImage() == null || existingDocument.getCoverImage() == null) {
                        existingDocument.setCoverImage(document.getCoverImage());
                        updated = true;
                    }
                    if (existingDocument.getRating() == 0) {
                        existingDocument.setRating(document.getRating());
                        updated = true;
                    }
                    if (existingDocument.getReviewCount() == 0) {
                        existingDocument.setReviewCount(document.getReviewCount());
                        updated = true;
                    }
                    if (updated) {
                        BookDao.getInstance().update(existingDocument, existingDocument.getBookID());
                    }
                } else {
                    BookDao.getInstance().insert(document);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> util.ErrorDialog.showError("Lỗi SQL", e.getMessage(),
                        (Stage) addByIsbnButton.getScene().getWindow()));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> util.ErrorDialog.showError("Lỗi", e.getMessage(),
                        (Stage) addByIsbnButton.getScene().getWindow()));
            }
        });
    }

    @FXML
    private Button addByFileButton;

    @FXML
    private void handleAddByFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(addByFileButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Tải và hiển thị màn hình loading
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/loading1.fxml"));
                Parent loadingRoot = loader.load();
                Stage loadingStage = new Stage();
                loadingStage.initModality(Modality.APPLICATION_MODAL);
                loadingStage.setScene(new Scene(loadingRoot));
                loadingStage.setOnCloseRequest(event -> event.consume()); // Prevent closing
                loadingStage.show();

                // Lấy thanh tiến trình từ FXML
                ProgressBar progressBar = (ProgressBar) loader.getNamespace().get("progressBar");

                ThreadManager.submitSqlTask(() -> {
                    try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                        String isbn;
                        int successCount = 0;
                        int failureCount = 0;
                        int totalLines = (int) reader.lines().count(); // Đếm tổng số dòng trong file
                        reader.close(); // Đóng và mở lại reader để đọc từ đầu
                        BufferedReader reader2 = new BufferedReader(new FileReader(selectedFile));
                        int currentLine = 0;

                        while ((isbn = reader2.readLine()) != null) {
                            if (!isbn.trim().isEmpty()) {
                                boolean check = true;
                                final String currentIsbn = isbn.trim();
                                Document document = null;
                                try {
                                    document = GoogleApiBookController.getBookInfoByISBN(currentIsbn);
                                    if (document != null) {
                                        BookDao.getInstance().insert(document);
                                        check = true;
                                    } else {
                                        check = false;
                                    }
                                } catch (Exception e) {
                                    check = false;
                                    e.printStackTrace();
                                }
                                if (check) {
                                    successCount++;
                                } else {
                                    failureCount++;
                                }
                            }
                            currentLine++;
                            final double progress = (double) currentLine / totalLines;
                            Platform.runLater(() -> progressBar.setProgress(progress)); // Cập nhật thanh tiến trình
                        }
                        reader2.close();
                        final int finalSuccessCount = successCount;
                        final int finalFailureCount = failureCount;
                        Platform.runLater(() -> {
                            loadingStage.close(); // Đóng màn hình loading
                            util.ErrorDialog.showSuccess("Kết quả",
                                    "Thành công: " + finalSuccessCount + ", Thất bại: " + finalFailureCount,
                                    (Stage) addByFileButton.getScene().getWindow());
                        });
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            loadingStage.close(); // Đóng màn hình loading
                            util.ErrorDialog.showError("Lỗi", "Không thể đọc file: " + e.getMessage(),
                                    (Stage) addByFileButton.getScene().getWindow());
                            e.printStackTrace();
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                util.ErrorDialog.showError("Lỗi", "Không thể tải màn hình loading: " + e.getMessage(),
                        (Stage) addByFileButton.getScene().getWindow());
            }
        }
    }
}
