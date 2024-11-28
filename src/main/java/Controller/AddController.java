package Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;

import DAO.BookDao;
import googleAPI.GoogleApiBookController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Document;
import util.ThreadManager;

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
    

    /**
    * Xử lý sự kiện khi người dùng nhấn nút "Thêm tài liệu".
    * <p>
    * Hàm này thực hiện các bước sau:
    * <ul>
    *   <li>Thu thập thông tin từ các trường nhập liệu (tiêu đề, tác giả, thể loại, nhà xuất bản, năm, số lượng).</li>
    *   <li>Kiểm tra và chuyển đổi dữ liệu đầu vào.</li>
    *   <li>Tạo đối tượng {@code Document} mới và thêm nó vào cơ sở dữ liệu thông qua lớp {@code BookDao}.</li>
    *   <li>Hiển thị thông báo thành công hoặc lỗi, đồng thời xóa các trường nhập liệu nếu thêm thành công.</li>
    * </ul>
    * 
    * Các lỗi có thể xảy ra:
    * <ul>
    *   <li>Định dạng không hợp lệ của trường năm hoặc số lượng.</li>
    *   <li>Lỗi khi thêm tài liệu vào cơ sở dữ liệu (SQLException).</li>
    *   <li>Các lỗi không xác định khác.</li>
    * </ul>
    * 
    * @throws NumberFormatException nếu trường năm hoặc số lượng không đúng định dạng số nguyên.
    * @throws SQLException nếu xảy ra lỗi khi tương tác với cơ sở dữ liệu.
    */
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



    /**
    * Xử lý sự kiện khi người dùng nhấn nút "Thêm bằng ISBN".
    * <p>
    * Hàm này thực hiện các bước sau:
    * <ul>
    *   <li>Kiểm tra xem trường ISBN đã được điền hay chưa. Nếu trống, hiển thị thông báo lỗi.</li>
    *   <li>Sử dụng API của Google để lấy thông tin sách dựa trên ISBN cung cấp.</li>
    *   <li>Nếu tìm thấy thông tin sách, thêm thông tin đó vào cơ sở dữ liệu thông qua lớp {@code BookDao}.</li>
    *   <li>Hiển thị thông báo thành công hoặc lỗi, đồng thời xóa các trường nhập liệu nếu thêm thành công.</li>
    * </ul>
    * 
    * Các lỗi có thể xảy ra:
    * <ul>
    *   <li>Trường ISBN trống.</li>
    *   <li>Không tìm thấy thông tin sách qua API Google.</li>
    *   <li>Lỗi khi thêm sách vào cơ sở dữ liệu (SQLException).</li>
    *   <li>Các lỗi không xác định khác trong quá trình xử lý luồng.</li>
    * </ul>
    * 
    * @throws SQLException nếu xảy ra lỗi khi tương tác với cơ sở dữ liệu.
    * @throws Exception nếu có lỗi không xác định khi lấy thông tin sách hoặc thêm sách vào cơ sở dữ liệu.
    */
    @FXML
    private void handleAddByIsbn() {
        String isbn = isbnField.getText();
        if (isbn.isEmpty()) {
            util.ErrorDialog.showError("Lỗi", "Vui lòng nhập ISBN.", (Stage) addByIsbnButton.getScene().getWindow());
            return;
        }

        util.ThreadManager.submitSqlTask(() -> {
            Document document = GoogleApiBookController.getBookInfoByISBN(isbn);
            if (document == null) {
                Platform.runLater(
                        () -> util.ErrorDialog.showError("Lỗi", "Không tìm thấy thông tin sách với ISBN đã nhập.",
                                (Stage) addByIsbnButton.getScene().getWindow()));
                return;
            }

            try {
                BookDao.getInstance().insert(document);
                Platform.runLater(() -> {
                    util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được thêm thành công.",
                            (Stage) addByIsbnButton.getScene().getWindow());
                    clearFields();
                    isbnField.clear();
                });
            } catch (SQLException e) {
                Platform.runLater(() -> util.ErrorDialog.showError("Lỗi SQL", e.getMessage(),
                        (Stage) addByIsbnButton.getScene().getWindow()));
            } catch (Exception e) {
                Platform.runLater(() -> util.ErrorDialog.showError("Lỗi", e.getMessage(),
                        (Stage) addByIsbnButton.getScene().getWindow()));
            }
        });
    }

    @FXML
    private Button addByFileButton;


        /**
        * Xử lý sự kiện khi người dùng nhấn nút "Thêm bằng File".
        * <p>
        * Hàm này cho phép người dùng chọn một tệp văn bản chứa danh sách ISBN, sau đó tự động lấy thông tin sách từ API Google và thêm vào cơ sở dữ liệu. 
        * Tiến trình được hiển thị thông qua thanh tiến trình trong màn hình loading.
        * </p>
        * <p><b>Chức năng chính:</b></p>
        * <ul>
        *   <li>Mở hộp thoại chọn file và chỉ chấp nhận file có đuôi ".txt".</li>
        *   <li>Hiển thị màn hình loading với thanh tiến trình cập nhật trạng thái xử lý file.</li>
        *   <li>Đọc từng dòng ISBN từ file, kiểm tra tính hợp lệ và lấy thông tin sách từ Google API.</li>
        *   <li>Thêm sách vào cơ sở dữ liệu thông qua lớp {@code BookDao} nếu thành công.</li>
        *   <li>Hiển thị kết quả với số lượng ISBN xử lý thành công và thất bại.</li>
        * </ul>
        * 
        * <p><b>Các lỗi có thể xảy ra:</b></p>
        * <ul>
        *   <li>Không thể tải hoặc mở file: Hiển thị lỗi "Không thể đọc file".</li>
        *   <li>Lỗi khi tương tác với API Google hoặc cơ sở dữ liệu: Hiển thị số lượng thất bại trong thông báo kết quả.</li>
        *   <li>Lỗi khi tải màn hình loading.</li>
        * </ul>
        * 
        * <p><b>Ghi chú:</b></p>
        * <ul>
        *   <li>Tệp văn bản đầu vào phải có định dạng mỗi dòng chứa một ISBN duy nhất.</li>
        *   <li>Màn hình loading không thể bị đóng bằng tay để đảm bảo tiến trình hoàn tất.</li>
        * </ul>
        * 
        * @throws IOException nếu không thể đọc file hoặc tải màn hình loading.
        * @throws SQLException nếu xảy ra lỗi khi thêm tài liệu vào cơ sở dữ liệu.
        */
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
                                    } catch (SQLException e) {
                                        check = false;
                                    } finally {
                                        reader2.close();
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
                            });
                        }
                    });
                } catch (IOException e) {
                    util.ErrorDialog.showError("Lỗi", "Không thể tải màn hình loading: " + e.getMessage(),
                            (Stage) addByFileButton.getScene().getWindow());
                }
            }
        }
}
