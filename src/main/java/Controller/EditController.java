package Controller;

import DAO.BookDao;
import model.Document;
import util.ThreadManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

    
public class EditController extends menuController {
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> suggestionListView;
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

    private BookDao bookDao;
    private Consumer<Void> onEditSuccess; // Callback
   

    public EditController() {
        this.bookDao = BookDao.getInstance();
    }

    public void setOnEditSuccess(Consumer<Void> onEditSuccess) {
        this.onEditSuccess = onEditSuccess;
    }

    public List<Document> bookList = new ArrayList<>();
    @FXML
    public void initialize() {
        suggestionListView.setVisible(false);
        try {
            bookList = BookDao.getInstance().getAll();
        } catch (SQLException e) {
            util.ErrorDialog.showError("Lỗi", "Đã xảy ra lỗi khi tải danh sách sách: " + e.getMessage(), null);
        }

        final long[] lastTypingTime = {System.currentTimeMillis()};
        final long typingDelay = 100;
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
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
            searchField.setText(selectedDocumentID);
            suggestionListView.setVisible(false);
            
            searchField.getParent().requestFocus();
            int documentId = -1;
                    String[] parts = suggestionListView.getSelectionModel().getSelectedItem().split(" - ");
                    if (parts.length > 0 && parts[0].matches("\\d+")) {
                        documentId = Integer.parseInt(parts[0]);
                        loadDocumentDetails(documentId);

                    }
            }
        });

       
    }

        

        private void loadDocumentDetails(int ID) {
            if (ID == -1) {
                util.ErrorDialog.showError("Lỗi", "ID không hợp lệ.", null);
                return;
            }
            try {
                Document document = bookDao.get(ID);
                if (document != null) {
                    titleField.setText(document.getTitle());
                    authorField.setText(document.getAuthor());
                    categoryField.setText(document.getCategory());
                    publisherField.setText(document.getPublisher());
                    yearField.setText(String.valueOf(document.getYearPublished()));
                    quantityField.setText(String.valueOf(document.getAvailableCopies()));
                }
            } catch (SQLException e) {
                util.ErrorDialog.showError("Lỗi",  e.getMessage(), null);
            } catch (Exception e) {
                util.ErrorDialog.showError("Lỗi",  e.getMessage(), null);
            }
        }

       

        private void updateSuggestions(String title) {
            
            ObservableList<String> suggestions = FXCollections.observableArrayList();
            for (Document doc : bookList) {
                if (doc.getTitle().toLowerCase().contains(title.toLowerCase())) {
                suggestions.add(doc.getBookID() + " - " + doc.getTitle());
                }
            }
            javafx.application.Platform.runLater(() -> {
                suggestionListView.setItems(suggestions);
                suggestionListView.setVisible(true);
            });
          
        }

    @FXML
    private void handleEditDocument() {
        try {
            // Lấy thông tin từ các trường và cập nhật tài liệu
            String title = titleField.getText();
            String author = authorField.getText();
            String category = categoryField.getText();
            String publisher = publisherField.getText();
            int year = Integer.parseInt(yearField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            int ID = -1;
            String searchText = searchField.getText();
            String[] parts = searchText.split(" - ");
            if (parts.length > 0) {
            ID = Integer.parseInt(parts[0]);
            } else {
            util.ErrorDialog.showError("Lỗi", "Không thể lấy ID từ trường tìm kiếm.", null);
            return;
            }
            System.out.println(ID);
            System.out.println("Updated Document Details:");
            System.out.println("Title: " + title);
            System.out.println("Author: " + author);
            System.out.println("Category: " + category);
            System.out.println("Publisher: " + publisher);
            System.out.println("Year: " + year);
            System.out.println("Quantity: " + quantity);

            Document updatedDocument = new Document(title, author, category, publisher, year, quantity);
            bookDao.update(updatedDocument, ID);
            util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được cập nhật.", null);
            handleCancel();
            if (onEditSuccess != null) {
            onEditSuccess.accept(null); // Gọi callback
            }

        } catch (NumberFormatException e) {
            util.ErrorDialog.showError("Lỗi", "Năm và số lượng phải là số nguyên hợp lệ.", null);
        } catch (SQLException e) {
            util.ErrorDialog.showError("Lỗi", "Đã xảy ra lỗi khi cập nhật cơ sở dữ liệu: " + e.getMessage(), null);
        } catch (Exception e) {
            util.ErrorDialog.showError("Lỗi", "Đã xảy ra lỗi khi cập nhật tài liệu.", null);
        }
    }

    @FXML
    private void handleCancel() {
        titleField.setText("");
        authorField.setText("");
        categoryField.setText("");
        publisherField.setText("");
        yearField.setText("");
        quantityField.setText("");
        searchField.setText("");
        suggestionListView.setItems(FXCollections.observableArrayList());
        suggestionListView.setVisible(false);
    }

    public void handleReload() {
        handleCancel();
    }
    
   
}
