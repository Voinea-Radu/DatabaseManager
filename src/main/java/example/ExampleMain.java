package example;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.database.IDatabaseManager;
import dev.lightdream.databasemanager.dto.DriverConfig;
import dev.lightdream.databasemanager.dto.SQLConfig;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;

// If you are using lombok you can skip the getters just add @Getter to the following fields: sqlConfig, driverConfig,
// reflections, databaseManager
@SuppressWarnings("unused")
public class ExampleMain implements DatabaseMain {

    private final SQLConfig sqlConfig;
    private final DriverConfig driverConfig;

    private final Reflections reflections;

    private final ExampleDatabaseManager databaseManager;

    public ExampleMain() {
        // Usually this would be used with FileManager to be loaded from disk as a configuration
        // To see the FileManager go to https://github.com/L1ghtDream/FileManager
        // sqlConfig = FileManager.load(SQLConfig.class);
        sqlConfig = new SQLConfig();

        driverConfig = new DriverConfig();
        // This allows you to change and drivers if having any issues with the syntax in any of them.
        // Please open a PR if you find anything wrong with the drivers.
        // driverConfig.MYSQL.createTable = "";


        ClassLoader[] classLoaders = new ClassLoader[]{getClass().getClassLoader()};
        reflections = new Reflections(new ConfigurationBuilder()
                .setClassLoaders(classLoaders)
                .forPackages("example")
        );

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
    public DriverConfig getDriverConfig() {
        return driverConfig;
    }

    @Override
    public IDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public Reflections getReflections() {
        return reflections;
    }
}
