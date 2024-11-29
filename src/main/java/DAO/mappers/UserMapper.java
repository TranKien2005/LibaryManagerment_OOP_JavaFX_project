package DAO.mappers;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;

/**
 * Implementation of RowMapper for User entities.
 * Maps database rows from the User table to User domain objects.
 */
public class UserMapper implements RowMapper<User> {
    /**
     * Maps a database row to a User object.
     *
     * @param rs The ResultSet containing the database row
     * @return A new User object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs or this method is called on a closed ResultSet
     */
    @Override
    public User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("AccountID"),
            rs.getString("FullName"),
            rs.getString("Email"),
            rs.getString("Phone")
        );
    }
}
