package DAO.utils;

/**
 * Utility class for building SQL queries specific to BorrowReturn operations.
 * Extends QueryBuilder to provide specialized query construction for borrow/return records.
 */
public class BorrowReturnQueryBuilder extends QueryBuilder {
    private static final String TABLE_NAME = "BorrowReturnList";
    
    /**
     * Creates a SELECT query for all borrow/return fields.
     *
     * @return A QueryBuilder configured for selecting all borrow/return records
     */
    public static QueryBuilder select() {
        return new QueryBuilder().select("*").from(TABLE_NAME);
    }
    
    /**
     * Creates a SELECT query filtered by account.
     * Uses SUBSTRING_INDEX to extract account ID from the Member field.
     *
     * @return A QueryBuilder configured for selecting borrow/return records by account
     */
    public static QueryBuilder selectByAccount() {
        return new QueryBuilder()
            .select("*")
            .from(TABLE_NAME)
            .where("SUBSTRING_INDEX(Member, ' - ', 1) = ?");
    }
} 