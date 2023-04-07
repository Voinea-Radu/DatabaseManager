package dev.lightdream.databasemanager.dto.entry.impl;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.dto.entry.DatabaseEntry;

/**
 * String ID Database Entry
 */
@SuppressWarnings("unused")
public abstract class StringDatabaseEntry extends DatabaseEntry {

    @DatabaseField(
            column = "id",
            unique = true,
            primaryKey = true
    )
    public String id;

    public StringDatabaseEntry(DatabaseMain main) {
        super(main);
    }

    @Override
    public String getID() {
        return id;
    }
}
