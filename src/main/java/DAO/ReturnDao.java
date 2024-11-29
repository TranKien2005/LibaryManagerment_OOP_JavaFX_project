package DAO;

import java.sql.SQLException;
import java.util.List;

import DAO.base.BaseDao;
import DAO.mappers.ReturnMapper;
import DAO.utils.QueryBuilder;
import model.Return;

/**
 * Data Access Object (DAO) for managing Return records in the database.
 * Implements the Singleton pattern and provides functionality for tracking book returns.
 * Handles return dates and damage assessment.
 */
public class ReturnDao extends BaseDao<Return> {
    private static ReturnDao instance;

    private ReturnDao() {
        super("ReturnTable", new ReturnMapper());
    }

    /**
     * Gets the singleton instance of ReturnDao.
     *
     * @return The singleton instance of ReturnDao
     */
    public static synchronized ReturnDao getInstance() {
        if (instance == null) {
            instance = new ReturnDao();
        }
        return instance;
    }

    @Override
    public List<Return> getAll() throws SQLException {
        return executeQuery(new QueryBuilder().select("*").from(tableName).build());
    }

    @Override
    public Return get(int id) throws SQLException {
        return executeQuery(
            new QueryBuilder()
                .select("*")
                .from(tableName)
                .where("BorrowID = ?")
                .build(),
            id
        ).stream().findFirst().orElse(null);
    }

    @Override
    public void insert(Return returnRecord) throws SQLException {
        executeUpdate(
            new QueryBuilder()
                .insertInto(tableName)
                .columns("BorrowID", "ReturnDate", "DamagePercentage")
                .values(3)
                .build(),
            returnRecord.getBorrowID(),
            returnRecord.getReturnDate(),
            returnRecord.getDamagePercentage()
        );
    }

    @Override
    public void delete(int id) throws SQLException {
        executeUpdate(
            new QueryBuilder()
                .delete()
                .from(tableName)
                .where("BorrowID = ?")
                .build(),
            id
        );
    }

    @Override
    public int getID(Return returnRecord) throws SQLException {
        String query = new QueryBuilder()
            .select("BorrowID")
            .from(tableName)
            .where("ReturnDate = ? AND DamagePercentage = ?")
            .build();
            
        Object result = executeQueryRow(query, 
            returnRecord.getReturnDate(),
            returnRecord.getDamagePercentage()
        );
        return result != null ? (int) result : -1;
    }

    @Override
    public List<Integer> getAllID() throws SQLException {
        return executeQueryList(
            new QueryBuilder()
                .select("BorrowID")
                .from(tableName)
                .build(),
            Integer.class,
            1
        );
    }

    @Override
    public void update(Return entity, int id) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
