package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.database.IDatabaseManager;
import dev.lightdream.databasemanager.dto.DriverConfig;
import dev.lightdream.databasemanager.dto.SQLConfig;
import dev.lightdream.lambda.LambdaExecutor;
import dev.lightdream.logger.LoggableMain;

import java.io.File;

public interface DatabaseMain {

    @SuppressWarnings({"unused", "StringConcatenationInLoop"})
    static String getVersion(int tabs) {
        String output = "DatabaseManager 2.6.38";

        String prepend = "";

        for (int i = 0; i < tabs; ++i) {
            prepend += "    ";
        }

        output = output + prepend + "    -> " + LoggableMain.getVersion() + "\n";
        output = output + prepend + "    -> " + LambdaExecutor.getVersion() + "\n";
        return output;
    }

    File getDataFolder();

    SQLConfig getSqlConfig();

    DriverConfig getDriverConfig();

    IDatabaseManager getDatabaseManager();

}
