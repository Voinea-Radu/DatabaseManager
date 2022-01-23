package dev.lightdream.databasemanager.dto;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("unused")
public class DriverConfig {

    public Driver MYSQL = new Driver("SELECT * FROM %table% WHERE %placeholder%",
            "SELECT * FROM %table% WHERE 1",
            "UPDATE %table% SET %placeholder% WHERE id=?",
            "INSERT INTO %table% (%placeholder-1%) VALUES(%placeholder-2%)",
            "CREATE TABLE IF NOT EXISTS %table% (%placeholder%, PRIMARY KEY(%keys%))",
            "DELETE FROM %table% WHERE id=?",
            new HashMap<Class<?>, String>() {{
                put(int.class, "INT");
                put(Integer.class, "INT");
                put(String.class, "TEXT");
                put(boolean.class, "BOOLEAN");
                put(Boolean.class, "BOOLEAN");
                put(float.class, "FLOAT");
                put(Float.class, "FLOAT");
                put(double.class, "DOUBLE");
                put(Double.class, "DOUBLE");
                put(UUID.class, "TEXT");
                put(Long.class, "BIGINT");
                put(long.class, "BIGINT");
            }},
            "AUTO_INCREMENT",
            "ORDER BY %order% DESC",
            "ORDER BY %order% ASC",
            "LIMIT %limit%");
    public Driver MARIADB = new Driver(MYSQL);
    public Driver SQLSERVER = new Driver(MYSQL);
    public Driver POSTGRESQL = new Driver(MYSQL);
    public Driver H2 = new Driver(MYSQL);
    public Driver SQLITE = new Driver("SELECT * FROM %table% WHERE %placeholder% %order% %limit%",
            "SELECT * FROM %table% WHERE 1",
            "UPDATE %table% SET %placeholder% WHERE id=?",
            "INSERT INTO %table% (%placeholder-1%) VALUES(%placeholder-2%)",
            "CREATE TABLE IF NOT EXISTS %table% (%placeholder%)",
            "DELETE FROM %table% WHERE id=?",
            new HashMap<Class<?>, String>() {{
                put(int.class, "INTEGER");
                put(Integer.class, "INTEGER");
                put(String.class, "TEXT(10000)");
                put(boolean.class, "BOOLEAN");
                put(Boolean.class, "BOOLEAN");
                put(float.class, "REAL");
                put(Float.class, "REAL");
                put(double.class, "REAL");
                put(Double.class, "REAL");
                put(UUID.class, "TEXT(10000)");
                put(Long.class, "BIGINT");
                put(long.class, "BIGINT");
            }},
            "PRIMARY KEY AUTOINCREMENT",
            "ORDER BY %order% DESC",
            "ORDER BY %order% ASC",
            "LIMIT %limit%");

    @NoArgsConstructor
    public static class Driver {
        public String select;
        public String selectAll;
        public String update;
        public String insert;
        public String createTable;
        public String delete;
        public HashMap<Class<?>, String> dataTypes;
        public String autoIncrement;
        public String orderDesc;
        public String orderAsc;
        public String limit;

        public Driver(String select, String selectAll, String update, String insert, String createTable, String delete, HashMap<Class<?>, String> dataTypes, String autoIncrement, String orderDesc, String orderAsc, String limit) {
            this.select = select;
            this.selectAll = selectAll;
            this.update = update;
            this.insert = insert;
            this.createTable = createTable;
            this.delete = delete;
            this.dataTypes = dataTypes;
            this.autoIncrement = autoIncrement;
            this.orderDesc = orderDesc;
            this.orderAsc = orderAsc;
            this.limit = limit;
        }

        public Driver(Driver driver) {
            this.select = driver.select;
            this.selectAll = driver.selectAll;
            this.update = driver.update;
            this.insert = driver.insert;
            this.createTable = driver.createTable;
            this.delete = driver.delete;
            this.dataTypes = driver.dataTypes;
            this.autoIncrement = driver.autoIncrement;
            this.orderDesc = driver.orderDesc;
            this.orderAsc = driver.orderAsc;
            this.limit = driver.limit;
        }
    }


}
