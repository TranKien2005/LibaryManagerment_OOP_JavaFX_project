package DAO;

import java.sql.SQLException;
import java.util.List;

import DAO.base.BaseDao;
import DAO.mappers.ManagerMapper;
import DAO.utils.ManagerQueryBuilder;
import model.Manager;

public class ManagerDao extends BaseDao<Manager> {
    private static ManagerDao instance;

    private ManagerDao() {
        super("Manager", new ManagerMapper());
    }

    public static synchronized ManagerDao getInstance() {
        if (instance == null) {
            instance = new ManagerDao();
        }
        return instance;
    }

    @Override
    public List<Manager> getAll() throws SQLException {
        return executeQuery(ManagerQueryBuilder.select().build());
    }

    @Override
    public Manager get(int id) throws SQLException {
        return executeQuery(
            ManagerQueryBuilder.select()
                .where("AccountID = ?")
                .build(),
            id
        ).stream().findFirst().orElse(null);
    }

    @Override
    public void insert(Manager manager) throws SQLException {
        executeUpdate(
            ManagerQueryBuilder.insert().build(),
            manager.getAccountID(),
            manager.getFullName(),
            manager.getEmail(),
            manager.getPhone()
        );
    }

    @Override
    public void update(Manager manager, int id) throws SQLException {
        executeUpdate(
            ManagerQueryBuilder.update()
                .set("FullName", "Email", "Phone")
                .where("AccountID = ?")
                .build(),
            manager.getFullName(),
            manager.getEmail(),
            manager.getPhone(),
            id
        );
    }

    @Override
    public void delete(int id) throws SQLException {
        executeUpdate(
            ManagerQueryBuilder.delete()
                .where("AccountID = ?")
                .build(),
            id
        );
    }

    @Override
    public int getID(Manager manager) throws SQLException {
        Object result = executeQueryRow(
            ManagerQueryBuilder.select("AccountID")
                .where("FullName = ? AND Email = ? AND Phone = ?")
                .build(),
            manager.getFullName(),
            manager.getEmail(),
            manager.getPhone()
        );
        return result != null ? (int) result : -1;
    }

    @Override
    public List<Integer> getAllID() throws SQLException {
        return executeQueryList(
            ManagerQueryBuilder.select("AccountID").build(),
            Integer.class,
            1
        );
    }
}
