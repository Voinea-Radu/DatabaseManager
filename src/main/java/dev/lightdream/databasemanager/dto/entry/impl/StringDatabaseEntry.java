package dev.lightdream.databasemanager.dto.entry.impl;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.dto.IDatabaseEntry;
import dev.lightdream.databasemanager.dto.entry.DatabaseEntry;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class StringDatabaseEntry extends DatabaseEntry {

    @DatabaseField(
            columnName = "id",
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
