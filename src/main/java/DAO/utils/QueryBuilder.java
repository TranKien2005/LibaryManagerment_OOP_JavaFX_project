package DAO.utils;

import DAO.enums.SQLOperation;

/**
 * A fluent builder class for constructing SQL queries.
 * Provides methods to build SELECT, INSERT, UPDATE, and DELETE queries
 * in a type-safe and readable manner.
 */
public class QueryBuilder {
    private final StringBuilder query;

    /**
     * Constructs a new QueryBuilder with an empty query.
     */
    public QueryBuilder() {
        this.query = new StringBuilder();
    }

    /**
     * Appends an SQL operation to the query.
     *
     * @param operation The SQL operation to append
     * @return This QueryBuilder instance for method chaining
     */
    public QueryBuilder append(SQLOperation operation) {
        query.append(operation.get()).append(" ");
        return this;
    }

    /**
     * Appends arbitrary text to the query.
     *
     * @param text The text to append
     * @return This QueryBuilder instance for method chaining
     */
    public QueryBuilder append(String text) {
        query.append(text).append(" ");
        return this;
    }

    public QueryBuilder select(String... columns) {
        append(SQLOperation.SELECT);
        if (columns.length == 0) {
            append("*");
        } else {
            append(String.join(", ", columns));
        }
        return this;
    }

    public QueryBuilder from(String table) {
        append(SQLOperation.FROM).append(table);
        return this;
    }

    public QueryBuilder where(String condition) {
        append(SQLOperation.WHERE).append(condition);
        return this;
    }

    public QueryBuilder delete() {
        append(SQLOperation.DELETE);
        return this;
    }

    public QueryBuilder update(String table) {
        append(SQLOperation.UPDATE).append(table);
        return this;
    }

    public QueryBuilder set(String... columns) {
        append(SQLOperation.SET);
        StringBuilder setClause = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) setClause.append(", ");
            setClause.append(columns[i]).append(" = ?");
        }
        append(setClause.toString());
        return this;
    }

    public QueryBuilder insertInto(String table) {
        append(SQLOperation.INSERT).append(table);
        return this;
    }

    public QueryBuilder columns(String... columns) {
        append("(").append(String.join(", ", columns)).append(")");
        return this;
    }

    public QueryBuilder values(int count) {
        append(SQLOperation.VALUES).append("(");
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) placeholders.append(", ");
            placeholders.append("?");
        }
        append(placeholders.toString()).append(")");
        return this;
    }

    public QueryBuilder orderBy(String... columns) {
        append(SQLOperation.ORDER_BY)
            .append(String.join(", ", columns))
            .append(" ");
        return this;
    }

    public QueryBuilder limit(int limit) {
        append(SQLOperation.LIMIT).append(String.valueOf(limit));
        return this;
    }

    public QueryBuilder offset(int offset) {
        append(SQLOperation.OFFSET).append(String.valueOf(offset));
        return this;
    }

    public String build() {
        return query.toString().trim();
    }
} 