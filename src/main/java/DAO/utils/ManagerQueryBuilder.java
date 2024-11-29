package DAO.utils;

/**
 * Utility class for building SQL queries specific to Manager operations.
 * Provides static factory methods to create common Manager-related queries.
 */
public class ManagerQueryBuilder {
    private static final String TABLE_NAME = "Manager";
    
    /**
     * Creates a SELECT query for all manager fields.
     *
     * @param accountID The account ID to filter by (optional)
     * @return A QueryBuilder configured for selecting manager records
     */
    public static QueryBuilder select(String accountID) {
        return new QueryBuilder().select("*").from(TABLE_NAME);
    }

    /**
     * Creates a SELECT query for specific manager columns.
     *
     * @param columns The columns to select
     * @return A QueryBuilder configured for selecting specified manager fields
     */
    public static QueryBuilder select(String...columns) {
        return new QueryBuilder().select(columns).from(TABLE_NAME);
    }
    
    public static QueryBuilder insert() {
        return new QueryBuilder()
            .insertInto(TABLE_NAME)
            .columns("AccountID", "FullName", "Email", "Phone")
            .values(4);
    }
    
    public static QueryBuilder update() {
        return new QueryBuilder().update(TABLE_NAME);
    }
    
    public static QueryBuilder delete() {
        return new QueryBuilder().delete().from(TABLE_NAME);
    }
} 