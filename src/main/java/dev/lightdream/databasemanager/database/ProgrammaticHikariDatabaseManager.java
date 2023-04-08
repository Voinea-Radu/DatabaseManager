package dev.lightdream.databasemanager.database;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.dto.IDatabaseEntry;
import dev.lightdream.databasemanager.dto.OrderBy;
import dev.lightdream.databasemanager.dto.QueryConstrains;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"deprecation", "unused"})
public abstract class ProgrammaticHikariDatabaseManager extends HikariDatabaseManager {
    private Connection connection;

    public ProgrammaticHikariDatabaseManager(DatabaseMain main) {
        super(main);
    }

    public <T> Query<T> get(Class<T> clazz) {
        return new Query<>(clazz);
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
                    Object result = getObject(field.getType(), rs.getObject(databaseField.column()));
                    if (result == null) {
                        field.set(obj, result);
                        continue;
                    }
                    if ((field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) &&
                            result.getClass().equals(Integer.class)) {
                        Integer object = (Integer) result;
                        boolean bObject = object == 1;
                        field.set(obj, bObject);
                        continue;
                    }

                    field.set(obj, result);
                }
                ((IDatabaseEntry) obj).setMain(main);
                output.add(obj);
            }

            return output;
        }


    }


}
