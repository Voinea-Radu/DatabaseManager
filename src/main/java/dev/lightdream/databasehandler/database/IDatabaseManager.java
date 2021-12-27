package dev.lightdream.databasehandler.database;

import dev.lightdream.databasehandler.dto.DatabaseEntry;

import java.util.List;

public interface IDatabaseManager {

    void connect();

    <T> List<T> getAll(Class<T> clazz);

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
