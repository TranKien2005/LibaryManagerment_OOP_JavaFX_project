package DAO.utils;

/**
 * Utility class for building SQL queries specific to User operations.
 * Provides static factory methods to create common User-related queries.
 */
public class UserQueryBuilder {
    private static final String TABLE_NAME = "User";
    private static final String[] ALL_COLUMNS = {"AccountID", "FullName", "Email", "Phone"};
    
    /**
     * Creates a SELECT query for all user fields.
     *
     * @return A QueryBuilder configured for selecting all user fields
     */
    public static QueryBuilder select() {
        return new QueryBuilder()
            .select(ALL_COLUMNS)
            .from(TABLE_NAME);
    }

    /**
     * Creates a SELECT query for specific user columns.
     *
     * @param columns The columns to select
     * @return A QueryBuilder configured for selecting specified user fields
     */
    public static QueryBuilder select(String...columns) {
        return new QueryBuilder()
            .select(columns)
            .from(TABLE_NAME);
    }

    public static QueryBuilder insert(String... columns) {
        return new QueryBuilder()
            .insertInto(TABLE_NAME)
            .columns(columns)
            .values(columns.length);
    }
    
    
    public static QueryBuilder selectId() {
        return new QueryBuilder()
            .select("AccountID")
            .from(TABLE_NAME);
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
            .columns(ALL_COLUMNS)
            .values(ALL_COLUMNS.length);
    }
} 