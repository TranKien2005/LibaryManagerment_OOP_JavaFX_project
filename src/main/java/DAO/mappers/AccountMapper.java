package DAO.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.Account;

/**
 * Implementation of RowMapper for Account entities.
 * Maps database rows from the Account table to Account domain objects.
 */
public class AccountMapper implements RowMapper<Account> {
    /**
     * Maps a database row to an Account object.
     *
     * @param rs The ResultSet containing the database row
     * @return A new Account object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs or this method is called on a closed ResultSet
     */
    @Override
    public Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
            rs.getInt("AccountID"),
            rs.getString("Username"),
            rs.getString("Password"),
            rs.getString("AccountType")
        );
    }
} 