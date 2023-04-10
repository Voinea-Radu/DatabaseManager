package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.database.HibernateDatabaseManager;
import dev.lightdream.databasemanager.config.SQLConfig;

import java.io.File;

public interface DatabaseMain {

    File getDataFolder();

    SQLConfig getSqlConfig();

    HibernateDatabaseManager getDatabaseManager();
}
