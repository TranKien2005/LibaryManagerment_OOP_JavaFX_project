package Controller;

import java.io.IOException;
import java.util.List;

import DAO.UserDao;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.User;
import thread.ThreadManager;

public class MemberManagementController {

    @FXML private TextField tfMemberFilter;
    @FXML private TableView<User> tvMembers;
    @FXML private VBox rootVBox;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colAddress;
    @FXML private TableColumn<User, String> colPhone;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colSex;
    @FXML private TableColumn<User, Integer> colAge;

    @FXML private TextField tfUserId;
    @FXML private TextField tfUsername;
    @FXML private TextField tfAddress;
    @FXML private TextField tfPhone;
    @FXML private TextField tfEmail;
    @FXML private ComboBox<String> cbSex;
    @FXML private TextField tfAge;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        tvMembers.prefHeightProperty().bind(stage.heightProperty().multiply(0.8));
        initializeTableView();
        initializeComboBox();
        loadUsers();
        });
    }

    private void initializeTableView() {
        colUserId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colAddress.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colSex.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSex()));
        colAge.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAge()).asObject());

        tvMembers.setItems(userList);
    }

    private void initializeComboBox() {
        cbSex.getItems().addAll("Nam", "Nữ", "Khác");
    }

    private void loadUsers() {
        ThreadManager.execute(() -> {
            List<User> users = UserDao.getInstance().getAll();
            Platform.runLater(() -> {
                userList.clear();
                userList.addAll(users);
            });
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
                    user.getUsername().toLowerCase().contains(filterText) ||
                    user.getAddress().toLowerCase().contains(filterText) ||
                    user.getPhone().toLowerCase().contains(filterText) ||
                    user.getEmail().toLowerCase().contains(filterText) ||
                    String.valueOf(user.getAge()).contains(filterText)
                ));
            }
            
            Platform.runLater(() -> tvMembers.setItems(filteredList));
        });
    }

    @FXML
    private void handleAddUser() {
        // Validate input fields
        if (!validateInputFields()) {
            return;
        }

        // Create a new User object with input values
        User newUser = new User(
            tfUsername.getText(),
            "", // password (empty for now)
            tfAddress.getText(),
            tfPhone.getText(),
            tfEmail.getText(),
            cbSex.getValue(),
            Integer.parseInt(tfAge.getText())
        );

        // Add user to database
        ThreadManager.execute(() -> {
            Platform.runLater(() -> {
                UserDao.getInstance().insert(newUser);
                userList.add(newUser);
                clearInputFields();
                showAlert("User Added", "The new user has been successfully added.");
            });
        });
    }

    private void clearInputFields() {
        tfUsername.clear();
        tfAddress.clear();
        tfPhone.clear();
        tfEmail.clear();
        cbSex.getSelectionModel().clearSelection();
        tfAge.clear();
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
        selectedUser.setUsername(tfUsername.getText());
        selectedUser.setAddress(tfAddress.getText());
        selectedUser.setPhone(tfPhone.getText());
        selectedUser.setEmail(tfEmail.getText());
        selectedUser.setSex(cbSex.getValue());
        selectedUser.setAge(Integer.parseInt(tfAge.getText()));

        // Update user in database
        ThreadManager.execute(() -> {
            Platform.runLater(() -> {
              
                    refreshTableView();
                  
               
                
            });
        });
    }

    private boolean validateInputFields() {
        // Perform validation on input fields
        if (tfUsername.getText().isEmpty() || tfAddress.getText().isEmpty() || 
            tfPhone.getText().isEmpty() || tfEmail.getText().isEmpty() || 
            cbSex.getValue() == null || tfAge.getText().isEmpty()) {
            showAlert("Invalid Input", "Please fill in all fields.");
            return false;
        }

        try {
            Integer.parseInt(tfAge.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid Age", "Please enter a valid number for age.");
            return false;
        }

        // Add more specific validation as needed (e.g., email format, phone number format)

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
        int selectedIndex = tvMembers.getSelectionModel().getSelectedIndex();
        tvMembers.getItems().set(selectedIndex, tvMembers.getItems().get(selectedIndex));
        tvMembers.refresh();
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
        confirmAlert.setContentText("Are you sure you want to delete the user: " + selectedUser.getUsername() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Perform deletion
                ThreadManager.execute(() -> {
                    UserDao.getInstance().delete(selectedUser);
                    Platform.runLater(() -> {
                        userList.remove(selectedUser);
                        showAlert("User Deleted", "The user has been successfully deleted.");
                    });
                });
            }
        });
    }

   
  
}
