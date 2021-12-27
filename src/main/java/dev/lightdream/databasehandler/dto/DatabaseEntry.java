package dev.lightdream.databasehandler.dto;

import dev.lightdream.databasehandler.DatabaseMain;
import dev.lightdream.databasehandler.annotations.database.DatabaseField;
import dev.lightdream.logger.Debugger;

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
