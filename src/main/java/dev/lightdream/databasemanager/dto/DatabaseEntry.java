package dev.lightdream.databasemanager.dto;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;

public abstract class DatabaseEntry {

    @DatabaseField(columnName = "id", autoGenerate = true, unique = true, primaryKey = true)
    public int id;
    private DatabaseMain main;

    public DatabaseEntry(DatabaseMain main) {
        this.main = main;
    }

    @SuppressWarnings("unused")
    public void save() {
        save(true);
    }

    public void save(boolean cache) {
        this.main.getDatabaseManager().save(this, cache);
    }

    @SuppressWarnings("unused")
    public void delete() {
        main.getDatabaseManager().delete(this);
    }

    public void setMain(DatabaseMain main) {
        this.main = main;
    }

}
