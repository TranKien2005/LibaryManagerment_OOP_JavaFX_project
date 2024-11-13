package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import model.Document;
import util.ThreadManager;

public final class BookDao implements DaoInterface<Document> {
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

    @Override
public List<Document> getAll() {
    List<Document> documents = new ArrayList<>();
    String query = "SELECT * FROM Book";
    Future<?> future = ThreadManager.submitSqlTask(() -> {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Document document = new Document(
                    rs.getString("Title"),
                    rs.getString("Author"),
                    rs.getString("Category"),
                    rs.getString("Publisher"),
                    rs.getInt("YearPublished"),
                    rs.getInt("AvailableCopies")
                );
                synchronized (documents) {
                    documents.add(document);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    });
    try {
        future.get(); // Đợi cho đến khi tác vụ hoàn thành
    } catch (Exception e) {
        e.printStackTrace();
    }
    return documents;
}
    @Override
    public void insert(Document document) {
        String query = "INSERT INTO Book (Title, Author, Category, Publisher, YearPublished, AvailableCopies) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getAuthor());
            pstmt.setString(3, document.getCategory());
            pstmt.setString(4, document.getPublisher());
            pstmt.setInt(5, document.getYearPublished());
            pstmt.setInt(6, document.getAvailableCopies());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Document document, int id) {
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
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM Book WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Document get(int id) {
        String query = "SELECT * FROM Book WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Document(
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Category"),
                        rs.getString("Publisher"),
                        rs.getInt("YearPublished"),
                        rs.getInt("AvailableCopies")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getID(Document document) {
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
            e.printStackTrace();
        }
        return -1; // Return -1 if the document is not found
    }

    public List<Integer> getAllID() {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT ID FROM Book";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ids.add(rs.getInt("ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public static void main(String[] args) {
        BookDao bookDao = BookDao.getInstance();

        // Create a new document
        Document newDocument = new Document("Sample Title", "Sample Author", "Sample Category", "Sample Publisher", 2023, 10);
        bookDao.insert(newDocument);

        // Get all documents
        List<Document> documents = bookDao.getAll();
        for (Document doc : documents) {
            System.out.println(doc);
        }

        // Get a document by ID
        int id = bookDao.getID(newDocument);
        Document document = bookDao.get(id);
        System.out.println("Retrieved Document: " + document);

        // Update the document
        document.setTitle("Updated Title");
        bookDao.update(document, id);

        // Get all IDs
        List<Integer> ids = bookDao.getAllID();
        System.out.println("All IDs: " + ids);

        // Delete the document
        bookDao.delete(id);
    }
}