package Controller;

import java.sql.SQLException;

import DAO.AccountDao;
import DAO.ManagerDao;
import DAO.UserDao;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Account;
import model.Manager;
import model.User;
import util.ErrorDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MyAccountController {

    private int accountID;
    @FXML
    private ImageView userIcon;

    @FXML
    private TextField fullnameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label headerLabel;

    @FXML
    public void initialize() {
    
       try {
        if (menuUserController.getAccountID() != 0) {
            accountID = menuUserController.getAccountID();
            User user = UserDao.getInstance().get(accountID);
            Account account = AccountDao.getInstance().get(accountID);
            fullnameField.setText(user.getFullName());
            phoneField.setText(user.getPhone());
            emailField.setText(user.getEmail());
            passwordField.setText(account.getPassword());
        } else {
            accountID = menuController.getInstance().getAccountID();
            Manager user = ManagerDao.getInstance().get(accountID);
            Account account = AccountDao.getInstance().get(accountID);
            fullnameField.setText(user.getFullName());
            phoneField.setText(user.getPhone());
            emailField.setText(user.getEmail());
            passwordField.setText(account.getPassword());
        }
        
        
        
       }
       catch (SQLException e) {
           ErrorDialog.showError("SQL Error", e.getMessage(), null);
           e.printStackTrace();
       }
       catch (Exception e) {
           ErrorDialog.showError("Error", e.getMessage(), null);
           e.printStackTrace();
       }
    }
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleUserIconClick(MouseEvent event) {
        
    }

    private boolean validateInputFields() {
        if (fullnameField.getText().isEmpty() || phoneField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            ErrorDialog.showError("Validation Error", "Please fill in all fields.", (Stage) fullnameField.getScene().getWindow());
            return false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            ErrorDialog.showError("Validation Error", "Passwords do not match.", (Stage) fullnameField.getScene().getWindow());
            return false;
        }
        return true;
    }

    @FXML
    private void handleUpdate() {
        // Validate input fields
        if (!validateInputFields()) {
            return;
        }

        // Update user object with new values
    

    
        try {
            if (menuUserController.getAccountID() != 0) {
                User user = UserDao.getInstance().get(accountID);
                Account account = AccountDao.getInstance().get(accountID);
                user.setFullName(fullnameField.getText());
                user.setPhone(phoneField.getText());
                user.setEmail(emailField.getText());
                account.setPassword(passwordField.getText());
                UserDao.getInstance().update(user, accountID);
                AccountDao.getInstance().updatePassword(accountID, passwordField.getText());
            } else {
                Manager manager = ManagerDao.getInstance().get(accountID);
                Account account = AccountDao.getInstance().get(accountID);
                manager.setFullName(fullnameField.getText());
                manager.setPhone(phoneField.getText());
                manager.setEmail(emailField.getText());
                account.setPassword(passwordField.getText());
                ManagerDao.getInstance().update(manager, accountID);
                AccountDao.getInstance().updatePassword(accountID, passwordField.getText());
            }

            ErrorDialog.showSuccess("Success", "Account updated successfully.", (Stage) fullnameField.getScene().getWindow());
            if (menuUserController.getAccountID() != 0) {
                menuUserController.getInstance().reload();
            } else {
                menuController.getInstance().reload();
            }
        } catch (SQLException e) {
            ErrorDialog.showError("SQL Error", e.getMessage(), (Stage) fullnameField.getScene().getWindow());
            e.printStackTrace();
        }
        catch (Exception e) {
            ErrorDialog.showError("Error", e.getMessage(), (Stage) fullnameField.getScene().getWindow());
            e.printStackTrace();
        }
        
   
    }

    @FXML
    private void handleCancel() {
        fullnameField.clear();
        phoneField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        initialize();
    }

    public void handleReload() {
        handleCancel();
    }
}