package DAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import DAO.base.BaseDao;
import DAO.enums.BookQueryTemplate;
import DAO.enums.SQLOperation;
import DAO.mappers.DocumentMapper;
import DAO.utils.BookQueryBuilder;
import DAO.utils.QueryBuilder;
import model.Document;

/**
 * Data Access Object (DAO) for managing Document (Book) entities in the database.
 * Implements the Singleton pattern and provides extended functionality for book-specific operations.
 * Handles operations like ratings, favorites, and trending books.
 */
public final class BookDao extends BaseDao<Document> {
    private static BookDao instance;

    private BookDao() {
        super("Book", new DocumentMapper());
    }

    /**
     * Gets the singleton instance of BookDao.
     *
     * @return The singleton instance of BookDao
     */
    public static BookDao getInstance() {
        if (instance == null) {
            instance = new BookDao();
        }
        return instance;
    }

    @Override
    public List<Document> getAll() throws SQLException {
        QueryBuilder qb = new QueryBuilder()
            .select("*")
            .from(tableName);
        return executeQuery(qb.build());
    }

    @Override
    public Document get(int id) throws SQLException {
        QueryBuilder qb = new QueryBuilder()
            .select("*")
            .from(tableName)
            .where("ID = ?");
        List<Document> results = executeQuery(qb.build(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void insert(Document document) throws SQLException {
        QueryBuilder qb = new QueryBuilder()
            .append(SQLOperation.INSERT)
            .append(tableName)
            .append("(Title, Author, Category, Publisher, YearPublished, AvailableCopies, Image, Description, Rating, NumberOfRatings)")
            .append(SQLOperation.VALUES)
            .append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            
        executeUpdate(qb.build(),
            document.getTitle(),
            document.getAuthor(),
            document.getCategory(),
            document.getPublisher(),
            document.getYearPublished(),
            document.getAvailableCopies(),
            document.getCoverImage(),
            document.getDescription(),
            document.getRating(),
            document.getReviewCount()
        );
    }

    @Override
    public int getID(Document document) throws SQLException {
        String query = BookQueryBuilder.select("ID")
            .where("Title = ? AND Author = ? AND Publisher = ?")
            .build();
        
        Object result = executeQueryRow(query,
            document.getTitle(),
            document.getAuthor(),
            document.getPublisher()
        );
        return result != null ? (int) result : -1;
    }

    @Override
    public void update(Document document, int id) throws SQLException {
        QueryBuilder qb = BookQueryBuilder.update()
            .set("Title", "Author", "Category", "Publisher", 
                 "YearPublished", "AvailableCopies")
            .where("ID = ?");
            
        executeUpdate(qb.build(),
            document.getTitle(),
            document.getAuthor(), 
            document.getCategory(),
            document.getPublisher(),
            document.getYearPublished(),
            document.getAvailableCopies(),
            id
        );
    }
    @Override
    public void delete(int id) throws SQLException {
        executeQuery(BookQueryBuilder.delete().where("id = ?").build(), id);
    }

    @Override
    public List<Integer> getAllID() throws SQLException {
        return executeQueryList(
            BookQueryBuilder.select("ID").build(),
            Integer.class,
            0
        );
    }

    public void setBookImage(int bookId, String imagePath) throws SQLException, IOException {
        try (FileInputStream fis = new FileInputStream(new File(imagePath))) {
            executeUpdate(
                BookQueryBuilder.update()
                    .set("Image")
                    .where("ID = ?")
                    .build(),
                fis, bookId
            );
        }
    }

    public void setBookImageByURL(int id, String imageUrl) throws SQLException, IOException, URISyntaxException {
        URL url = new URI(imageUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try (InputStream is = connection.getInputStream()) {
            executeUpdate(
                BookQueryBuilder.update()
                    .set("Image")
                    .where("ID = ?")
                    .build(),
                is, id
            );
        }
    }

    public void setDescription(int id, String description) throws SQLException {
        executeUpdate(
            BookQueryBuilder.update()
                .set("Description")
                .where("ID = ?")
                .build(),
            description, id
        );
    }

    /**
     * Adds a new rating for a book and updates the average rating.
     *
     * @param id The book ID
     * @param newRating The rating to add (typically 1-5)
     * @throws SQLException if database error occurs
     */
    public void addRating(int id, int newRating) throws SQLException {
        executeUpdate(
            BookQueryBuilder.update()
                .append("SET Rating = ((Rating * NumberOfRatings) + ?) / (NumberOfRatings + 1), ")
                .append("NumberOfRatings = NumberOfRatings + 1")
                .where("ID = ?")
                .build(),
            newRating, id
        );
    }

    /**
     * Retrieves the top-rated books (rating >= 4).
     *
     * @return List of top-rated books, ordered by rating
     * @throws SQLException if database error occurs
     */
    public List<Document> getTopRatedBooks() throws SQLException {
        return executeQuery(
            BookQueryBuilder.select()
                .where("Rating >= 4")
                .orderBy("Rating DESC")
                .limit(10)
                .build()
        );
    }

    /**
     * Retrieves favorite books for a specific account.
     *
     * @param accountId The account ID
     * @return List of favorite books
     * @throws SQLException if database error occurs
     */
    public List<Document> getFavorites(int accountId) throws SQLException {
        return executeQuery(
            BookQueryTemplate.GET_FAVORITES.get(),
            accountId,
            accountId,
            14 
        );
    }

    /**
     * Retrieves trending books based on recent activity.
     *
     * @return List of trending books
     * @throws SQLException if database error occurs
     */
    public List<Document> getTrendingBooks() throws SQLException {
        return executeQuery(
            BookQueryTemplate.GET_TRENDING.get(),
            7 
        );
    }

    public List<Document> getAll(int page, int pageSize) throws SQLException {
        return executeQuery(
            BookQueryBuilder.select()
                .orderBy("ID")
                .limit(pageSize)
                .offset(page * pageSize)
                .build()
        );
    }

    public List<Document> searchNewArrivals(String searchText, int page, int pageSize) throws SQLException {
        String pattern = "%" + searchText + "%";
        return executeQuery(
            BookQueryTemplate.SEARCH_NEW_ARRIVALS.get(),
            pattern, pattern, pattern, pattern,
            pattern, pattern, pattern, pattern,
            pageSize,
            page * pageSize
        );
    }

    public List<Document> getFavorite(int accountId) throws SQLException {
        String query = BookQueryTemplate.GET_FAVORITES.get();
        
        long paramCount = query.chars().filter(ch -> ch == '?').count();
        
        Object[] params = new Object[(int)paramCount];
        Arrays.fill(params, accountId);
        
        return executeQuery(query, params);
    }

    
}
