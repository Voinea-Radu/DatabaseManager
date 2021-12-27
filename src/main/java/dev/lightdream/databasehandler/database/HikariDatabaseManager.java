package dev.lightdream.databasehandler.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lightdream.databasehandler.DatabaseMain;
import dev.lightdream.databasehandler.OrderByType;
import dev.lightdream.databasehandler.annotations.database.DatabaseField;
import dev.lightdream.databasehandler.annotations.database.DatabaseTable;
import dev.lightdream.databasehandler.dto.DatabaseEntry;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@SuppressWarnings("unused")
public abstract class HikariDatabaseManager extends DatabaseManager {

    private Connection connection;
    private DatabaseMain main;

    public HikariDatabaseManager(DatabaseMain main) {
        super(main);
        Logger.good("Connecting to the database");
        connect();
        Logger.good("Connected to the database");
    }

    @SneakyThrows
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getDatabaseURL());
        config.setUsername(sqlConfig.username);
        config.setPassword(sqlConfig.password);
        config.setConnectionTestQuery("SELECT 1");
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(50);
        config.setConnectionTimeout(1000000000);
        config.setIdleTimeout(600000000);
        config.setMaxLifetime(1800000000);
        switch (sqlConfig.driver) {
            case SQLITE:
                config.setDriverClassName("org.sqlite.JDBC");
                config.addDataSourceProperty("dataSourceClassName", "org.sqlite.SQLiteDataSource");
                break;
        }
        HikariDataSource ds = new HikariDataSource(config);
        connection = ds.getConnection();
        setup();
    }

    @SneakyThrows
    public Connection getConnection() {
        return connection;
    }

    @SuppressWarnings("SqlNoDataSourceInspection")
    @SneakyThrows
    @Override
    public <T> List<T> getAll(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
            Logger.error("Class " + clazz.getSimpleName() + " is not annotated as a database table");
            return new ArrayList<>();
        }

        List<T> output = new ArrayList<>();

        ResultSet rs = executeQuery(sqlConfig.driver.selectAll.replace("%table%", clazz.getAnnotation(DatabaseTable.class).table()), new ArrayList<>());
        while (rs.next()) {
            T obj = clazz.newInstance();
            Field[] fields = obj.getClass().getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(DatabaseField.class)) {
                    continue;
                }
                DatabaseField databaseField = field.getAnnotation(DatabaseField.class);
                field.set(obj, getObject(field.getType(), rs.getObject(databaseField.columnName())));
            }
            output.add(obj);
            DatabaseEntry entry = (DatabaseEntry) obj;
            entry.setMain(main);
        }
        return output;
    }

    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries) {
        return get(clazz, queries, null, -1, null);
    }

    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, String orderBy, OrderByType orderByType) {
        return get(clazz, queries, orderBy, -1, orderByType);
    }

    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, int limitCount) {
        return get(clazz, queries, null, limitCount, null);
    }

    @SneakyThrows
    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, String orderBy, int limitCount, OrderByType orderByType) {
        if (queries.size() == 0) {
            return getAll(clazz);
        }

        if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
            Logger.error("Class " + clazz.getSimpleName() + " is not annotated as a database table");
            return new ArrayList<>();
        }

        StringBuilder placeholder = new StringBuilder();
        for (String key : queries.keySet()) {
            Object value = queries.get(key);
            if (key.startsWith("<")) {
                placeholder.append(key.replaceFirst("<", "")).append("<").append(formatQueryArgument(value)).append(" AND ");
            } else if (key.startsWith(">")) {
                placeholder.append(key.replaceFirst(">", "")).append(">").append(formatQueryArgument(value)).append(" AND ");
            } else if (key.startsWith("!=")) {
                placeholder.append(key.replaceFirst("!=", "")).append("!=").append(formatQueryArgument(value)).append(" AND ");
            } else if (key.startsWith("=")) {
                placeholder.append(key.replaceFirst("=", "")).append("=").append(formatQueryArgument(value)).append(" AND ");
            } else {
                placeholder.append(key).append("=").append(formatQueryArgument(value)).append(" AND ");
            }
        }
        placeholder.append(" ");
        placeholder = new StringBuilder(placeholder.toString().replace(" AND  ", ""));

        String order = Objects.equals(orderBy, "") || orderBy == null ? "" : orderByType == OrderByType.ASCENDANT ? sqlConfig.driver.orderAsc.replace("%order%", orderBy) : sqlConfig.driver.orderDesc.replace("%order%", orderBy);
        String limit = limitCount == -1 ? "" : sqlConfig.driver.limit.replace("%limit%", String.valueOf(limitCount));

        List<T> output = new ArrayList<>();
        ResultSet rs = executeQuery(sqlConfig.driver.select.replace("%placeholder%", placeholder.toString()).replace("%order%", order).replace("%limit%", limit).replace("%table%", clazz.getAnnotation(DatabaseTable.class).table()), new ArrayList<>());
        while (rs.next()) {
            T obj = clazz.newInstance();
            Field[] fields = obj.getClass().getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(DatabaseField.class)) {
                    continue;
                }
                DatabaseField databaseField = field.getAnnotation(DatabaseField.class);
                field.set(obj, getObject(field.getType(), rs.getObject(databaseField.columnName())));
            }
            DatabaseEntry entry = (DatabaseEntry) obj;
            entry.setMain(main);
            output.add(obj);
        }
        return output;
    }


    @SuppressWarnings("StringConcatenationInLoop")
    @SneakyThrows
    @Override
    public void createTable(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
            Logger.error("Class " + clazz.getSimpleName() + " is not annotated as a database table");
            return;
        }

        Object obj = clazz.newInstance();
        String placeholder = "";
        String keys = "";

        Field[] fields = obj.getClass().getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DatabaseField.class)) {
                continue;
            }
            DatabaseField dbField = field.getAnnotation(DatabaseField.class);
            placeholder += dbField.columnName() + " " + getDataType(field.getType()) + " " + (dbField.unique() ? "UNIQUE " : "") + (dbField.autoGenerate() ? sqlConfig.driver.autoIncrement : "") + ",";

            if (dbField.primaryKey()) {
                keys += dbField.columnName() + ",";
            }
        }

        keys += ",";
        keys = keys.replace(",,", "");
        placeholder += ",";
        placeholder = placeholder.replace(",,", "");

        executeUpdate(sqlConfig.driver.createTable.replace("%placeholder%", placeholder).replace("%keys%", keys).replace("%table%", clazz.getAnnotation(DatabaseTable.class).table()), new ArrayList<>());
    }

    @Override
    public abstract void setup();

    @Override
    public void setup(Class<?> clazz) {
        createTable(clazz);
        //todo implement cache
    }

    @Override
    public void save() {
        //todo implement cache
        //todo
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    @SneakyThrows
    @Override
    public void save(DatabaseEntry entry, boolean cache) {
        if (!entry.getClass().isAnnotationPresent(DatabaseTable.class)) {
            Logger.error("The class " + entry.getClass().getSimpleName() + " is not annotated as " + DatabaseTable.class.getSimpleName());
            return;
        }

        //update
        if (entry.id != 0) {
            StringBuilder placeholder = new StringBuilder();

            Field[] fields = entry.getClass().getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(DatabaseField.class)) {
                    continue;
                }
                DatabaseField dbField = field.getAnnotation(DatabaseField.class);
                if (dbField.autoGenerate()) {
                    continue;
                }
                placeholder.append(dbField.columnName()).append("=").append(formatQueryArgument(field.get(entry))).append(",");
            }

            placeholder.append(",");
            placeholder = new StringBuilder(placeholder.toString().replace(",,", ""));

            executeUpdate(sqlConfig.driver.update.replace("%placeholder%", placeholder.toString()).replace("%table%", entry.getClass().getAnnotation(DatabaseTable.class).table()), Arrays.asList(entry.id));
            return;
        }

        //insert
        StringBuilder placeholder1 = new StringBuilder();
        StringBuilder placeholder2 = new StringBuilder();

        Field[] fields = entry.getClass().getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DatabaseField.class)) {
                continue;
            }
            DatabaseField databaseField = field.getAnnotation(DatabaseField.class);

            if (databaseField.autoGenerate()) {
                continue;
            }

            String columnName = databaseField.columnName();
            placeholder1.append(columnName).append(",");
            placeholder2.append(formatQueryArgument(field.get(entry))).append(",");
        }

        placeholder1.append(",");
        placeholder2.append(",");

        placeholder1 = new StringBuilder(placeholder1.toString().replace(",,", ""));
        placeholder2 = new StringBuilder(placeholder2.toString().replace(",,", ""));
        executeUpdate(sqlConfig.driver.insert.replace("%placeholder-1%", placeholder1.toString()).replace("%placeholder-2%", placeholder2.toString()).replace("%table%", entry.getClass().getAnnotation(DatabaseTable.class).table()), new ArrayList<>(

        ));
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    @SneakyThrows
    @Override
    public void delete(DatabaseEntry entry) {
        executeUpdate(sqlConfig.driver.delete.replace("%table%", entry.getClass().getAnnotation(DatabaseTable.class).table()), Arrays.asList(entry.id));
    }

    @SneakyThrows
    public void executeUpdate(String sql, List<Object> values) {
        Debugger.info(sql);
        PreparedStatement statement = getConnection().prepareStatement(sql);

        for (int i = 0; i < values.size(); i++) {
            statement.setObject(i + 1, values.get(i));
        }

        statement.executeUpdate();
    }

    @SneakyThrows
    public ResultSet executeQuery(String sql, List<Object> values) {
        Debugger.info(sql);
        PreparedStatement statement;
        try {
            statement = getConnection().prepareStatement(sql);
        } catch (Throwable t) {
            Logger.error("The connection to the database has been lost trying to reconnect!");
            connect();
            return executeQuery(sql, values);
        }

        for (int i = 0; i < values.size(); i++) {
            statement.setObject(i + 1, values.get(i));
        }

        return statement.executeQuery();
    }

    @Override
    public void save(DatabaseEntry object) {
        save(object, false);
    }
}
