package DAO;
import java.sql.SQLException;
import java.util.List;

import DAO.base.BaseDao;
import DAO.mappers.UserMapper;
import DAO.utils.UserQueryBuilder;
import model.User;

public class UserDao extends BaseDao<User> {
    private static UserDao instance;

    private UserDao() {
        super("User", new UserMapper());
    }

    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }
    
    @Override
    public List<User> getAll() throws SQLException {
        return executeQuery(UserQueryBuilder.select().build());
    }

    @Override
    public User get(int id) throws SQLException {
        return executeQuery(UserQueryBuilder.select().where("AccountID = ?").build(), id).get(0);
    }

    @Override
    public void insert(User user) throws SQLException {
        executeUpdate(UserQueryBuilder.insert().build(),
            user.getAccountID(), user.getFullName(), user.getEmail(), user.getPhone());
    }

    @Override
    public void update(User user, int id) throws SQLException {
        executeUpdate(UserQueryBuilder.update()
            .set("FullName", "Email", "Phone")
            .where("AccountID = ?")
            .build(),
            user.getFullName(), user.getEmail(), user.getPhone(), id);
    }

    @Override
    public void delete(int id) throws SQLException {
        executeUpdate(UserQueryBuilder.delete().where("AccountID = ?").build(), id);
    }

    @Override
    public int getID(User user) throws SQLException {
        String query = UserQueryBuilder
            .select("AccountID")
            .where("FullName = ? AND Email = ? AND Phone = ?")
            .build();

        Object result = executeQueryRow(query, user.getFullName(), user.getEmail(), user.getPhone());
        return result != null ? (int) result : -1;
    }
    

    @Override
    public List<Integer> getAllID() throws SQLException {
        return executeQueryList(UserQueryBuilder.select("AccountId").build(), Integer.class, 1);
    }

}