package dev.lightdream.databasemanager.database;

import dev.lightdream.databasemanager.DatabaseMain;

public abstract class ProgrammaticDatabaseManager extends ProgrammaticHikariDatabaseManager {
    public ProgrammaticDatabaseManager(DatabaseMain main) {
        super(main);
    }
}
