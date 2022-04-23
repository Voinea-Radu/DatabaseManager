package dev.lightdream.databasemanager.database;

import dev.lightdream.databasemanager.dto.IDatabaseEntry;

public interface IDatabaseManager {

    void connect();

    void createTable(Class<? extends IDatabaseEntry> clazz);

    void setup();

    @SuppressWarnings("unused")
    void setup(Class<? extends IDatabaseEntry> clazz);

    @SuppressWarnings("unused")
    void save();

    @SuppressWarnings("unused")
    void save(IDatabaseEntry object, boolean cache);

    @SuppressWarnings("unused")
    void save(IDatabaseEntry object);

    @SuppressWarnings("unused")
    void delete(IDatabaseEntry entry);

}
