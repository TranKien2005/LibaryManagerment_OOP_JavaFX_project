package DAO.interfaces;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface defining basic database operations for a specific entity type.
 *
 * @param <T> The type of the entity this interface manages.
 */
public interface IQueryable<T> {

    /**
     * Retrieves all records of the entity from the database.
     *
     * @return A list of all entities of type T.
     * @throws SQLException If a database access error occurs.
     */
    List<T> getAll() throws SQLException;

    /**
     * Retrieves a single entity by its unique identifier.
     *
     * @param id The unique identifier of the entity.
     * @return The entity of type T with the specified ID, or null if not found.
     * @throws SQLException If a database access error occurs.
     */
    T get(int id) throws SQLException;

    /**
     * Inserts a new entity into the database.
     *
     * @param entity The entity to be inserted.
     * @throws SQLException If a database access error occurs or the operation fails.
     */
    void insert(T entity) throws SQLException;

    /**
     * Updates an existing entity in the database.
     *
     * @param entity The updated entity data.
     * @param id     The unique identifier of the entity to be updated.
     * @throws SQLException If a database access error occurs or the operation fails.
     */
    void update(T entity, int id) throws SQLException;

    /**
     * Deletes an entity from the database by its unique identifier.
     *
     * @param id The unique identifier of the entity to be deleted.
     * @throws SQLException If a database access error occurs or the operation fails.
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves the unique identifier of a specific entity.
     *
     * @param entity The entity whose ID is to be retrieved.
     * @return The unique identifier of the specified entity.
     * @throws SQLException If a database access error occurs.
     */
    int getID(T entity) throws SQLException;

    /**
     * Retrieves a list of all unique identifiers for the entity.
     *
     * @return A list of all unique IDs of the entity type.
     * @throws SQLException If a database access error occurs.
     */
    List<Integer> getAllID() throws SQLException;

}
