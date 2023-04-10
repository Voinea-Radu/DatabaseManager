package dev.lightdream.databasemanager.database;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.dto.IDatabaseEntry;
import dev.lightdream.databasemanager.dto.OrderBy;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains deprecated methods and should not be used.
 * This is only to allow very low level access to the database in older implementations with the API.
 */
public abstract class DeprecatedHikariDatabaseManager extends DatabaseManager {

    public DeprecatedHikariDatabaseManager(DatabaseMain main) {
        super(main);
    }

    @SneakyThrows
    @Deprecated
    @SuppressWarnings({"SqlNoDataSourceInspection"})
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
                field.set(obj, DatabaseHelper.getObject(field.getType(), rs.getObject(databaseField.column())));
            }
            output.add(obj);
            IDatabaseEntry entry = (IDatabaseEntry) obj;
            entry.setMain(main);
        }
        return output;
    }


    @Deprecated
    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries) {
        return get(clazz, queries, null, -1);
    }

    @Deprecated
    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, OrderBy order) {
        return get(clazz, queries, order, -1);
    }

    @Deprecated
    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries, int limit) {
        return get(clazz, queries, null, limit);
    }

    @SneakyThrows
    @SuppressWarnings("resource")
    @Deprecated
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
                        .append(DatabaseHelper.formatQueryArgument(value))
                        .append(" AND ");
            } else if (key.startsWith(">")) {
                placeholder.append(key.replaceFirst(">", ""))
                        .append(">")
                        .append(DatabaseHelper.formatQueryArgument(value))
                        .append(" AND ");
            } else if (key.startsWith("!=")) {
                placeholder.append(key.replaceFirst("!=", ""))
                        .append("!=")
                        .append(DatabaseHelper.formatQueryArgument(value))
                        .append(" AND ");
            } else if (key.startsWith("=")) {
                placeholder.append(key.replaceFirst("=", ""))
                        .append("=")
                        .append(DatabaseHelper.formatQueryArgument(value))
                        .append(" AND ");
            } else {
                placeholder.append(key)
                        .append("=")
                        .append(DatabaseHelper.formatQueryArgument(value))
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
                field.set(obj, DatabaseHelper.getObject(field.getType(), rs.getObject(databaseField.column())));
            }
            ((IDatabaseEntry) obj).setMain(main);
            output.add(obj);
        }
        return output;
    }

    public abstract ResultSet executeQuery(String sql, @NotNull List<Object> values);
}
