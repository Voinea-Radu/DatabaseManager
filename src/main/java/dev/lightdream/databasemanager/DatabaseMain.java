package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.database.IDatabaseManager;
import dev.lightdream.databasemanager.dto.DriverConfig;
import dev.lightdream.databasemanager.dto.SQLConfig;
import dev.lightdream.lambda.LambdaExecutor;
import dev.lightdream.logger.LoggableMain;

import javax.xml.crypto.Data;
import java.io.File;

public interface DatabaseMain {

    @SuppressWarnings("unused")
    static String getVersion() {
        return "DatabaseManager " + DatabaseMain.class.getPackage().getImplementationVersion() +
                "    -> " + LoggableMain.getVersion()+
                "    -> " + LambdaExecutor.getVersion();
    }

    File getDataFolder();

    SQLConfig getSqlConfig();

    DriverConfig getDriverConfig();

    IDatabaseManager getDatabaseManager();

}
