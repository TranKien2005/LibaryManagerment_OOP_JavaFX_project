package DAO.utils;

/**
 * Utility class for building SQL queries specific to Book operations.
 * Provides static factory methods to create common Book-related queries.
 */
public class BookQueryBuilder {
    private static final String TABLE_NAME = "Book";
    
    /**
     * Creates a SELECT query for all book fields.
     * Includes standard book information and metadata like ratings.
     *
     * @return A QueryBuilder configured for selecting all book fields
     */
    public static QueryBuilder select() {
        return new QueryBuilder()
            .select("ID", "Title", "Author", "Category", "Publisher", 
                   "YearPublished", "AvailableCopies", "Description", 
                   "Image", "Rating", "NumberOfRatings")
            .from(TABLE_NAME);
    }

    public static QueryBuilder select(String...columns) {
        return new QueryBuilder().select(columns).from(TABLE_NAME);
    }
    
    public static QueryBuilder update() {
        return new QueryBuilder()
            .update(TABLE_NAME);
    }
    
    public static QueryBuilder delete() {
        return new QueryBuilder()
            .delete()
            .from(TABLE_NAME);
    }
    
    public static QueryBuilder insert() {
        return new QueryBuilder()
            .insertInto(TABLE_NAME)
            .columns("Title", "Author", "Category", "Publisher", 
                    "YearPublished", "AvailableCopies", "Image", 
                    "Description", "Rating", "NumberOfRatings");
    }
} 