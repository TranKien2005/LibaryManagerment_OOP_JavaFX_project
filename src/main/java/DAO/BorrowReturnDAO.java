package DAO;

import java.sql.SQLException;
import java.util.List;

import DAO.base.BaseDao;
import DAO.mappers.BorrowReturnMapper;
import DAO.utils.BorrowReturnQueryBuilder;
import model.BorrowReturn;

public class BorrowReturnDAO extends BaseDao<BorrowReturn> {
    private static BorrowReturnDAO instance;

    private BorrowReturnDAO() {
        super("BorrowReturnList", new BorrowReturnMapper());
    }

    public static synchronized BorrowReturnDAO getInstance() {
        if (instance == null) {
            instance = new BorrowReturnDAO();
        }
        return instance;
    }

    @Override
    public List<BorrowReturn> getAll() throws SQLException {
        return executeQuery(BorrowReturnQueryBuilder.select().build());
    }

    public List<BorrowReturn> getByAccountId(int accountId) throws SQLException {
        return executeQuery(
            BorrowReturnQueryBuilder.selectByAccount().build(),
            accountId
        );
    }

    public boolean isBorrowed(int accountId, int bookId) throws SQLException {
        String query = "SELECT 1 FROM BorrowReturnList WHERE " +
                      "SUBSTRING_INDEX(Member, ' - ', 1) = ? AND " +
                      "SUBSTRING_INDEX(Book, ' - ', 1) = ? AND " +
                      "Status = 'Borrowed'";
        return executeQueryRow(query, accountId, bookId) != null;
    }

    public int getID(int accountId, int bookId) throws SQLException {
        String query = "SELECT BorrowID FROM BorrowReturnList WHERE " +
                      "SUBSTRING_INDEX(Member, ' - ', 1) = ? AND " +
                      "SUBSTRING_INDEX(Book, ' - ', 1) = ? AND " +
                      "Status = 'Borrowed'";
        Object result = executeQueryRow(query, accountId, bookId);
        return result != null ? (int) result : -1;
    }

    @Override
    public BorrowReturn get(int id) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public void insert(BorrowReturn entity) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public void update(BorrowReturn entity, int id) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(int id) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public int getID(BorrowReturn entity) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'getID'");
    }

    @Override
    public List<Integer> getAllID() throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'getAllID'");
    }
}