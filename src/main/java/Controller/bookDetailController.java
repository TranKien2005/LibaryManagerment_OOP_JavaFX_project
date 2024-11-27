package Controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.imageio.ImageIO;

import org.checkerframework.checker.units.qual.m;

import QR.*;


import DAO.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.*;
import util.ErrorDialog;
import util.ThreadManager;


public class BookDetailController {

    @FXML
    private ImageView coverImageView; // Hiển thị ảnh bìa sách

    @FXML
    private Label titleLabel; // Tiêu đề sách

    @FXML
    private Label authorLabel; // Tác giả

    @FXML
    private Label publisherLabel; // Nhà xuất bản

    @FXML
    private Label yearPublishedLabel; // Năm phát hành

    @FXML
    private Label categoryLabel; // Thể loại

    @FXML
    private Label availableCopiesLabel; // Số bản có sẵn

    @FXML
    private Label descriptionLabel; // Mô tả sách (có hỗ trợ wrap text)

    @FXML
    private HBox ratingBox; // Khung hiển thị đánh giá (dạng sao)

    @FXML
    private Label numberOfRatingsLabel; // Số lượt đánh giá

    private Document book; // Đối tượng chứa thông tin sách

    @FXML
    private Button star1;

    @FXML
    private Button star2;

    @FXML
    private Button star3;

    @FXML
    private Button star4;

    @FXML
    private Button star5;

    @FXML
    private ImageView qrCodeImageView;

    private int rating = 0;

    int accountID = menuUserController.getInstance().getAccountID();


    /**
     * Thiết lập sách cần hiển thị.
     * @param book Đối tượng Document đại diện cho thông tin sách.
     */
   
    public void setBook(Document book) {
        this.book = book;
        updateBookDetails();
    }

    /**
     * Cập nhật thông tin chi tiết sách lên giao diện.
     */
    private void updateBookDetails() {
        // Cập nhật ảnh bìa
        accountID = menuUserController.getInstance().getAccountID();
        InputStream coverImageStream = book.getCoverImage();
        try {
            InputStream qrCodeStream = CreateQRCode.generateQRCode("BookID: " + book.getBookID());
            qrCodeImageView.setImage(new Image(qrCodeStream));
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", "Có lỗi xảy ra khi tạo mã QR.", null);
        }
        if (coverImageStream != null) {
            Image image = new Image(coverImageStream);
            try {
                coverImageStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            coverImageView.setImage(image);
        } else {
            Image image = new Image("/images/menu/coverArtUnknown.png");
            coverImageView.setImage(image);
        }

        // Cập nhật thông tin cơ bản
        titleLabel.setText(book.getTitle());
        System.out.println(book.getBookID());
        authorLabel.setText(book.getAuthor());
        publisherLabel.setText(book.getPublisher());
        yearPublishedLabel.setText(String.valueOf(book.getYearPublished()));
        descriptionLabel.setText(book.getDescription());
        categoryLabel.setText(book.getCategory());
        availableCopiesLabel.setText(String.valueOf(book.getAvailableCopies()));
        numberOfRatingsLabel.setText(String.valueOf(book.getReviewCount()));

        // Hiển thị đánh giá (rating)
        updateRatingBox();
    }

    /**
     * Cập nhật đánh giá sao (rating) của sách.
     */
    private void updateRatingBox() {
        try {
            ratingBox.getChildren().clear(); // Xóa các sao cũ
            for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView();
            if (i <= book.getRating()) {
            star.setImage(new Image("/images/menu/star_filled.png")); // Đường dẫn đến ảnh sao đen
            } else if (i - book.getRating() <= 0.5) {
            star.setImage(new Image("/images/menu/halfStar.png")); // Đường dẫn đến ảnh sao nửa
            } else {
            star.setImage(new Image("/images/menu/star_empty.png")); // Đường dẫn đến ảnh sao trống
            }
            star.setFitHeight(15);
            star.setFitWidth(15);
            star.setPreserveRatio(true);
            ratingBox.getChildren().add(star);
        }
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", "Có lỗi xảy ra khi cập nhật đánh giá sao.", (Stage) ratingBox.getScene().getWindow());
        }
    }

