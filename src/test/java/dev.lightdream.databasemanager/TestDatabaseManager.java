package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.database.ProgrammaticDatabaseManager;

public class TestDatabaseManager extends ProgrammaticDatabaseManager {
    public TestDatabaseManager(DatabaseMain main) {
        super(main);
    }

    // Disable automatic table creation
    @Override
    public void setup() {

    }
}