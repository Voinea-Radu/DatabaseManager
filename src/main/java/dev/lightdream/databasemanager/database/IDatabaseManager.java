package dev.lightdream.databasemanager.database;

import dev.lightdream.databasemanager.dto.DatabaseEntry;

public interface IDatabaseManager {

    void connect();

    void createTable(Class<?> clazz);

    void setup();

    @SuppressWarnings("unused")
    void setup(Class<?> clazz);

    @SuppressWarnings("unused")
    void save();

    @SuppressWarnings("unused")
    void save(DatabaseEntry object, boolean cache);

    @SuppressWarnings("unused")
    void save(DatabaseEntry object);

    @SuppressWarnings("unused")
    void delete(DatabaseEntry entry);

}
