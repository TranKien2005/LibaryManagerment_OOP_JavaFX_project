package Controller;

import model.Document;
import DAO.BookDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;

public class DeleteController {
    @FXML
    private TextField tenTaiLieuTextField;
    @FXML
    private TableView<Document> taiLieuTableView;
    @FXML
    private TableColumn<Document, String> tenColumn;
    @FXML
    private TableColumn<Document, String> tacGiaColumn;
    @FXML
    private ListView<String> suggestionListView;

    private BookDao bookDao;

    public DeleteController() {
        this.bookDao = BookDao.getInstance();
    }

    @FXML
    public void initialize() {
        tenColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tacGiaColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        capNhatBangTaiLieu();
        
        tenTaiLieuTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSuggestions(newValue);
        });

        suggestionListView.setOnMouseClicked(event -> {
            String selectedItem = suggestionListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                tenTaiLieuTextField.setText(selectedItem);
                suggestionListView.setVisible(false);
            }
        });
    }

    @FXML
    public void xoaTaiLieu() {
        String tenTaiLieu = tenTaiLieuTextField.getText().trim();
        if (tenTaiLieu.isEmpty()) {
            hienThiThongBao("Lỗi", "Vui lòng nhập tên tài liệu.");
            return;
        }

        Document documentToDelete = bookDao.get(new Document(tenTaiLieu, "", "", "", 0, 0));
        if (documentToDelete == null) {
            hienThiThongBao("Lỗi", "Tài liệu không tồn tại.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn xóa tài liệu này không?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                bookDao.delete(documentToDelete);
                hienThiThongBao("Xóa thành công", "Tài liệu đã được xóa.");
                capNhatBangTaiLieu();
                tenTaiLieuTextField.clear();
            }
        });
    }

    private void capNhatBangTaiLieu() {
        taiLieuTableView.setItems(FXCollections.observableArrayList(bookDao.getAll()));
    }

    private void hienThiThongBao(String tieuDe, String noiDung) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    private void updateSuggestions(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            suggestionListView.setVisible(false);
            return;
        }

        ObservableList<String> suggestions = FXCollections.observableArrayList();
        for (Document doc : bookDao.getAll()) {
            if (doc.getName().toLowerCase().contains(searchText.toLowerCase())) {
                suggestions.add(doc.getName());
            }
        }

        suggestionListView.setItems(suggestions);
        suggestionListView.setVisible(!suggestions.isEmpty());
    }
}
