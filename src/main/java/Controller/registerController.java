package Controller;

import java.io.IOException;

import DAO.UserDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

public class registerController extends LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField;
    @FXML
    private ComboBox<String> sexComboBox;
    @FXML
    private TextField ageField;
    @FXML
    private Button registerButton;

    private Parent loginRoot;
    private FXMLLoader loginLoader;

    private void showErrorAlert(String title, String message) {
        // Implementation for showing an error alert
        System.out.println(title + ": " + message);
    }

    @FXML
    public void initialize() {
        sexComboBox.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        
        try {
            loginLoader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            loginRoot = loginLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Lỗi không xác định", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handlRegister() {
        User user = validateInputs();
        if (user != null) {
           UserDao userDao = UserDao.getInstance();
           userDao.insert(user);
            showAlert("Thông báo", "Đăng ký thành công");
        }
    }

    private User validateInputs() {
       if (usernameField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            passwordField.getText().isEmpty() ||
            confirmPasswordField.getText().isEmpty() ||
            addressField.getText().isEmpty() ||
            phoneField.getText().isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin");
                return null;
            }
            return new User(usernameField.getText(), passwordField.getText(), addressField.getText(), phoneField.getText(), 
            emailField.getText(), sexComboBox.getValue(), Integer.parseInt(ageField.getText()));
    }

    @FXML
    public void handleLogin() {
        try {
            // Lấy Stage hiện tại từ một nút hoặc bất kỳ thành phần nào trong Scene hiện tại
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Đăng nhập");
           
            stage.getScene().setRoot(loginRoot);
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.centerOnScreen();
            stage.setMaximized(false); // Set the stage to full screen
            
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Lỗi không xác định", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }
}
