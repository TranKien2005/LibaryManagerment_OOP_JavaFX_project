package DAO.utils;

/**
 * Utility class for building SQL queries specific to Account operations.
 * Provides static factory methods to create common Account-related queries.
 */
public class AccountQueryBuilder {
    private static final String TABLE_NAME = "Account";
    private static final String[] ALL_COLUMNS = {
        "AccountID", "Username", "Password", "AccountType"
    };
    
    /**
     * Creates a SELECT query for all account columns.
     *
     * @return A QueryBuilder configured for selecting all account fields
     */
    public static QueryBuilder select() {
        return new QueryBuilder()
            .select(ALL_COLUMNS)
            .from(TABLE_NAME);
    }

    /**
     * Creates a SELECT query for specific account columns.
     *
     * @param columns The columns to select
     * @return A QueryBuilder configured for selecting specified account fields
     */
    public static QueryBuilder select(String...columns) {
        return new QueryBuilder()
            .select(columns)
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