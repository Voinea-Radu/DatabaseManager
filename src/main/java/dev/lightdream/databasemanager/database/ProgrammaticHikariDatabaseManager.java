package dev.lightdream.databasemanager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.OrderBy;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.dto.DatabaseEntry;
import dev.lightdream.databasemanager.dto.QueryConstrains;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public abstract class ProgrammaticHikariDatabaseManager extends HikariDatabaseManager {
    private Connection connection;

    public ProgrammaticHikariDatabaseManager(DatabaseMain main) {
        super(main);
    }

    @SneakyThrows
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public void connect() {
        Class.forName("com.mysql.jdbc.Driver");
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
        switch (sqlConfig.driverName) {
            case "SQLITE":
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

    public <T> Query<T> get(Class<T> clazz) {
        return new Query<>(clazz);
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

    public class Query<T> {

        private final Class<T> clazz;
        private QueryConstrains queryConstrains = null;
        private OrderBy orderBy = null;
        private int limit = -1;

        public Query(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Query<T> query(QueryConstrains queryConstrains) {
            this.queryConstrains = queryConstrains;
            return this;
        }

        public Query<T> order(OrderBy orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public Query<T> limit(int limit) {
            this.limit = limit;
            return this;
        }

        @SneakyThrows
        public List<T> query() {
            if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
                Logger.error("Class " + clazz.getSimpleName() + " is not annotated as a database table");
                return new ArrayList<>();
            }

            return processResults(executeQuery(getFinalQuery(), new ArrayList<>()));
        }

        private String getFinalQuery() {
            String query = sqlConfig.driver(main).select;
            String placeholder = "1";
            String order = "";
            String limit = "";
            String table = clazz.getAnnotation(DatabaseTable.class)
                    .table();

            if (queryConstrains != null) {
                placeholder = queryConstrains.getFinalQuery();
            }

            if (orderBy != null) {
                if (orderBy.type.equals(OrderBy.OrderByType.ASCENDANT)) {
                    order = sqlConfig.driver(main).orderAsc.replace("%order%", orderBy.field);
                } else if (orderBy.type.equals(OrderBy.OrderByType.DESCENDENT)) {
                    order = sqlConfig.driver(main).orderDesc.replace("%order%", orderBy.field);
                }
            }

            if (this.limit != -1) {
                limit = sqlConfig.driver(main).limit.replace("%limit%", String.valueOf(this.limit));
            }

            query = query.replace("%placeholder%", placeholder)
                    .replace("%order%", order)
                    .replace("%limit%", limit)
                    .replace("%table%", table);

            return query;
        }

        @SneakyThrows
        private List<T> processResults(ResultSet rs) {
            List<T> output = new ArrayList<>();

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
                    field.set(obj, getObject(field.getType(), rs.getObject(databaseField.columnName())));
                }
                ((DatabaseEntry) obj).setMain(main);
                output.add(obj);
            }

            return output;
        }

    }


}
