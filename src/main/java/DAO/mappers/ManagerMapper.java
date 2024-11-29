package DAO.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.Manager;

/**
 * Implementation of RowMapper for Manager entities.
 * Maps database rows from the Manager table to Manager domain objects.
 */
public class ManagerMapper implements RowMapper<Manager> {
    /**
     * Maps a database row to a Manager object.
     *
     * @param rs The ResultSet containing the database row
     * @return A new Manager object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs or this method is called on a closed ResultSet
     */
    @Override
    public Manager mapRow(ResultSet rs) throws SQLException {
        return new Manager(
            rs.getInt("AccountID"),
            rs.getString("FullName"),
            rs.getString("Email"),
            rs.getString("Phone")
        );
    }
} 