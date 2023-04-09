package dev.lightdream.databasemanager.config;

import dev.lightdream.databasemanager.dto.Driver;
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
            new MessageBuilder("SELECT %fields% FROM %table% WHERE %condition% %order% %limit%"),
            new MessageBuilder("INSERT INTO %table% (%columns%) VALUES(%values%) ON DUPLICATE KEY UPDATE %update%"),
            new MessageBuilder("CREATE TABLE IF NOT EXISTS %table% (%columns%, PRIMARY KEY(%keys%))"),
            new MessageBuilder("DELETE FROM %table% WHERE %condition%"),
            new MessageBuilder("AUTO_INCREMENT"),
            new MessageBuilder("ORDER BY %order)% DESC"),
            new MessageBuilder("ORDER BY %order% ASC"),
            new MessageBuilder("LIMIT %limit%"),
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
            }}
    );
    public Driver MARIADB = new Driver(MYSQL);
    public Driver SQLSERVER = new Driver(MYSQL);
    public Driver POSTGRESQL = new Driver(MYSQL);
    public Driver H2 = new Driver(MYSQL);
    public Driver SQLITE = new Driver(
            new MessageBuilder("SELECT %fields% FROM %table% WHERE %condition% %order% %limit%"),
            new MessageBuilder("INSERT INTO %table% (%columns%) VALUES(%values%) ON CONFLICT(%key%) DO UPDATE SET %update%"),
            new MessageBuilder("CREATE TABLE IF NOT EXISTS %table% (%columns%)"),
            new MessageBuilder("DELETE FROM %table% WHERE %condition%"),
            new MessageBuilder("PRIMARY KEY AUTOINCREMENT"),
            new MessageBuilder("ORDER BY %order% DESC"),
            new MessageBuilder("ORDER BY %order% ASC"),
            new MessageBuilder("LIMIT %limit%"),
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
            }}
    );

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