    /**
     * Xử lý mượn sách.
     */
    @FXML
    private void handleBorrow() {
        if (BorrowReturnDAO.getInstance().isBorrowed(accountID, book.getBookID())) {
            util.ErrorDialog.showError("Thông báo", "Bạn đã mượn sách này rồi.", null);
            return;
        }
        BorrowDao borrowDAO = BorrowDao.getInstance();
        int selectedMemberId = accountID;
        int selectedDocumentId = book.getBookID();
        LocalDate borrowDate = LocalDate.now();
        LocalDate returnDate = borrowDate.plusMonths(1);

        Borrow newBorrow = new Borrow(
            selectedMemberId,
            selectedDocumentId,
            borrowDate,
            returnDate,
            "Borrowed"
        );
        try {
            borrowDAO.insert(newBorrow);
            util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được mượn thành công.", null);
        }
         catch (SQLException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Database Error",  e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error",  e.getMessage(), null);
        }
    
    }

    /**
     * Xử lý trả sách.
     */
    @FXML
    private void handleReturn() {
        ReturnDao returnDAO = ReturnDao.getInstance();
        int selectedMemberId = accountID;
        int selectedDocumentId = book.getBookID();
        int selectedBorrow;
        selectedBorrow = -1;
        if (BorrowReturnDAO.getInstance().isBorrowed(accountID, book.getBookID())) {
            selectedBorrow = BorrowReturnDAO.getInstance().getID(accountID, book.getBookID());
        }
        else {
            util.ErrorDialog.showError("Thông báo", "Bạn chưa mượn sách này", null);
            return;
        }
        try {
         int damagePercentage = (int) (Math.random() * 100); // Random damage percentage between 0 and 100
         Return returnRecord = new Return(selectedBorrow, 
         LocalDate.now(), damagePercentage);
         ReturnDao.getInstance().insert(returnRecord);
         util.ErrorDialog.showSuccess("Thành công", "Tài liệu đã được trả thành công.", null);
        }
        catch (SQLException e) {
            e.printStackTrace();
           util.ErrorDialog.showError("Database Error",  e.getMessage(), null);
       } catch (Exception e) {
        e.printStackTrace();
           util.ErrorDialog.showError("Error",  e.getMessage(), null);
       }
    
    }

    
     @FXML
    private void handleStarClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();
        boolean update = false;
        if (rating != 0) {
            update = true;
        }
        int currentRating = rating;
        switch (buttonId) {
            case "star1":
                rating = 1;
                break;
            case "star2":
                rating = 2;
                break;
            case "star3":
                rating = 3;
                break;
            case "star4":
                rating = 4;
                break;
            case "star5":
                rating = 5;
                break;
        }

        updateStarDisplay();
        System.out.println("User rated the book: " + rating + " stars");
        if (update) {
            try {
            book.setRating((book.getRating() * book.getReviewCount() - currentRating + rating) / book.getReviewCount());
            BookDao.getInstance().update(book, book.getBookID());
            updateBookDetails();
            ErrorDialog.showSuccess("Success", "Rating added successfully.", null);
            
            }
            catch (SQLException e) {
                e.printStackTrace();
                util.ErrorDialog.showError("Database Error", e.getMessage(), null);
            } catch (Exception e) {
                e.printStackTrace();
                util.ErrorDialog.showError("Error", e.getMessage(), null);
            }
            return;
        }
        try {
            BookDao.getInstance().addRating(book.getBookID(), rating);
            book = BookDao.getInstance().get(book.getBookID());
            updateBookDetails();
            ErrorDialog.showSuccess("Success", "Rating added successfully.", null);
        } catch (SQLException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Database Error", e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", e.getMessage(), null);
        }
        // Thực hiện các hành động khác như lưu rating vào cơ sở dữ liệu
    }

    private void updateStarDisplay() {
        Button[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setStyle("-fx-text-fill: gold;");
            } else {
                stars[i].setStyle("-fx-text-fill: black;");
            }
        }
    }

    @FXML
    private void handleQrCodeClick(MouseEvent event) {
        try {
            Image qrCodeImage = qrCodeImageView.getImage();
            if (qrCodeImage != null) {
            File outputFile = new File(System.getProperty("user.home") + "/Downloads/QRCodeBook.png");
            if (outputFile.exists()) {
                outputFile.delete();
            }
            ImageIO.write(SwingFXUtils.fromFXImage(qrCodeImage, null), "png", outputFile);
            util.ErrorDialog.showSuccess("Success", "QR code has been saved to Downloads folder.", null);
            } else {
            throw new Exception("QR code not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Error", "Có lỗi xảy ra khi tải mã QR.", null);
        }
    }
}
