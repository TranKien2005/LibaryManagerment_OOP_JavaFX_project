package Controller;

import DAO.UserDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    @FXML
    public void initialize() {
        sexComboBox.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        
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

    
}
