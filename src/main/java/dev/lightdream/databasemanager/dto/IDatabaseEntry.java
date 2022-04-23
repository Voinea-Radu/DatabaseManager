package dev.lightdream.databasemanager.dto;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;

import java.util.Objects;

public interface IDatabaseEntry {

    @SuppressWarnings("unused")
    void save();

    void save(boolean cache);

    @SuppressWarnings("unused")
    void delete();

    void setMain(DatabaseMain main);

    Object getID();

}
