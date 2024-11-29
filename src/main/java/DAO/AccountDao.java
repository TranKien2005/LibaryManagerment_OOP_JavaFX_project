/**
 * Data Access Object (DAO) for managing Account entities in the database.
 * Implements the Singleton pattern to ensure a single instance of the DAO.
 * Provides CRUD operations and additional account-specific functionality.
 */

package DAO;

import java.sql.SQLException;
import java.util.List;

import DAO.base.BaseDao;
import DAO.mappers.AccountMapper;
import DAO.utils.AccountQueryBuilder;
import model.Account;

public class AccountDao extends BaseDao<Account> {
    private static AccountDao instance;

    private AccountDao() {
        super("Account", new AccountMapper());
    }

    /**
     * Gets the singleton instance of AccountDao.
     * Thread-safe implementation using synchronized keyword.
     *
     * @return The singleton instance of AccountDao
     */
    public static synchronized AccountDao getInstance() {
        if (instance == null) {
            instance = new AccountDao();
        }
        return instance;
    }

    @Override
    public List<Account> getAll() throws SQLException {
        return executeQuery(AccountQueryBuilder.select().build());
    }

    @Override
    public Account get(int id) throws SQLException {
        String qb = AccountQueryBuilder.select()
            .where("AccountID = ?").build();
        return executeSingleQuery(qb, id);
    }

    @Override
    public void insert(Account account) throws SQLException {
        executeUpdate(AccountQueryBuilder.insert().build(),
            account.getAccountID(),
            account.getUsername(),
            account.getPassword(),
            account.getAccountType()
        );
    }

    /**
     * Updates only the password of an account.
     * Validates that username and account type match before updating.
     *
     * @param account The account with updated password
     * @param id The ID of the account to update
     * @throws SQLException if database error occurs or validation fails
     */
    @Override
    public void update(Account account, int id) throws SQLException {
    
        Account existing = get(id);
        if (existing == null) {
            throw new SQLException("Account with the specified ID does not exist.");
        }
        if (!existing.getUsername().equals(account.getUsername()) || 
            !existing.getAccountType().equals(account.getAccountType())) {
            throw new SQLException("Username or AccountType does not match the existing record.");
        }

       String qb = AccountQueryBuilder.update()
            .set("Password")
            .where("AccountID = ?").build();
        executeUpdate(qb, account.getPassword(), id);
    }

    @Override
    public void delete(int id) throws SQLException {
        executeUpdate(AccountQueryBuilder.delete()
            .where("AccountID = ?")
            .build(), id);
    }

    @Override
    public int getID(Account account) throws SQLException {
        String qb = AccountQueryBuilder.select("AccountID")
            .where("Username = ? AND Password = ? AND AccountType = ?").build();
        Account result = executeSingleQuery(qb,
            account.getUsername(),
            account.getPassword(),
            account.getAccountType()
        );
        return result != null ? result.getAccountID() : -1;
    }

    @Override
    public List<Integer> getAllID() throws SQLException {
        return executeQueryList(
            AccountQueryBuilder.select("AccountID").build(),
            Integer.class,
            1
        );
    }

    /**
     * Updates the password for an account.
     * Direct password update without additional validation.
     *
     * @param id The ID of the account
     * @param newPassword The new password to set
     * @throws SQLException if database error occurs
     */
    public void updatePassword(int id, String newPassword) throws SQLException {
       String qb = AccountQueryBuilder.update()
            .set("Password")
            .where("AccountID = ?").build();
        executeUpdate(qb, newPassword, id);
    }

    private Account executeSingleQuery(String query, Object... params) throws SQLException {
        List<Account> results = executeQuery(query, params);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Retrieves an account by username.
     *
     * @param name The username to search for
     * @return The matching Account or null if not found
     * @throws SQLException if database error occurs
     */
    public Account getByUsername(String name) throws SQLException {
        String db = AccountQueryBuilder.select().where("username = ?").build();
        List<Account> results = executeQuery(db, name);
        return results.isEmpty() ? null : results.get(0);
    }
}
