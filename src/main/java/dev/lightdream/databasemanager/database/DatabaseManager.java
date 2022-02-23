package dev.lightdream.databasemanager.database;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.dto.DatabaseEntry;
import dev.lightdream.databasemanager.dto.LambdaExecutor;
import dev.lightdream.databasemanager.dto.SQLConfig;
import dev.lightdream.logger.Logger;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.*;

public abstract class DatabaseManager implements IDatabaseManager {

    private final static String lineSeparator = ";line_separator;";
    private final static HashMap<Class<?>, LambdaExecutor> serializeMap = new HashMap<>();
    private final static HashMap<Class<?>, LambdaExecutor> deserializeMap = new HashMap<>();
    public final DatabaseMain main;
    public SQLConfig sqlConfig;
    public File dataFolder;

    public DatabaseManager(DatabaseMain main) {
        this.main = main;
        this.sqlConfig = main.getSqlConfig();
        this.dataFolder = main.getDataFolder();

        registerSDPair(String.class,
                object -> "\"" + object.toString()
                        .replace("\"", "")
                        .replace("'", "") + "\"",
                object -> object);

        registerSDPair(UUID.class, object -> "\"" + object.toString() + "\"",
                object -> UUID.fromString(object.toString()));

        registerSDPair(List.class, DatabaseManager::serializeList,
                DatabaseManager::deserializeList);
    }

    private static List<?> deserializeList(Object object) {
        try {
            String[] datas = object.toString()
                    .split(lineSeparator);
            Class<?> clazz = Class.forName(datas[0]);
            List<Object> lst = new ArrayList<>();
            for (String data : Arrays.asList(datas)
                    .subList(1, datas.length - 1)) {
                lst.add(getObject(clazz, data));
            }
            return lst;
        } catch (ClassNotFoundException e) {
            Logger.error("Malformed data for " + object);
            e.printStackTrace();
        }
        return null;
    }

    private static String serializeList(Object object) {
        @SuppressWarnings("unchecked") List<Object> lst = (List<Object>) object;
        StringBuilder output = new StringBuilder();
        lst.forEach(entry -> output.append(formatQueryArgument(entry))
                .append(lineSeparator));
        output.append(lineSeparator);
        return output.toString()
                .replace(lineSeparator + lineSeparator, "");
    }

    public static String formatQueryArgument(Object object) {
        if (object == null) {
            return "NULL";
        }
        Class<?> clazz = object.getClass();
        Object output = null;
        if (serializeMap.get(clazz) != null) {
            output = serializeMap.get(clazz)
                    .execute(object);
        }

        if (output != null) {
            return output.toString();
        }

        return object.toString();
    }

    public static Object getObject(Class<?> clazz, Object object) {
        Object output = null;
        if (deserializeMap.get(clazz) != null) {
            output = deserializeMap.get(clazz)
                    .execute(object);
        }

        if (output != null) {
            return output;
        }

        return object;
    }

    public String getDatabaseURL() {
        switch (sqlConfig.driverName) {
            case "MYSQL":
            case "MARIADB":
            case "POSTGRESQL":
                return "jdbc:" + sqlConfig.driverName.toLowerCase() + "://" + sqlConfig.host + ":" + sqlConfig.port + "/" + sqlConfig.database + "?useSSL=" + sqlConfig.useSSL + "&autoReconnect=true";
            case "SQLSERVER":
                return "jdbc:sqlserver://" + sqlConfig.host + ":" + sqlConfig.port + ";databaseName=" + sqlConfig.database;
            case "H2":
                return "jdbc:h2:file:" + sqlConfig.database;
            case "SQLITE":
                return "jdbc:sqlite:" + new File(dataFolder, sqlConfig.database + ".db");
            default:
                throw new UnsupportedOperationException("Unsupported driver (how did we get here?): " + sqlConfig.driverName);
        }
    }

    public String getDataType(Class<?> clazz) {
        String dbDataType = sqlConfig.driver(main).dataTypes.get(clazz);

        if (dbDataType != null) {
            return dbDataType;
        }

        Logger.error("DataType " + clazz.getSimpleName() + " is not a supported data type");
        return "";
    }

    @Override
    public void save(DatabaseEntry object) {

    }

    @SuppressWarnings("unused")
    public void registerSDPair(Class<?> clazz, LambdaExecutor serialize, LambdaExecutor deserialize) {
        serializeMap.put(clazz, serialize);
        deserializeMap.put(clazz, deserialize);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassLambda {
        public Class<?> clazz;
        public LambdaExecutor executor;
    }


}
