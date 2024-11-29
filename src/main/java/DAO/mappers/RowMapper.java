package DAO.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Generic interface for mapping database result set rows to domain objects.
 * Implements the Data Mapper pattern to separate database query results from domain objects.
 *
 * @param <T> The type of object that this mapper will create from result set rows
 */
@FunctionalInterface
public interface RowMapper<T> {
    /**
     * Maps a single row of a ResultSet to an object of type T.
     *
     * @param rs The ResultSet to map, positioned at the current row
     * @return An object of type T mapped from the current ResultSet row
     * @throws SQLException if a database access error occurs or this method is called on a closed ResultSet
     */
    T mapRow(ResultSet rs) throws SQLException;
} 