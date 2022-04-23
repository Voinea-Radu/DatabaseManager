package dev.lightdream.databasemanager.dto;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings({"unused", "DanglingJavadoc"})
/**
 * Can use the next dependencies if having problems
 * org.xerial:sqlite-jdbc:3.36.0.3
 * com.zsoltfabok:sqlite-dialect:1.0
 * com.enigmabridge:hibernate4-sqlite-dialect:0.1.2
 * mysql:mysql-connector-java:8.0.28
 **/
public class DriverConfig {

    public Driver MYSQL = new Driver("SELECT * FROM %table% WHERE %placeholder% %order% %limit%",
            "SELECT * FROM %table% WHERE 1",
            "INSERT INTO %table% (%placeholder-1%) VALUES(%placeholder-2%) ON DUPLICATE KEY UPDATE %placeholder-3%",
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
            "INSERT INTO %table% (%placeholder-1%) VALUES(%placeholder-2%) ON CONFLICT(%key%) DO UPDATE SET %placeholder-3%",
            "CREATE TABLE IF NOT EXISTS %table% (%placeholder%)",
            "DELETE FROM %table% WHERE id=?",
            new HashMap<Class<?>, String>() {{
                put(int.class, "INTEGER");
                put(Integer.class, "INTEGER");
                put(String.class, "TEXT");
                put(boolean.class, "BOOLEAN");
                put(Boolean.class, "BOOLEAN");
                put(float.class, "REAL");
                put(Float.class, "REAL");
                put(double.class, "REAL");
                put(Double.class, "REAL");
                put(UUID.class, "TEXT");
                put(Long.class, "BIGINT");
                put(long.class, "BIGINT");
            }},
            "PRIMARY KEY AUTOINCREMENT",
            "ORDER BY %order% DESC",
            "ORDER BY %order% ASC",
            "LIMIT %limit%");

    public DriverConfig() {
    }

    public void registerDataType(Class<?> clazz, String dataType) {
        MYSQL.dataTypes.put(clazz, dataType);
        MARIADB.dataTypes.put(clazz, dataType);
        SQLSERVER.dataTypes.put(clazz, dataType);
        POSTGRESQL.dataTypes.put(clazz, dataType);
        H2.dataTypes.put(clazz, dataType);
        SQLITE.dataTypes.put(clazz, dataType);
    }

    public static class Driver {
        public String select;
        public String selectAll;
        public String insert;
        public String createTable;
        public String delete;
        public HashMap<Class<?>, String> dataTypes;
        public String autoIncrement;
        public String orderDesc;
        public String orderAsc;
        public String limit;

        public Driver() {
        }

        public Driver(String select, String selectAll, String insert, String createTable, String delete, HashMap<Class<?>, String> dataTypes, String autoIncrement, String orderDesc, String orderAsc, String limit) {
            this.select = select;
            this.selectAll = selectAll;
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
