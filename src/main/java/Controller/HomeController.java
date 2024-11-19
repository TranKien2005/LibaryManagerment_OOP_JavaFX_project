package Controller;

import DAO.BookDao;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Document;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class HomeController {

    @FXML
    private FlowPane fpTopBooks;

    @FXML
    private FlowPane fpRecommendedBooks;

    @FXML
    private FlowPane fpTrendingBooks;

    @FXML
    private FlowPane fpNewArrivals;

    @FXML
    private TextField tfSearch;

    @FXML
    private ScrollPane scrollPaneMain;

    @FXML
    private VBox topBooksSection;

    @FXML
    private VBox recommendedBooksSection;

    @FXML
    private VBox trendingBooksSection;

    private BookDao bookDao = BookDao.getInstance();
    private int newArrivalsPage = 0;
    private int searchPage = 0;
    private static final int PAGE_SIZE = 10;
    private boolean isSearching = false;
    private String currentSearchText = "";

    private List<Document> topBooks;
    private List<Document> favoriteBooks;
    private List<Document> trendingBooks;
    private List<Document> newArrivals;

    @FXML
    public void initialize() {
        try {
            // Load dữ liệu từ cơ sở dữ liệu
            topBooks = bookDao.getTopRatedBooks();
            favoriteBooks = bookDao.getFavorite(2);
            trendingBooks = bookDao.getTrendingBooks();
            newArrivals = bookDao.getAll(newArrivalsPage, PAGE_SIZE);

            // Hiển thị top sách
            for (Document book : topBooks) {
                VBox bookItem = createBookItem(book.getTitle(), book.getCoverImage(),  (int) book.getRating());
                fpTopBooks.getChildren().add(bookItem);
            }

            // Hiển thị sách yêu thích cho người dùng có AccountID = 2
            for (Document book : favoriteBooks) {
                VBox bookItem = createBookItem(book.getTitle(), book.getCoverImage(),  (int) book.getRating());
                fpRecommendedBooks.getChildren().add(bookItem);
            }

            // Hiển thị sách trending
            for (Document book : trendingBooks) {
                VBox bookItem = createBookItem(book.getTitle(), book.getCoverImage(),  (int) book.getRating());
                fpTrendingBooks.getChildren().add(bookItem);
            }

            // Hiển thị sách mới
            loadMoreNewArrivals();

             // Add listener for Enter key press in the search field
            tfSearch.setOnAction(event -> handleSearch());


            // Add scroll listener for lazy loading
            scrollPaneMain.vvalueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() == scrollPaneMain.getVmax()) {
                    try {
                        if (isSearching) {
                            loadMoreSearchResults();
                        } else {
                            loadMoreNewArrivals();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        // Handle the exception appropriately (e.g., show an error message to the user)
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message to the user)
        }
    }

    private void loadMoreNewArrivals() throws SQLException {
        List<Document> moreNewArrivals = bookDao.getAll(newArrivalsPage, PAGE_SIZE);
        for (Document book : moreNewArrivals) {
            VBox bookItem = createBookItem(book.getTitle(), book.getCoverImage(),  (int) book.getRating());
            fpNewArrivals.getChildren().add(bookItem);
        }
        newArrivalsPage++;
    }

    private void loadMoreSearchResults() throws SQLException {
        List<Document> searchResults = bookDao.searchNewArrivals(currentSearchText, searchPage, PAGE_SIZE);
        for (Document book : searchResults) {
            VBox bookItem = createBookItem(book.getTitle(), book.getCoverImage(),  (int) book.getRating());
            fpNewArrivals.getChildren().add(bookItem);
        }
        searchPage++;
    }

    private VBox createBookItem(String title, InputStream coverImageStream, int rating) {
        VBox vBox = new VBox(10);
        vBox.getStyleClass().add("book-item");
        ImageView imageView = new ImageView();
        if (coverImageStream != null) {
            Image image = new Image(coverImageStream);
            imageView.setImage(image);
        } else {
            Image image = new Image("/images/menu/coverArtUnknown.png");
            imageView.setImage(image);
        }
        imageView.setFitHeight(150);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("image-view");
        Label label = new Label(title);
        label.getStyleClass().add("label");
    
        // Tạo HBox để chứa các sao
        HBox starsBox = new HBox(2);
        starsBox.getStyleClass().add("stars-box");
        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView();
            if (i <= rating) {
                star.setImage(new Image("/images/menu/star_filled.png")); // Đường dẫn đến ảnh sao đen
            } else {
                star.setImage(new Image("/images/menu/star_empty.png")); // Đường dẫn đến ảnh sao trống
            }
            star.setFitHeight(15);
            star.setFitWidth(15);
            star.setPreserveRatio(true);
            starsBox.getChildren().add(star);
        }
    
        vBox.getChildren().addAll(imageView, label, starsBox);
        return vBox;
    }

    @FXML
    private void handleSearch() {
        currentSearchText = tfSearch.getText().toLowerCase().trim();
        isSearching = true;
        searchPage = 0;
    
        // Ẩn các VBox top, recommend, và trending
        topBooksSection.setVisible(false);
        topBooksSection.setManaged(false);
        recommendedBooksSection.setVisible(false);
        recommendedBooksSection.setManaged(false);
        trendingBooksSection.setVisible(false);
        trendingBooksSection.setManaged(false);
    
        // Hiển thị phần sách mới
        fpNewArrivals.setVisible(true);
        fpNewArrivals.setManaged(true);
    
        // Xóa các sách hiện tại trong phần sách mới
        fpNewArrivals.getChildren().clear();
        newArrivalsPage = 0; // Reset the page number for new arrivals
    
        try {
            // Tìm kiếm sách mới theo tiêu đề
            loadMoreSearchResults();
            scrollPaneMain.setVvalue(0);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message to the user)
        }
    }

    public void handleAboutUs() {
        System.out.println("Thông tin về chúng tôi");
        // TODO: Implement about us page navigation
    }

    public void handleContact() {
        System.out.println("Liên hệ");
        // TODO: Implement contact page navigation
    }

    @FXML
    private void handleReload() {
        isSearching = false;
        tfSearch.clear();
        
        // Hiển thị lại các VBox top, recommend, và trending
        topBooksSection.setVisible(true);
        topBooksSection.setManaged(true);
        recommendedBooksSection.setVisible(true);
        recommendedBooksSection.setManaged(true);
        trendingBooksSection.setVisible(true);
        trendingBooksSection.setManaged(true);
        
        // Xóa các sách hiện tại trong phần sách mới
        fpNewArrivals.getChildren().clear();
        newArrivalsPage = 0; // Reset the page number for new arrivals
        
        try {
            // Tải lại sách mới
            loadMoreNewArrivals();
            scrollPaneMain.setVvalue(0);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message to the user)
        }
    }
}