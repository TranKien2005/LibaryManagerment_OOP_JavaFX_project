package Controller;

import googleAPI.GoogleApiBookController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Document;

public class bookDetailController {

    @FXML
    private ImageView bookCoverImageView;
    @FXML
    private Label bookTitleLabel;
    @FXML
    private Label bookAuthorLabel;
    @FXML
    private Label bookPublisherLabel;
    @FXML
    private Label bookPublishedDateLabel;
    @FXML
    private Label bookRatingLabel;
    @FXML
    private TextArea bookDescriptionTextArea;
    @FXML
    private Button borrowButton;
    @FXML
    private Button returnButton;

    private GoogleApiBookController googleApiBookController = new GoogleApiBookController();


    @FXML
    public void initialize() {
        // Initialization code if needed
    }

    // public void setBookDetails(Document book) {
    //     bookTitleLabel.setText(book.getName());
    //     bookAuthorLabel.setText(book.getAuthor());
    //     bookPublisherLabel.setText(book.getPublisher());
    //     bookPublishedDateLabel.setText(String.valueOf(book.getYear()));
    //     bookRatingLabel.setText(String.valueOf(googleApiBookController.getBookRating(book.getName(), book.getAuthor())));
    //     bookDescriptionTextArea.setText(googleApiBookController.getBookDescription(book.getName(), book.getAuthor()));
    //     String bookCoverImageUrl = googleApiBookController.getBookImageLink(book.getName(), book.getAuthor());
    //     Image bookCoverImage = new Image(bookCoverImageUrl);
    //     bookCoverImageView.setImage(bookCoverImage);
    // }

    @FXML
    private void handleBorrowBook() {
        // Handle borrow book action
    }

    @FXML
    private void handleReturnBook() {
        // Handle return book action
    }
}