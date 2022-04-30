package dev.lightdream.databasemanager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.OrderBy;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.dto.IDatabaseEntry;
import dev.lightdream.lambda.LambdaExecutor;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@SuppressWarnings({"unused", "DeprecatedIsStillUsed"})
@Deprecated
public abstract class HikariDatabaseManager extends DatabaseManager {

    private Connection connection;

    public HikariDatabaseManager(DatabaseMain main) {
        super(main);
        connect();
    }

    @SuppressWarnings({"SwitchStatementWithTooFewBranches", "resource"})
    public void connect() {
        LambdaExecutor.LambdaCatch.NoReturnLambdaCatch.executeCatch(() -> {
            Logger.good("Connecting to the database with url " + getDatabaseURL());

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
            URLClassLoader child;
            URL url;
            switch (sqlConfig.driverName) {
                case "SQLITE":
                    config.setDriverClassName("org.sqlite.JDBC");
                    config.addDataSourceProperty("dataSourceClassName", "org.sqlite.SQLiteDataSource");
                    break;
            }
            HikariDataSource ds = new HikariDataSource(config);
            connection = ds.getConnection();
            setup();
            Logger.good("Connected to the database");
        }, e -> {
            Logger.error("The driver for the desired database type could not be loaded. Please check the release page and get the proper driver version");
            if (Debugger.isEnabled()) {
                e.printStackTrace();
            }
        });
    }

    public Connection getConnection() {
        return connection;
    }

