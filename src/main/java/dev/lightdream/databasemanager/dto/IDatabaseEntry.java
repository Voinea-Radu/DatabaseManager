package dev.lightdream.databasemanager.dto;

import dev.lightdream.databasemanager.DatabaseMain;

public interface IDatabaseEntry {

    @SuppressWarnings("unused")
    void save();

    void save(boolean cache);

    @SuppressWarnings("unused")
    void delete();

    void setMain(DatabaseMain main);

    Object getID();

}
