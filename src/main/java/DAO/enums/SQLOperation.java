package DAO.enums;

public enum SQLOperation {
    SELECT("SELECT"),
    INSERT("INSERT INTO"),
    UPDATE("UPDATE"),
    DELETE("DELETE FROM"),
    WHERE("WHERE"),
    VALUES("VALUES"),
    SET("SET"),
    FROM("FROM"),
    JOIN("JOIN"),
    LEFT_JOIN("LEFT JOIN"),
    GROUP_BY("GROUP BY"),
    ORDER_BY("ORDER BY"),
    LIMIT("LIMIT"),
    OFFSET("OFFSET"),
    AND("AND"),
    OR("OR"),
    LIKE("LIKE"),
    IN("IN"),
    BETWEEN("BETWEEN"),
    DESC("DESC"),
    ASC("ASC");

    private final String operation;

    SQLOperation(String operation) {
        this.operation = operation;
    }

    public String get() {
        return operation;
    }
} 