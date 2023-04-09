package dev.lightdream.databasemanager.dto;

import dev.lightdream.messagebuilder.MessageBuilder;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings({"unused", "DanglingJavadoc"})
/**
 * Can use the next dependencies if having problems
 * org.xerial:sqlite-jdbc
 * com.zsoltfabok:sqlite-dialect
 * com.enigmabridge:hibernate4-sqlite-dialect
 * mysql:mysql-connector-java
 **/
public class DriverConfig {

    public Driver MYSQL = new Driver(
            "SELECT %fields% FROM %table% WHERE %condition% %order% %limit%",
            "INSERT INTO %table% (%placeholder-1%) VALUES(%placeholder-2%) ON DUPLICATE KEY UPDATE %placeholder-3%",
            "CREATE TABLE IF NOT EXISTS %table% (%placeholder%, PRIMARY KEY(%keys%))",
            "DELETE FROM %table% WHERE id=?",
            "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = \"%database%\" AND TABLE_NAME = \"%table%\";",
            "ALTER TABLE %table% AUTO_INCREMENT=%autoincrement%",
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
    public Driver SQLITE = new Driver(
            "SELECT %fields% FROM %table% WHERE %condition% %order% %limit%",
            "INSERT INTO %table% (%placeholder-1%) VALUES(%placeholder-2%) ON CONFLICT(%key%) DO UPDATE SET %placeholder-3%",
            "CREATE TABLE IF NOT EXISTS %table% (%placeholder%)",
            "DELETE FROM %table% WHERE id=?",
            "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = \"%database%\" AND TABLE_NAME = \"%table%\";",
            "ALTER TABLE %table% AUTO_INCREMENT=%autoincrement%",
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
        // Queries
        public String select;
        public String insert;
        public String createTable;
        public String delete;

        // Data Structure
        public HashMap<Class<?>, String> dataTypes;

        // Keywords
        public String autoIncrement;
        public String orderDesc;
        public String orderAsc;
        public String limit;

        public Driver() {
        }


        public Driver(String select, String insert, String createTable, String delete,
                      String getAutoIncrement, String updateAutoIncrement, HashMap<Class<?>, String> dataTypes,
                      String autoIncrement, String orderDesc, String orderAsc, String limit) {
            this.select = select;
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
            this.insert = driver.insert;
            this.createTable = driver.createTable;
            this.delete = driver.delete;

            this.dataTypes = driver.dataTypes;

            this.autoIncrement = driver.autoIncrement;
            this.orderDesc = driver.orderDesc;
            this.orderAsc = driver.orderAsc;
            this.limit = driver.limit;
        }

        public String select(String table, String condition, OrderBy order, String limit) {
            // SELECT * FROM %table% WHERE %condition% %order% %limit%
            return new MessageBuilder(select)
                    .parse("%fields%", "*")
                    .parse("%table%", table)
                    .parse("%condition%", condition)
                    .parse("%order%", order.parse(this))
                    .parse("%limit%", limit)
                    .parse();
        }

        public String select(String table) {
            // SELECT * FROM %table% WHERE 1

            return new MessageBuilder(select)
                    .parse("%fields%", "*")
                    .parse("%table%", table)
                    .parse("%condition%", "1")
                    .parse("%order%", "")
                    .parse("%limit%", "")
                    .parse();
        }

        public String select(String fields, String table, String condition, OrderBy order, String limit) {
            // SELECT %fields% FROM %table% WHERE %condition% %order% %limit%

            return new MessageBuilder(select)
                    .parse("%fields%", fields)
                    .parse("%table%", table)
                    .parse("%condition%", condition)
                    .parse("%order%", order.parse(this))
                    .parse("%limit%", limit)
                    .parse();
        }
    }


}
