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
                        rs.getInt("AvailableCopies")
                    );
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
        } catch (Exception e) {
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
            throw new SQLException( e.getMessage(), e);
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
            throw new SQLException("Error deleting document"+ e.getMessage(), e);
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
                        rs.getInt("AvailableCopies")
                    );
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
            throw new SQLException("Error getting all document IDs"+ e.getMessage(), e);
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
            throw new SQLException("Error setting book image"+ e.getMessage(), e);
        } catch (IOException e) {
            throw new IOException("Error reading image file"+ e.getMessage(), e);
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
}
