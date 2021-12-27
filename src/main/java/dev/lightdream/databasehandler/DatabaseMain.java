package dev.lightdream.databasehandler;

import dev.lightdream.databasehandler.database.IDatabaseManager;
import dev.lightdream.databasehandler.dto.SQLConfig;

import java.io.File;

public interface DatabaseMain {

    File getDataFolder();

    SQLConfig getSqlConfig();

    IDatabaseManager getDatabaseManager();


}
