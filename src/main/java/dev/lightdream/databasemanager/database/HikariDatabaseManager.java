package dev.lightdream.databasemanager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.dto.Driver;
import dev.lightdream.databasemanager.dto.IDatabaseEntry;
import dev.lightdream.databasemanager.dto.OrderBy;
import dev.lightdream.databasemanager.utils.DatabaseProcessor;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"unused", "DeprecatedIsStillUsed"})
@Deprecated // Deprecated just to show that it not what should be used
public abstract class HikariDatabaseManager extends DatabaseManager {

    private DatabaseProcessor processor;
    private Connection connection;

    public HikariDatabaseManager(DatabaseMain main) {
        super(main);
        connect();
    }

    @SuppressWarnings({"SwitchStatementWithTooFewBranches", "resource"})
    public void connect() {
        try {
            Logger.good("Connecting to the database with url " + getDatabaseURL());

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(getDatabaseURL());
            hikariConfig.setUsername(sqlConfig.username);
            hikariConfig.setPassword(sqlConfig.password);
            hikariConfig.setConnectionTestQuery("SELECT 1");

            if (sqlConfig.enableHighRateOfAccess) {
                hikariConfig.setMinimumIdle(5);
                hikariConfig.setMaximumPoolSize(50);
                hikariConfig.setConnectionTimeout(1000000000);
                hikariConfig.setIdleTimeout(600000000);
                hikariConfig.setMaxLifetime(1800000000);
            }

            switch (sqlConfig.driverName) {
                case "SQLITE":
                    hikariConfig.setDriverClassName("org.sqlite.JDBC");
                    hikariConfig.addDataSourceProperty("dataSourceClassName", "org.sqlite.SQLiteDataSource");
                    break;
            }

            HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

            connection = hikariDataSource.getConnection();
            processor = new DatabaseProcessor(sqlConfig.driver(main), this);
            setup();

            Logger.good("Connected to the database");
        } catch (Exception e) {
            Logger.error("The driver for the desired database type could not be loaded. Please check the release page and get the proper driver version");
            if (Debugger.isEnabled()) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @SneakyThrows
    @SuppressWarnings({"SqlNoDataSourceInspection", "resource"})
    public <T> List<T> getAll(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
            Logger.error("Class " + clazz.getSimpleName() + " is not annotated as a database table");
            return new ArrayList<>();
        }

        DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);

        List<T> output = new ArrayList<>();

        ResultSet rs = executeQuery(
                sqlConfig.driver(main).select(databaseTable.name()),
                new ArrayList<>()
        );

        while (rs.next()) {
            T obj = clazz.getDeclaredConstructor()
                    .newInstance();
            Field[] fields = obj.getClass()
                    .getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(DatabaseField.class)) {
                    continue;
                }
                DatabaseField databaseField = field.getAnnotation(DatabaseField.class);
                field.set(obj, getObject(field.getType(), rs.getObject(databaseField.column())));
            }
            output.add(obj);
            IDatabaseEntry entry = (IDatabaseEntry) obj;
            entry.setMain(main);
        }
        return output;
    }

    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries) {
        return get(clazz, queries, null, -1);
    }

    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, OrderBy order) {
        return get(clazz, queries, order, -1);
    }

    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, int limit) {
        return get(clazz, queries, null, limit);
    }

    @SneakyThrows
    @SuppressWarnings("resource")
    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, OrderBy order, int limit) {

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
                placeholder.append(key.replaceFirst("<", ""))
                        .append("<")
                        .append(formatQueryArgument(value))
                        .append(" AND ");
            } else if (key.startsWith(">")) {
                placeholder.append(key.replaceFirst(">", ""))
                        .append(">")
                        .append(formatQueryArgument(value))
                        .append(" AND ");
            } else if (key.startsWith("!=")) {
                placeholder.append(key.replaceFirst("!=", ""))
                        .append("!=")
                        .append(formatQueryArgument(value))
                        .append(" AND ");
            } else if (key.startsWith("=")) {
                placeholder.append(key.replaceFirst("=", ""))
                        .append("=")
                        .append(formatQueryArgument(value))
                        .append(" AND ");
            } else {
                placeholder.append(key)
                        .append("=")
                        .append(formatQueryArgument(value))
                        .append(" AND ");
            }
        }
        placeholder.append(" ");
        placeholder = new StringBuilder(placeholder.toString()
                .replace(" AND  ", ""));


        List<T> output = new ArrayList<>();
        ResultSet rs = executeQuery(
                sqlConfig.driver(main).select(
                        clazz.getAnnotation(DatabaseTable.class).name(),
                        placeholder.toString(),
                        order,
                        limit),
                new ArrayList<>()
        );

        while (rs.next()) {
            T obj = clazz.getDeclaredConstructor()
                    .newInstance();
            Field[] fields = obj.getClass()
                    .getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(DatabaseField.class)) {
                    continue;
                }
                DatabaseField databaseField = field.getAnnotation(DatabaseField.class);
                field.set(obj, getObject(field.getType(), rs.getObject(databaseField.column())));
            }
            ((IDatabaseEntry) obj).setMain(main);
            output.add(obj);
        }
        return output;
    }

    private Driver driver() {
        return sqlConfig.driver(main);
    }


    @SneakyThrows
    @Override
    public void createTable(Class<? extends IDatabaseEntry> clazz) {
        if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
            Logger.error("Class " + clazz.getSimpleName() + " is not annotated as a database table");
            return;
        }

        DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);

        executeUpdate(
                driver().createTable(processor, databaseTable, clazz),
                new ArrayList<>()
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setup() {
        for (Class<?> aClass : main.getReflections().getTypesAnnotatedWith(DatabaseTable.class)) {
            if (IDatabaseEntry.class.isAssignableFrom(aClass)) {
                setup((Class<? extends IDatabaseEntry>) aClass);
            }
        }
    }

    @Override
    public void setup(Class<? extends IDatabaseEntry> clazz) {
        createTable(clazz);
        //todo implement cache
    }

    @Override
    public void save() {
        //todo implement cache
        //todo save everything from cache
    }

    @SneakyThrows
    @Override
    public void save(IDatabaseEntry entry, boolean cache) {
        if (!entry.getClass()
                .isAnnotationPresent(DatabaseTable.class)) {
            Logger.error("The class " + entry.getClass()
                    .getSimpleName() + " is not annotated as " + DatabaseTable.class.getSimpleName());
            return;
        }

        DatabaseTable table = entry.getClass().getAnnotation(DatabaseTable.class);

        //insert
        StringBuilder placeholder1 = new StringBuilder();
        StringBuilder placeholder2 = new StringBuilder();
        StringBuilder placeholder3 = new StringBuilder();

        Field[] fields = entry.getClass()
                .getFields();

        String key = "";

        for (Field field : fields) {
            if (!field.isAnnotationPresent(DatabaseField.class)) {
                continue;
            }
            DatabaseField databaseField = field.getAnnotation(DatabaseField.class);

            if (databaseField.primaryKey()) {
                key = field.getName();
            }

            if (databaseField.autoGenerate() && field.get(entry) == null) {
                String query = sqlConfig.driver(main).select(
                        databaseField.column(),
                        table.name(),
                        "1",
                        OrderBy.DESCENDENT(databaseField.column()),
                        1
                );
                //noinspection resource
                ResultSet rs = executeQuery(query, new ArrayList<>());
                rs.next();
                int nextID = rs.getInt(1);

                field.set(entry, nextID);
            }


            String column = databaseField.column();
            String query = formatQueryArgument(field.get(entry));

            placeholder1.append(column)
                    .append(",");
            placeholder2.append(query)
                    .append(",");
            placeholder3.append(column)
                    .append("=")
                    .append(query)
                    .append(",");
        }

        placeholder1.append(",");
        placeholder2.append(",");
        placeholder3.append(",");

        placeholder1 = new StringBuilder(placeholder1.toString().replace(",,", ""));
        placeholder2 = new StringBuilder(placeholder2.toString().replace(",,", ""));
        placeholder3 = new StringBuilder(placeholder3.toString().replace(",,", ""));

        executeUpdate(
                sqlConfig.driver(main).insert(
                        table.name(),
                        placeholder1.toString(),
                        placeholder2.toString(),
                        placeholder3.toString()
                ),
                new ArrayList<>()
        );
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    @Override
    public void delete(IDatabaseEntry entry) {
        DatabaseTable databaseTable = entry.getClass().getAnnotation(DatabaseTable.class);

        executeUpdate(
                sqlConfig.driver(main).delete(
                        databaseTable.name(),
                        "id=?"
                ),
                Arrays.asList(entry.getID())
        );
    }

    @SneakyThrows
    @SuppressWarnings("resource")
    public void executeUpdate(String sql, @NotNull List<Object> values) {
        debug(sql, values);

        PreparedStatement statement = getConnection().prepareStatement(sql);

        for (int i = 0; i < values.size(); i++) {
            statement.setObject(i + 1, values.get(i));
        }

        statement.executeUpdate();
    }

    @SneakyThrows
    @SuppressWarnings("resource")
    public ResultSet executeQuery(String sql, @NotNull List<Object> values) {

        debug(sql, values);

        PreparedStatement statement;
        try {
            statement = getConnection().prepareStatement(sql);
        } catch (Throwable t) {
            Logger.error("The connection to the database has been lost trying to reconnect!");
            if (Debugger.isEnabled()) {
                t.printStackTrace();
            }
            connect();
            return executeQuery(sql, values);
        }

        for (int i = 0; i < values.size(); i++) {
            statement.setObject(i + 1, values.get(i));
        }

        return statement.executeQuery();
    }

    private void debug(String sql, List<Object> values) {
        String debugSQL = sql;
        for (Object value : values) {
            debugSQL = debugSQL.replaceFirst("\\?", value == null ? "null" : value.toString());
        }

        if (main.getSqlConfig().logSelect && debugSQL.startsWith("SELECT")) {
            Debugger.info(debugSQL);
        }

        if (main.getSqlConfig().logUpdate &&
                (debugSQL.startsWith("INSERT") ||
                        debugSQL.startsWith("CREATE") ||
                        debugSQL.startsWith("DELETE"))) {
            Debugger.info(debugSQL);
        }
    }

    @Override
    public void save(IDatabaseEntry object) {
        save(object, false);
    }
}
