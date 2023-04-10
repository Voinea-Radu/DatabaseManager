package dev.lightdream.databasemanager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.dto.Driver;
import dev.lightdream.databasemanager.dto.IDatabaseEntry;
import dev.lightdream.databasemanager.dto.PreparedQuery;
import dev.lightdream.databasemanager.utils.DatabaseProcessor;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public abstract class HikariDatabaseManager extends DeprecatedHikariDatabaseManager {

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

        executeUpdate(driver().createTable(processor, databaseTable, clazz));
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

        executeUpdate(sqlConfig.driver(main).insert(processor, entry, table));
    }

    @Override
    public void delete(IDatabaseEntry entry) {
        DatabaseTable databaseTable = entry.getClass().getAnnotation(DatabaseTable.class);

        executeUpdate(
                sqlConfig.driver(main).delete(
                        databaseTable.name(),
                        "id=" + entry.getID()
                )
        );
    }

    public void executeUpdate(String sql) {
        executeUpdate(sql, new ArrayList<>());
    }

    public void executeUpdate(PreparedQuery query) {
        executeUpdate(query.query, query.values);
    }

    public void executeQuery(String sql) {
        executeUpdate(sql, new ArrayList<>());
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
