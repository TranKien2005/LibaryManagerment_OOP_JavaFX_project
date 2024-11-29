package DAO.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import DAO.DatabaseConnection;
import DAO.interfaces.IQueryable;
import DAO.mappers.RowMapper;
import util.ThreadManager;

/**
 * Abstract base class for Data Access Objects (DAOs).
 * Provides common database operations such as executing queries and updates.
 * 
 * @param <T> The type of object this DAO handles.
 */
public abstract class BaseDao<T> implements IQueryable<T> {
    protected final DatabaseConnection dbConnection;
    protected final String tableName;
    protected final RowMapper<T> mapper;

    /**
     * Constructor for BaseDao.
     * 
     * @param tableName The name of the database table.
     * @param mapper    A mapper to convert a ResultSet row into an object of type T.
     */
    protected BaseDao(String tableName, RowMapper<T> mapper) {
        this.dbConnection = DatabaseConnection.getInstance();
        this.tableName = tableName;
        this.mapper = mapper;
    }

    /**
     * Executes a SQL query and maps the result set to a list of objects.
     * 
     * @param query  The SQL query to execute.
     * @param params Parameters for the SQL query.
     * @return A list of objects of type T mapped from the result set.
     * @throws SQLException If an error occurs during query execution.
     */
    protected List<T> executeQuery(String query, Object... params) throws SQLException {
        long expectedParams = query.chars().filter(ch -> ch == '?').count();
        if (params.length != expectedParams) {
            throw new SQLException(String.format(
                "Query expects %d parameters but %d were provided. Query: %s", 
                expectedParams, params.length, query));
        }
        List<T> results = new ArrayList<>();
        Future<?> future = ThreadManager.submitSqlTask(() -> {
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(mapper.mapRow(rs));
                    }
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException("SQL execution error: " + e.getMessage(), e);
            }
        });
        
        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("SQL task was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("SQL task execution failed: " + e.getCause().getMessage(), e.getCause());
        } catch (TimeoutException e) {
            throw new RuntimeException("SQL task timed out", e);
        }
        
        return results;
    }

    /**
     * Executes a SQL update query (INSERT, UPDATE, DELETE).
     * 
     * @param query  The SQL query to execute.
     * @param params Parameters for the SQL query.
     * @return The number of rows affected by the update.
     * @throws SQLException If an error occurs during query execution.
     */
    protected int executeUpdate(String query, Object... params) throws SQLException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes a SQL query and returns a single row as a map.
     * 
     * @param query  The SQL query to execute.
     * @param params Parameters for the SQL query.
     * @return A map representing a single row of the result set, or null if no row is found.
     * @throws SQLException If an error occurs during query execution.
     */
    protected Map<String, Object> executeQueryRow(String query, Object... params) throws SQLException {
        Future<Map<String, Object>> future = ThreadManager.submitSqlTask(() -> {
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
    
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            row.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        return row; // Trả về dòng đầu tiên
                    }
                    return null; // Không có kết quả
                }
            } catch (SQLException e) {
                throw new RuntimeException("SQL execution error: " + e.getMessage(), e);
            }
        });
        try {
            // Chờ kết quả trong tối đa 30 giây
            return future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Khôi phục trạng thái ngắt
            throw new RuntimeException("SQL task was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("SQL task execution failed: " + e.getCause().getMessage(), e.getCause());
        } catch (TimeoutException e) {
            throw new RuntimeException("SQL task timed out", e);
        }
    }
    

    /**
     * Executes a SQL query and maps a single column of the result set to a list of objects.
     * 
     * @param <E>         The type of the column values.
     * @param query       The SQL query to execute.
     * @param type        The class type of the column values.
     * @param columnIndex The index of the column to retrieve (1-based).
     * @param params      Parameters for the SQL query.
     * @return A list of objects of type E from the specified column.
     * @throws SQLException If an error occurs during query execution.
     */
    protected <E> List<E> executeQueryList(String query, Class<E> type, int columnIndex, Object... params) throws SQLException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                List<E> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(type.cast(rs.getObject(columnIndex)));
                }
                return results;
            }
        }
    }
}
