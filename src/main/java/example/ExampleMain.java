package example;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.config.SQLConfig;
import dev.lightdream.databasemanager.database.HibernateDatabaseManager;
import dev.lightdream.logger.LoggableMain;
import dev.lightdream.logger.Logger;

import java.io.File;

// If you are using lombok you can skip the getters just add @Getter to the following fields: sqlConfig, driverConfig,
// reflections, databaseManager
@SuppressWarnings("unused")
public class ExampleMain implements DatabaseMain, LoggableMain {

    public static ExampleMain instance;

    private final SQLConfig sqlConfig;

    private final ExampleDatabaseManager databaseManager;

    public ExampleMain() {
        instance = this;
        Logger.init(this);

        // Usually this would be used with FileManager to be loaded from disk as a configuration
        // To see the FileManager go to https://github.com/L1ghtDream/FileManager
        // sqlConfig = FileManager.load(SQLConfig.class);
        sqlConfig = new SQLConfig();

        ClassLoader[] classLoaders = new ClassLoader[]{getClass().getClassLoader()};

        databaseManager = new ExampleDatabaseManager(this);
    }

    @Override
    public File getDataFolder() {
        return new File(System.getProperty("user.dir"));
    }

    @Override
    public SQLConfig getSqlConfig() {
        return sqlConfig;
    }

    @Override
    public HibernateDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public boolean debugToConsole() {
        return true;
    }
}
