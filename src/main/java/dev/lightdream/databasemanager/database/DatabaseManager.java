package dev.lightdream.databasemanager.database;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.dto.DatabaseEntry;
import dev.lightdream.databasemanager.dto.LambdaExecutor;
import dev.lightdream.databasemanager.dto.SQLConfig;
import dev.lightdream.logger.Debugger;
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

        registerSDPair(UUID.class, object -> "\"" + object.toString() + "\"", object -> UUID.fromString(object.toString()));

        registerSDPair(ArrayList.class, DatabaseManager::serializeList, DatabaseManager::deserializeList);

        registerSDPair(List.class, DatabaseManager::serializeList, DatabaseManager::deserializeList);

        registerSDPair(Long.class, object -> object, object -> Long.parseLong(object.toString()));
        registerSDPair(long.class, object -> object, object -> Long.parseLong(object.toString()));

        registerSDPair(Integer.class, object -> object, object -> Integer.parseInt(object.toString()));
        registerSDPair(int.class, object -> object, object -> Integer.parseInt(object.toString()));

        registerSDPair(Double.class, object -> object, object -> Double.parseDouble(object.toString()));
        registerSDPair(double.class, object -> object, object -> Double.parseDouble(object.toString()));

        registerSDPair(Float.class, object -> object, object -> Float.parseFloat(object.toString()));
        registerSDPair(float.class, object -> object, object -> Float.parseFloat(object.toString()));

        registerDataType(ArrayList.class, "TEXT");
        registerDataType(List.class, "TEXT");
    }

    private static List<?> deserializeList(Object object) {
        Debugger.info("Deserializing list " + object);
        if (object == null) {
            return null;
        }
        try {
            if (object.toString()
                    .equals("[]")) {
                return new ArrayList<>();
            }
            String[] datas = object.toString()
                    .split(lineSeparator);
            Class<?> clazz = Class.forName(datas[0]);
            List<Object> lst = new ArrayList<>();
            for (String data : Arrays.asList(datas)
                    .subList(1, datas.length)) {
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
        Debugger.info("Serializing list " + object);
        @SuppressWarnings("unchecked") List<Object> lst = (List<Object>) object;
        StringBuilder o1 = new StringBuilder();
        lst.forEach(entry -> o1.append(formatQueryArgument(entry))
                .append(lineSeparator));
        o1.append(lineSeparator);
        if (o1.toString()
                .equals(lineSeparator)) {
            return "\"[]\"";
        }
        StringBuilder output = new StringBuilder(lst.get(0)
                .getClass()
                .toString()
                .replace("class ", "")).append(lineSeparator)
                .append(o1);
        return ("\"" + output.append("\"")).replace(lineSeparator + lineSeparator, "");
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
        Debugger.info("Getting object of type " + clazz.getSimpleName());
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
        Debugger.info("Registering deserializer for " + clazz.getSimpleName());
        serializeMap.put(clazz, serialize);
        deserializeMap.put(clazz, deserialize);
        Debugger.info("The new deserializers are " + Arrays.toString(serializeMap.keySet()
                .toArray()));
    }

    public void registerDataType(Class<?> clazz, String dataType) {
        Debugger.info("Registering data type " + clazz.getSimpleName());
        main.getDriverConfig()
                .registerDataType(clazz, dataType);
        Debugger.info("New data types " + main.getDriverConfig().SQLITE.dataTypes.keySet());
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassLambda {
        public Class<?> clazz;
        public LambdaExecutor executor;
    }


}
