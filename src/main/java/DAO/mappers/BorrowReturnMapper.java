package DAO.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.BorrowReturn;

/**
 * Implementation of RowMapper for BorrowReturn entities.
 * Maps database rows from the BorrowReturnList view to BorrowReturn domain objects.
 * Handles the combined view of borrow and return information.
 */
public class BorrowReturnMapper implements RowMapper<BorrowReturn> {
    /**
     * Maps a database row to a BorrowReturn object.
     * Includes both borrowing and return information from the combined view.
     *
     * @param rs The ResultSet containing the database row
     * @return A new BorrowReturn object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs or this method is called on a closed ResultSet
     */
    @Override
    public BorrowReturn mapRow(ResultSet rs) throws SQLException {
        return new BorrowReturn(
            rs.getInt("BorrowID"),
            rs.getString("Member"),
            rs.getString("Book"),
            rs.getDate("BorrowDate"),
            rs.getDate("ExpectedReturnDate"),
            rs.getString("Status"),
            rs.getDate("ReturnDate"),
            rs.getInt("DamagePercentage"),
            rs.getInt("PenaltyFee")
        );
    }
} 