    @SuppressWarnings({"SqlNoDataSourceInspection", "resource"})
    public <T> List<T> getAll(Class<T> clazz) {
        return LambdaExecutor.LambdaCatch.ReturnLambdaCatch.executeCatch(() -> {
            if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
                Logger.error("Class " + clazz.getSimpleName() + " is not annotated as a database table");
                return new ArrayList<>();
            }

            List<T> output = new ArrayList<>();

            ResultSet rs = executeQuery(sqlConfig.driver(main).selectAll.replace("%table%",
                    clazz.getAnnotation(DatabaseTable.class)
                            .table()), new ArrayList<>());
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
                output.add(obj);
                IDatabaseEntry entry = (IDatabaseEntry) obj;
                entry.setMain(main);
            }
            return output;
        });
    }

    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries) {
        return get(clazz, queries, null, -1, null);
    }

    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, String orderBy, OrderBy.OrderByType orderByType) {
        return get(clazz, queries, orderBy, -1, orderByType);
    }

    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, int limitCount) {
        return get(clazz, queries, null, limitCount, null);
    }

    @SuppressWarnings("resource")
    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, String orderBy, int limitCount, OrderBy.OrderByType orderByType) {
        return LambdaExecutor.LambdaCatch.ReturnLambdaCatch.executeCatch(() -> {

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

            String order = Objects.equals(orderBy,
                    "") || orderBy == null ? "" : orderByType == OrderBy.OrderByType.ASCENDANT ? sqlConfig.driver(main).orderAsc.replace(
                    "%order%",
                    orderBy) : sqlConfig.driver(main).orderDesc.replace("%order%", orderBy);
            String limit = limitCount == -1 ? "" : sqlConfig.driver(main).limit.replace("%limit%", String.valueOf(limitCount));

            List<T> output = new ArrayList<>();
            ResultSet rs = executeQuery(sqlConfig.driver(main).select.replace("%placeholder%", placeholder.toString())
                    .replace("%order%", order)
                    .replace("%limit%", limit)
                    .replace("%table%",
                            clazz.getAnnotation(DatabaseTable.class)
                                    .table()), new ArrayList<>());
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
                ((IDatabaseEntry) obj).setMain(main);
                output.add(obj);
            }
            return output;
        });
    }


    @SuppressWarnings("StringConcatenationInLoop")
    @Override
    public void createTable(Class<? extends IDatabaseEntry> clazz) {
        LambdaExecutor.LambdaCatch.NoReturnLambdaCatch.executeCatch(() -> {
            if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
                Logger.error("Class " + clazz.getSimpleName() + " is not annotated as a database table");
                return;
            }

            clazz.getDeclaredConstructor().newInstance();

            Object obj = clazz.getDeclaredConstructor().newInstance();
            String placeholder = "";
            String keys = "";

            Field[] fields = obj.getClass()
                    .getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(DatabaseField.class)) {
                    continue;
                }
                DatabaseField dbField = field.getAnnotation(DatabaseField.class);
                placeholder += dbField.columnName() + " " + getDataType(field.getType()) + " " + (dbField.unique() ? "UNIQUE " : "") + (dbField.autoGenerate() ? sqlConfig.driver(
                        main).autoIncrement : "") + ",";

                if (dbField.primaryKey()) {
                    keys += dbField.columnName();
                    if (getDataType(field.getType()).equals(sqlConfig.driver(main).dataTypes.get(String.class))) {
                        keys += "(255)";
                    }
                    keys += ",";
                }
            }

            keys += ",";
            keys = keys.replace(",,", "");
            placeholder += ",";
            placeholder = placeholder.replace(",,", "");

            executeUpdate(sqlConfig.driver(main).createTable.replace("%placeholder%", placeholder)
                    .replace("%keys%", keys)
                    .replace("%table%",
                            clazz.getAnnotation(DatabaseTable.class)
                                    .table()), new ArrayList<>());
        });
    }

    @Override
    public abstract void setup();

    @Override
    public void setup(Class<? extends IDatabaseEntry> clazz) {
        createTable(clazz);
        //todo implement cache
    }

    @Override
    public void save() {
        //todo implement cache
        //todo
    }

    @Override
    public void save(IDatabaseEntry entry, boolean cache) {
        LambdaExecutor.LambdaCatch.NoReturnLambdaCatch.executeCatch(() -> {
            if (!entry.getClass()
                    .isAnnotationPresent(DatabaseTable.class)) {
                Logger.error("The class " + entry.getClass()
                        .getSimpleName() + " is not annotated as " + DatabaseTable.class.getSimpleName());
                return;
            }

            //update
            /*
            if (entry.getID() != null) {
                StringBuilder placeholder = new StringBuilder();

                Field[] fields = entry.getClass()
                        .getFields();
                for (Field field : fields) {
                    if (!field.isAnnotationPresent(DatabaseField.class)) {
                        continue;
                    }
                    DatabaseField dbField = field.getAnnotation(DatabaseField.class);
                    if (dbField.autoGenerate()) {
                        continue;
                    }
                    placeholder.append(dbField.columnName())
                            .append("=")
                            .append(formatQueryArgument(field.get(entry)))
                            .append(",");
                }

                placeholder.append(",");
                placeholder = new StringBuilder(placeholder.toString()
                        .replace(",,", ""));

                executeUpdate(sqlConfig.driver(main).update.replace("%placeholder%", placeholder.toString())
                        .replace("%table%",
                                entry.getClass()
                                        .getAnnotation(DatabaseTable.class)
                                        .table()), Arrays.asList(entry.getID()));
                return;
            }
             */

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

                if (databaseField.autoGenerate()) {
                    continue;
                }

                String columnName = databaseField.columnName();
                String query = formatQueryArgument(field.get(entry));

                placeholder1.append(columnName)
                        .append(",");
                placeholder2.append(query)
                        .append(",");
                placeholder3.append(columnName)
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

            executeUpdate(sqlConfig.driver(main).insert
                    .replace("%placeholder-1%", placeholder1.toString())
                    .replace("%placeholder-2%", placeholder2.toString())
                    .replace("%placeholder-3%", placeholder3.toString())
                    .replace("%key%", key)
                    .replace("%table%",
                            entry.getClass()
                                    .getAnnotation(DatabaseTable.class)
                                    .table()), new ArrayList<>(

            ));
        });
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    @Override
    public void delete(IDatabaseEntry entry) {
        executeUpdate(sqlConfig.driver(main).delete.replace("%table%",
                entry.getClass()
                        .getAnnotation(DatabaseTable.class)
                        .table()), Arrays.asList(entry.getID()));
    }

    @SuppressWarnings("resource")
    public void executeUpdate(String sql, List<Object> values) {
        LambdaExecutor.LambdaCatch.NoReturnLambdaCatch.executeCatch(() -> {
            debug(sql, values);

            PreparedStatement statement = getConnection().prepareStatement(sql);

            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i + 1, values.get(i));
            }

            statement.executeUpdate();
        });
    }

    @SuppressWarnings("resource")
    public ResultSet executeQuery(String sql, List<Object> values) {
        return LambdaExecutor.LambdaCatch.ReturnLambdaCatch.executeCatch(() -> {
            debug(sql, values);

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
        });
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
