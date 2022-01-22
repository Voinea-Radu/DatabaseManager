package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.database.IDatabaseManager;
import dev.lightdream.databasemanager.dto.SQLConfig;

import java.io.File;

public interface DatabaseMain {

    File getDataFolder();

    SQLConfig getSqlConfig();

    IDatabaseManager getDatabaseManager();


}
