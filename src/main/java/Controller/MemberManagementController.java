package Controller;

import java.io.IOException;
import java.util.List;

import DAO.UserDao;
import DAO.AccountDao;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Account;
import model.User;
import util.ThreadManager;

public class MemberManagementController {

    @FXML private TextField tfMemberFilter;
    @FXML private TableView<User> tvMembers;
    @FXML private VBox rootVBox;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colUsername;
   
    @FXML private TableColumn<User, String> colPhone;
    @FXML private TableColumn<User, String> colEmail;


    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private TextField tfFullname;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPhone;


    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        tvMembers.prefHeightProperty().bind(stage.heightProperty().multiply(0.8));
        initializeTableView();
        loadUsers();
        });

        tvMembers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            tfUsername.setText(newValue.getFullName());
            Account account = AccountDao.getInstance().get(newValue.getAccountID());
            pfPassword.setText(account.getPassword());
            tfUsername.setText(account.getUsername());
            tfFullname.setText(newValue.getFullName());
            tfEmail.setText(newValue.getEmail());
            tfPhone.setText(newValue.getPhone());
            }
        });
    }
  

    private void initializeTableView() {
        colUserId.setCellValueFactory(cellData -> new SimpleIntegerProperty(UserDao.getInstance().getID(cellData.getValue())).asObject());
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
      
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        tvMembers.setItems(userList);
    }

   

    private void loadUsers() {
        
            List<User> users = UserDao.getInstance().getAll();
            Platform.runLater(() -> {
                userList.clear();
                userList.addAll(users);
            });
    
    }

    @FXML
    private void handleMemberFilterAction() {
        String filterText = tfMemberFilter.getText().toLowerCase().trim();
        
        ThreadManager.execute(() -> {
            ObservableList<User> filteredList;
            if (filterText.isEmpty()) {
                filteredList = FXCollections.observableArrayList(userList);
            } else {
                filteredList = FXCollections.observableArrayList(userList.filtered(user ->
                    user.getFullName().toLowerCase().contains(filterText) ||
                    user.getPhone().toLowerCase().contains(filterText) ||
                    user.getEmail().toLowerCase().contains(filterText)
                ));
            }
            
            Platform.runLater(() -> tvMembers.setItems(filteredList));
        });
    }

    
    @FXML
    private void handleUpdateUser() {
        User selectedUser = tvMembers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            // Show an alert that no user is selected
            showAlert("No User Selected", "Please select a user to update.");
            return;
        }

        // Validate input fields
        if (!validateInputFields()) {
            return;
        }

        // Update user object with new values
        selectedUser.setFullName(tfFullname.getText());
        selectedUser.setPhone(tfPhone.getText());
        selectedUser.setEmail(tfEmail.getText());
        int userId = tvMembers.getSelectionModel().getSelectedItem().getAccountID();
        
        if (userId == -1) {
            showAlert("Update Failed", "Failed to update user. Please try again.");
            return;
        } else {
            UserDao.getInstance().update(selectedUser, userId);
            AccountDao.getInstance().updatePassword(userId, pfPassword.getText());
        }
        // Update user in database
        ThreadManager.execute(() -> {
            Platform.runLater(() -> {
              
                    refreshTableView();
                  
               
                
            });
        });
    }

    private boolean validateInputFields() {
        // Perform validation on input fields
        if (tfUsername.getText().isEmpty() || tfPhone.getText().isEmpty() || tfEmail.getText().isEmpty() ) {
            showAlert("Invalid Input", "Please fill in all fields.");
            return false;
        }


        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void refreshTableView() {
    userList.setAll(UserDao.getInstance().getAll());
    tfUsername.clear();
    pfPassword.clear();
    tfFullname.clear();
    tfEmail.clear();
    tfPhone.clear();

    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = tvMembers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("No User Selected", "Please select a user to delete.");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the user: " + selectedUser.getFullName() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Perform deletion
                ThreadManager.execute(() -> {
                    
                    Platform.runLater(() -> {
                        userList.remove(selectedUser);
                        UserDao.getInstance().delete(UserDao.getInstance().getID(selectedUser));
                        refreshTableView();
                        showAlert("User Deleted", "The user has been successfully deleted.");
                    });
                });
            }
        });
    }
    @FXML
    private void handleShowPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password");
        alert.setHeaderText(null);
        alert.setContentText(pfPassword.getText());
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh() {
        refreshTableView();
        loadUsers();
    }

   
  
}
