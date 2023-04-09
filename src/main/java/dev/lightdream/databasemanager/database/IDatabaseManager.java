package dev.lightdream.databasemanager.database;

import dev.lightdream.databasemanager.dto.IDatabaseEntry;

public interface IDatabaseManager {

    void connect();

    void createTable(Class<? extends IDatabaseEntry> clazz);

    void setup();

    void setup(Class<? extends IDatabaseEntry> clazz);

    @SuppressWarnings("unused")
    void save();

    void save(IDatabaseEntry object, boolean cache);

    @SuppressWarnings("unused")
    void save(IDatabaseEntry object);

    void delete(IDatabaseEntry entry);

    String getDataType(Class<?> clazz);

}
