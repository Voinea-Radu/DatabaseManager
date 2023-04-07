package dev.lightdream.databasemanager.dto.entry.impl;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.dto.entry.DatabaseEntry;

/**
 * Integer ID Database Entry
 */
@SuppressWarnings("unused")
public abstract class IntegerDatabaseEntry extends DatabaseEntry {

    @DatabaseField(columnName = "id",
            autoGenerate = true,
            unique = true,
            primaryKey = true)
    public Integer id;

    public IntegerDatabaseEntry(DatabaseMain main) {
        super(main);
    }

    @Override
    public Integer getID() {
        return id;
    }
}
