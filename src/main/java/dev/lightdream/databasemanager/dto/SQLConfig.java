package dev.lightdream.databasemanager.dto;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("CanBeFinal")
@NoArgsConstructor
public class SQLConfig {

    public Driver driver = Driver.MYSQL;
    public String host = "162.55.135.18";
    public String database = "new_panel";
    public String username = "root";
    @SuppressWarnings("SpellCheckingInspection")
    public String password = "5xbqT7JUaxam7heFeWH577M4QPTTYCTx";
    public int port = 3306;
    public boolean useSSL = false;

    public enum Driver {
        MYSQL("SELECT * FROM %table% WHERE %placeholder%",
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
                "LIMIT %limit%"),
        MARIADB(MYSQL),
        SQLSERVER(MYSQL),
        POSTGRESQL(MYSQL),
        H2(MYSQL),
        SQLITE("SELECT * FROM %table% WHERE %placeholder% %order% %limit%",
                "SELECT * FROM %table% WHERE 1",
                "UPDATE %table% SET %placeholder% WHERE id=?",
                "INSERT INTO %table% (%placeholder-1%) VALUES(%placeholder-2%)",
                "CREATE TABLE IF NOT EXISTS %table% (%placeholder%)",
                "DELETE FROM %table% WHERE id=?", new HashMap<Class<?>, String>() {{
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

        public final String select;
        public final String selectAll;
        public final String update;
        public final String insert;
        public final String createTable;
        public final String delete;
        public final HashMap<Class<?>, String> dataTypes;
        public final String autoIncrement;
        public final String orderDesc;
        public final String orderAsc;
        public final String limit;

        Driver(String select, String selectAll, String update, String insert, String createTable, String delete, HashMap<Class<?>, String> dataTypes, String autoIncrement, String orderDesc,String orderAsc, String limit) {
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

        Driver(Driver driver) {
            this.select = driver.select;
            this.selectAll = driver.selectAll;
            this.update = driver.update;
            this.insert = driver.insert;
            this.createTable = driver.createTable;
            this.delete = driver.delete;
            this.dataTypes = driver.dataTypes;
            this.autoIncrement = driver.autoIncrement;
            this.orderDesc=driver.orderDesc;
            this.orderAsc=driver.orderAsc;
            this.limit= driver.limit;
        }

    }
}
