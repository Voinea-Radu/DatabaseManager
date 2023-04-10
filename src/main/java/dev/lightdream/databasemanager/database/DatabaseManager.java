package dev.lightdream.databasemanager.database;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.config.SQLConfig;
import dev.lightdream.databasemanager.dto.IDatabaseEntry;
import dev.lightdream.lambda.lambda.ReturnArgLambdaExecutor;
import dev.lightdream.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DatabaseManager implements IDatabaseManager {

    public final DatabaseMain main;
    public SQLConfig sqlConfig;
    public File dataFolder;

    public DatabaseManager(DatabaseMain main) {
        DatabaseHelper.init(main);

        this.main = main;
        this.sqlConfig = main.getSqlConfig();
        this.dataFolder = main.getDataFolder();

        registerSDPair(String.class,
                object -> "\"" + object
                        .replace("\"", "")
                        .replace("'", "") + "\"",
                Object::toString);

        //registerSDPair(UUID.class, object -> "\"" + object.toString() + "\"", object -> UUID.fromString(object.toString()));
        //registerSDPair(ArrayList.class, DatabaseHelper::serializeList, DatabaseHelper::deserializeList);
        //registerSDPair(List.class, DatabaseHelper::serializeList, DatabaseHelper::deserializeList);
        registerSDPair(Long.class, object -> object, object -> {
            if (object == null) {
                return null;
            }
            return Long.parseLong(object.toString());
        });
        registerSDPair(long.class, object -> object, object -> {
            if (object == null) {
                return null;
            }
            return Long.parseLong(object.toString());
        });
        registerSDPair(Integer.class, object -> object, object -> {
            if (object == null) {
                return null;
            }
            if (object == "true") {
                return 1;
            }
            if (object == "false") {
                return 0;
            }
            return Integer.parseInt(object.toString());
        });
        registerSDPair(int.class, object -> object, object -> {
            if (object == null) {
                return null;
            }
            return Integer.parseInt(object.toString());
        });
        registerSDPair(Double.class, object -> object, object -> {
            if (object == null) {
                return null;
            }
            return Double.parseDouble(object.toString());
        });
        registerSDPair(double.class, object -> object, object -> {
            if (object == null) {
                return null;
            }
            return Double.parseDouble(object.toString());
        });
        registerSDPair(Float.class, object -> object, object -> {
            if (object == null) {
                return null;
            }
            return Float.parseFloat(object.toString());
        });
        registerSDPair(float.class, object -> object, object -> {
            if (object == null) {
                return null;
            }
            return Float.parseFloat(object.toString());
        });
        registerSDPair(Boolean.class, object -> object, object -> {
            if (object == null) {
                return null;
            }
            return Boolean.parseBoolean(object.toString());
        });
        registerSDPair(boolean.class, object -> object, object -> {
            if (object == null) {
                return null;
            }
            return Boolean.parseBoolean(object.toString());
        });

        //registerDataType(ArrayList.class, "TEXT");
        //registerDataType(List.class, "TEXT");
    }

    /**
     * @param string The string to format
     * @return String appended with " at the beginning and end
     */
    @SuppressWarnings("unused")
    public static String formatString(String string) {
        return "\"" + string + "\"";
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

    @Override
    public String getDataType(Class<?> clazz) {
        String dbDataType = sqlConfig.driver(main).dataTypes.get(clazz);

        if (dbDataType != null) {
            return dbDataType;
        }

        Logger.error("DataType " + clazz.getSimpleName() + " is not a registered data type. Defaulting to TEXT");
        registerDataType(clazz, "TEXT");
        return "TEXT";
    }

    @Override
    public void save(IDatabaseEntry object) {

    }

    public <R> void registerSDPair(Class<R> clazz, ReturnArgLambdaExecutor<?, R> serialize, ReturnArgLambdaExecutor<R, Object> deserialize) {
        DatabaseHelper.registerSDPair(clazz, serialize, deserialize);
    }

    public void registerDataType(Class<?> clazz, String dataType) {
        main.getDriverConfig().registerDataType(clazz, dataType);
    }

}
