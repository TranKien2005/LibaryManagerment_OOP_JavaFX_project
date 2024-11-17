package Controller;
import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.*;
import DAO.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.util.function.Consumer;
import javafx.util.Pair;
import util.*;
import googleAPI.*;


public class AddController extends menuController{
   
    
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
            util.ErrorDialog.showError("Lỗi", "Năm không đúng định dạng. Vui lòng nhập số nguyên.", (Stage) addButton.getScene().getWindow());
            return;
        }
        
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            util.ErrorDialog.showError("Lỗi", "Số lượng không đúng định dạng. Vui lòng nhập số nguyên.", (Stage) addButton.getScene().getWindow());
            return;
        }

        // Tạo đối tượng Document mới
        Document newDocument = new Document(title, author, category, publisher, year, quantity);

    try {
        BookDao.getInstance().insert(newDocument);
        util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được thêm thành công.", (Stage) addButton.getScene().getWindow());
        clearFields();
        if (onAddListener != null) {
            onAddListener.accept(newDocument);
        }
      
    } catch (SQLException e) {
        util.ErrorDialog.showError("Lỗi SQL", "Không thể thêm tài liệu do lỗi cơ sở dữ liệu: " + e.getMessage(), (Stage) addButton.getScene().getWindow());
    } catch (Exception e) {
        util.ErrorDialog.showError("Lỗi", "Không thể thêm tài liệu. Vui lòng thử lại: " + e.getMessage(), (Stage) addButton.getScene().getWindow());
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

    @FXML
    private Button addByIsbnButton;

  
    @FXML
    private void handleAddByIsbn() {
        String isbn = isbnField.getText();
        if (isbn.isEmpty()) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng nhập ISBN.", (Stage) addByIsbnButton.getScene().getWindow());
            return;
        }

            util.ThreadManager.submitSqlTask(()->{
            Document document = GoogleApiBookController.getBookInfoByISBN(isbn);
            if (document == null) {
            util.ErrorDialog.showError("Lỗi", "Không tìm thấy thông tin sách với ISBN đã nhập.", (Stage) addByIsbnButton.getScene().getWindow());
            return;
            }

            if (document != null) {
            try {
                BookDao.getInstance().insert(document);
                util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được thêm thành công.", (Stage) addByIsbnButton.getScene().getWindow());
                clearFields();
            } catch (SQLException e) {
                util.ErrorDialog.showError("Lỗi SQL", e.getMessage(), (Stage) addByIsbnButton.getScene().getWindow());
            } catch (Exception e) {
                util.ErrorDialog.showError("Lỗi", e.getMessage(), (Stage) addByIsbnButton.getScene().getWindow());
            }
            }
        });
    }
}

