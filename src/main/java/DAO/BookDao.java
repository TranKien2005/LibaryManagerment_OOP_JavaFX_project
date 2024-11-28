package DAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import model.Document;
import util.ThreadManager;

public final class BookDao {
    private static BookDao instance;

    private BookDao() {
        // Private constructor to prevent instantiation
    }

    public static BookDao getInstance() {
        if (instance == null) {
            instance = new BookDao();
        }
        return instance;
    }

    public List<Document> getAll() throws SQLException {
        List<Document> documents = new ArrayList<>();
        String query = "SELECT * FROM Book";
        Future<?> future = ThreadManager.submitSqlTask(() -> {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Document document = new Document(
                            rs.getInt("ID"),
                            rs.getString("Title"),
                            rs.getString("Author"),
                            rs.getString("Category"),
                            rs.getString("Publisher"),
                            rs.getInt("YearPublished"),
                            rs.getInt("AvailableCopies"));
                    document.setDescription(rs.getString("Description"));
                    document.setCoverImage(rs.getBinaryStream("Image"));
                    document.setRating(rs.getInt("Rating"));
                    document.setReviewCount(rs.getInt("NumberOfRatings"));
                    synchronized (documents) {
                        documents.add(document);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        try {
            future.get(); // Đợi cho đến khi tác vụ hoàn thành
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        }
        return documents;
    }

    public void insert(Document document) throws SQLException {
        String query = "INSERT INTO Book (Title, Author, Category, Publisher, YearPublished, AvailableCopies, Image, Description, Rating, NumberOfRatings) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getAuthor());
            pstmt.setString(3, document.getCategory());
            pstmt.setString(4, document.getPublisher());
            pstmt.setInt(5, document.getYearPublished());
            pstmt.setInt(6, document.getAvailableCopies());
            pstmt.setBinaryStream(7, document.getCoverImage());
            pstmt.setString(8, document.getDescription());
            pstmt.setDouble(9, document.getRating());
            pstmt.setInt(10, document.getReviewCount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    public int getID(Document document) throws SQLException {
        String query = "SELECT ID FROM Book WHERE Title = ? AND Author = ? AND Category = ? AND Publisher = ? AND YearPublished = ? AND AvailableCopies = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getAuthor());
            pstmt.setString(3, document.getCategory());
            pstmt.setString(4, document.getPublisher());
            pstmt.setInt(5, document.getYearPublished());
            pstmt.setInt(6, document.getAvailableCopies());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error getting document ID" + e.getMessage(), e);
        }
        return -1;
    }

    public void update(Document document, int id) throws SQLException {
        String query = "UPDATE Book SET Title = ?, Author = ?, Category = ?, Publisher = ?, YearPublished = ?, AvailableCopies = ? WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getAuthor());
            pstmt.setString(3, document.getCategory());
            pstmt.setString(4, document.getPublisher());
            pstmt.setInt(5, document.getYearPublished());
            pstmt.setInt(6, document.getAvailableCopies());
            pstmt.setInt(7, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating document" + e.getMessage(), e);
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM Book WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting document" + e.getMessage(), e);
        }
    }

    public Document get(int id) throws SQLException {
        String query = "SELECT * FROM Book WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Document document = new Document(
                            rs.getInt("ID"),
                            rs.getString("Title"),
                            rs.getString("Author"),
                            rs.getString("Category"),
                            rs.getString("Publisher"),
                            rs.getInt("YearPublished"),
                            rs.getInt("AvailableCopies"));
                    document.setDescription(rs.getString("Description"));
                    document.setCoverImage(rs.getBinaryStream("Image"));
                    document.setRating(rs.getDouble("Rating"));
                    document.setReviewCount(rs.getInt("NumberOfRatings"));
                    return document;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error getting document" + e.getMessage(), e);
        }
        return null;
    }

    public List<Integer> getAllID() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT ID FROM Book";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ids.add(rs.getInt("ID"));
            }
        } catch (SQLException e) {
            throw new SQLException("Error getting all document IDs" + e.getMessage(), e);
        }
        return ids;
    }

    public void setBookImage(int bookId, String imagePath) throws SQLException, IOException {
        String query = "UPDATE Book SET Image = ? WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                FileInputStream fis = new FileInputStream(imagePath)) {
            stmt.setBinaryStream(1, fis, (int) new File(imagePath).length());
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error setting book image" + e.getMessage(), e);
        } catch (IOException e) {
            throw new IOException("Error reading image file" + e.getMessage(), e);
        }
    }

    public void setBookImageByURL(int id, String imageUrl) throws SQLException, IOException, URISyntaxException {
        String query = "UPDATE Book SET Image = ? WHERE ID = ?";
        ThreadManager.submitSqlTask(() -> {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(query)) {

                // Open connection to the image URL
                URI uri = new URI(imageUrl);
                URL url = uri.toURL();
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setRequestMethod("GET");
                InputStream inputStream = httpConn.getInputStream();

                stmt.setBinaryStream(1, inputStream, httpConn.getContentLength());
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (URISyntaxException | SQLException | IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void setDescription(int id, String description) throws SQLException {
        String query = "UPDATE Book SET Description = ? WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, description);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error setting description" + e.getMessage(), e);
        }
    }

    public void addRating(int id, int newRating) throws SQLException {
        String query = "UPDATE Book SET Rating = ((Rating * NumberOfRatings) + ?) / (NumberOfRatings + 1), NumberOfRatings = NumberOfRatings + 1 WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, newRating);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error adding rating" + e.getMessage(), e);
        }
    }

    public List<Document> getTopRatedBooks() throws SQLException {
        List<Document> topRatedBooks = new ArrayList<>();
        String query = "SELECT * FROM Book ORDER BY Rating DESC LIMIT 7";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Document document = new Document(
                        rs.getInt("ID"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Category"),
                        rs.getString("Publisher"),
                        rs.getInt("YearPublished"),
                        rs.getInt("AvailableCopies"));
                document.setDescription(rs.getString("Description"));
                document.setCoverImage(rs.getBinaryStream("Image"));
                document.setRating(rs.getDouble("Rating"));
                document.setReviewCount(rs.getInt("NumberOfRatings"));
                topRatedBooks.add(document);
            }
        } catch (SQLException e) {
            throw new SQLException("Error getting top rated books" + e.getMessage(), e);
        }
        return topRatedBooks;
    }

    public List<Document> getFavorite(int accountId) throws SQLException {
        List<Document> recommendedBooks = new ArrayList<>();
        String query = """
                       WITH BorrowHistory AS (
                           -- L\u1ea5y danh s\u00e1ch s\u00e1ch, th\u1ec3 lo\u1ea1i v\u00e0 t\u00e1c gi\u1ea3 m\u00e0 ng\u01b0\u1eddi d\u00f9ng \u0111\u00e3 m\u01b0\u1ee3n
                           SELECT 
                               b.`BookID` AS BookID,
                               bk.`Title`,
                               bk.Category,
                               bk.Author,
                               COUNT(*) AS BorrowCount
                           FROM Borrow b
                           JOIN Book bk ON b.BookID = bk.`ID`
                           WHERE b.AccountID = 1
                           GROUP BY b.`BookID`
                       ),
                       CategoryScore AS (
                           -- T\u00ednh \u0111i\u1ec3m theo th\u1ec3 lo\u1ea1i d\u1ef1a tr\u00ean l\u1ecbch s\u1eed m\u01b0\u1ee3n
                           SELECT 
                               bh.Category,
                               SUM(bh.BorrowCount) AS CategoryScore
                           FROM BorrowHistory bh
                           GROUP BY bh.Category
                       ),
                       AuthorScore AS (
                           SELECT 
                               bh.Author,
                               SUM(bh.BorrowCount) AS AuthorScore
                           FROM BorrowHistory bh
                           GROUP BY bh.Author
                       ),
                       Ranking AS (
                           -- T\u00ednh t\u1ed5ng \u0111i\u1ec3m cho t\u1eebng s\u00e1ch
                           SELECT
                               bk.*, 
                               COALESCE(cs.CategoryScore, 0) + COALESCE(au.AuthorScore, 0) AS RankingScore
                           FROM Book bk
                           LEFT JOIN CategoryScore cs ON bk.Category = cs.Category
                           LEFT JOIN AuthorScore au ON bk.Author = au.Author
                           WHERE bk.ID NOT IN (
                               -- Lo\u1ea1i s\u00e1ch m\u00e0 ng\u01b0\u1eddi d\u00f9ng \u0111\u00e3 m\u01b0\u1ee3n
                               SELECT b.BookID
                               FROM Borrow b
                               WHERE b.AccountID = 1
                           )
                       )
                       -- L\u1ea5y 5 s\u00e1ch c\u00f3 \u0111i\u1ec3m cao nh\u1ea5t
                       SELECT 
                           *
                       FROM Ranking
                       ORDER BY RankingScore DESC LIMIT 14;""";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Document document = new Document(
                            rs.getInt("ID"),
                            rs.getString("Title"),
                            rs.getString("Author"),
                            rs.getString("Category"),
                            rs.getString("Publisher"), // Thêm Publisher nếu cần
                            rs.getInt("YearPublished"), // Thêm YearPublished nếu cần
                            rs.getInt("AvailableCopies") // Thêm AvailableCopies nếu cần
                    );
                    document.setDescription(rs.getString("Description")); // Thêm Description nếu cần
                    document.setCoverImage(rs.getBinaryStream("Image")); // Thêm Image nếu cần
                    document.setRating(rs.getDouble("Rating")); // Thêm Rating nếu cần
                    document.setReviewCount(rs.getInt("NumberOfRatings")); // Thêm NumberOfRatings nếu cần
                    recommendedBooks.add(document);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error getting search-based recommendations: " + e.getMessage(), e);
        }
        return recommendedBooks;
    }

    public List<Document> getTrendingBooks() throws SQLException {
        List<Document> trendingBooks = new ArrayList<>();
        String query = "SELECT " +
                "    book.ID, " +
                "    book.Title, " +
                "    book.Author, " +
                "    book.Category, " +
                "    book.Publisher, " +
                "    book.YearPublished, " +
                "    book.AvailableCopies, " +
                "    book.Description, " +
                "    book.Image, " +
                "    book.Rating, " +
                "    book.NumberOfRatings, " +
                "    COUNT(borrow.BookID) AS borrow_count " +
                "FROM " +
                "    Book AS book " +
                "LEFT JOIN " +
                "    Borrow AS borrow ON book.ID = borrow.BookID " +
                "    AND borrow.BorrowDate >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) " +
                "GROUP BY " +
                "    book.ID, book.Title, book.Author, book.Category, book.Publisher, book.YearPublished, book.AvailableCopies, book.Description, book.Image, book.Rating, book.NumberOfRatings "
                +
                "ORDER BY " +
                "    borrow_count DESC, " +
                "    book.Rating DESC " +
                "LIMIT 7";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Document document = new Document(
                        rs.getInt("ID"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Category"),
                        rs.getString("Publisher"),
                        rs.getInt("YearPublished"),
                        rs.getInt("AvailableCopies"));
                document.setDescription(rs.getString("Description"));
                document.setCoverImage(rs.getBinaryStream("Image"));
                document.setRating(rs.getDouble("Rating"));
                document.setReviewCount(rs.getInt("NumberOfRatings"));
                trendingBooks.add(document);
            }
        } catch (SQLException e) {
            throw new SQLException("Error getting trending books" + e.getMessage(), e);
        }
        return trendingBooks;
    }

    public List<Document> getAll(int page, int pageSize) throws SQLException {
        List<Document> documents = new ArrayList<>();
        String query = "SELECT ID, Title, Author, Category, Publisher, YearPublished, AvailableCopies, Description, Image, Rating, NumberOfRatings FROM Book ORDER BY YearPublished DESC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, page * pageSize);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Document document = new Document(
                        rs.getInt("ID"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Category"),
                        rs.getString("Publisher"),
                        rs.getInt("YearPublished"),
                        rs.getInt("AvailableCopies"));
                document.setDescription(rs.getString("Description"));
                document.setCoverImage(rs.getBinaryStream("Image"));
                document.setRating(rs.getInt("Rating"));
                document.setReviewCount(rs.getInt("NumberOfRatings"));
                synchronized (documents) {
                    documents.add(document);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return documents;
    }

    public List<Document> searchNewArrivals(String searchText, int page, int pageSize) throws SQLException {
        List<Document> documents = new ArrayList<>();
        String query = "SELECT ID, Title, Author, Category, Publisher, YearPublished, AvailableCopies, Description, Image, Rating, NumberOfRatings "
                +
                "FROM Book " +
                "WHERE Title LIKE ? OR Category LIKE ? OR Author LIKE ? OR Description LIKE ? " +
                "ORDER BY " +
                "CASE " +
                "WHEN Title LIKE ? THEN 1 " +
                "WHEN Category LIKE ? THEN 2 " +
                "WHEN Author LIKE ? THEN 3 " +
                "WHEN Description LIKE ? THEN 4 " +
                "ELSE 5 " +
                "END, " +
                "YearPublished DESC " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);
            stmt.setString(6, searchPattern);
            stmt.setString(7, searchPattern);
            stmt.setString(8, searchPattern);
            stmt.setInt(9, pageSize);
            stmt.setInt(10, page * pageSize);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Document document = new Document(
                        rs.getInt("ID"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Category"),
                        rs.getString("Publisher"),
                        rs.getInt("YearPublished"),
                        rs.getInt("AvailableCopies"));
                document.setDescription(rs.getString("Description"));
                document.setCoverImage(rs.getBinaryStream("Image"));
                document.setRating(rs.getInt("Rating"));
                document.setReviewCount(rs.getInt("NumberOfRatings"));
                synchronized (documents) {
                    documents.add(document);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return documents;
    }
}
