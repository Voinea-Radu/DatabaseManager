package dev.lightdream.databasemanager.dto;

import dev.lightdream.databasemanager.DatabaseMain;

public abstract class DatabaseEntry {

    private final DatabaseMain main;

    public DatabaseEntry(DatabaseMain main) {
        this.main = main;
    }

    public abstract Object getID();

    public void save(){
        main.getDatabaseManager().save(this);
    }

    public void delete(){
        main.getDatabaseManager().delete(this);
    }


}
