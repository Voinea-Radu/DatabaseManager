package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.database.IDatabaseManager;
import dev.lightdream.databasemanager.config.DriverConfig;
import dev.lightdream.databasemanager.config.SQLConfig;
import org.reflections.Reflections;

import java.io.File;

public interface DatabaseMain {

    File getDataFolder();

    SQLConfig getSqlConfig();

    DriverConfig getDriverConfig();

    IDatabaseManager getDatabaseManager();

    Reflections getReflections();

}
