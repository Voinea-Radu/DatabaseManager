package dev.lightdream.databasemanager.dto;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class DatabaseEntry implements IDatabaseEntry {

    @DatabaseField(columnName = "id",
            autoGenerate = true,
            unique = true,
            primaryKey = true)
    public Integer id;
    private DatabaseMain main;

    public DatabaseEntry(DatabaseMain main) {
        this.main = main;
    }

    @SuppressWarnings("unused")
    public void save() {
        save(true);
    }

    public void save(boolean cache) {
        this.main.getDatabaseManager()
                .save(this, cache);
    }

    @SuppressWarnings("unused")
    public void delete() {
        main.getDatabaseManager()
                .delete(this);
    }

    public void setMain(DatabaseMain main) {
        this.main = main;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseEntry that = (DatabaseEntry) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Integer getID() {
        return id;
    }
}
