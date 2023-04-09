package dev.lightdream.databasemanager.dto;

import dev.lightdream.messagebuilder.MessageBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            "INSERT INTO %table% (%columns%) VALUES(%values%) ON DUPLICATE KEY UPDATE %update%",
            "CREATE TABLE IF NOT EXISTS %table% (%columns%, PRIMARY KEY(%keys%))",
            "DELETE FROM %table% WHERE %condition%",
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
            "INSERT INTO %table% (%columns%) VALUES(%values%) ON CONFLICT(%key%) DO UPDATE SET %update%",
            "CREATE TABLE IF NOT EXISTS %table% (%columns%)",
            "DELETE FROM %table% WHERE %condition%",
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

}
