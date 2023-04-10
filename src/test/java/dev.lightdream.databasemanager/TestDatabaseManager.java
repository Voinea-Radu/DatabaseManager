package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.database.HibernateDatabaseManager;

import java.util.List;

public class TestDatabaseManager extends HibernateDatabaseManager {
    public TestDatabaseManager(DatabaseMain main) {
        super(main);
    }

    @Override
    protected List<Class<?>> getClasses() {
        return List.of(TestDatabaseEntry.class);
    }

}