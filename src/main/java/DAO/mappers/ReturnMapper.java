package DAO.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.Return;

/**
 * Implementation of RowMapper for Return entities.
 * Maps database rows from the ReturnTable to Return domain objects.
 */
public class ReturnMapper implements RowMapper<Return> {
    /**
     * Maps a database row to a Return object.
     * Converts SQL Date to LocalDate for the return date.
     *
     * @param rs The ResultSet containing the database row
     * @return A new Return object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs or this method is called on a closed ResultSet
     */
    @Override
    public Return mapRow(ResultSet rs) throws SQLException {
        return new Return(
            rs.getInt("ReturnID"),
            rs.getInt("BorrowID"),
            rs.getDate("ReturnDate").toLocalDate(),
            rs.getInt("DamagePercentage")
        );
    }
} 