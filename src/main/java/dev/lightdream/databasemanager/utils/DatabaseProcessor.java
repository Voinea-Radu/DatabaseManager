package dev.lightdream.databasemanager.utils;

import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.database.IDatabaseManager;
import dev.lightdream.databasemanager.dto.Driver;
import dev.lightdream.databasemanager.dto.IDatabaseEntry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProcessor {
    private final Driver driver;
    private final IDatabaseManager databaseManager;

    public DatabaseProcessor(Driver driver, IDatabaseManager databaseManager) {
        this.driver = driver;
        this.databaseManager = databaseManager;
    }

    public List<String> getAllFields(Class<? extends IDatabaseEntry> clazz) {
        List<String> output = new ArrayList<>();

        for (Field field : clazz.getFields()) {
            if (!field.isAnnotationPresent(DatabaseField.class)) {
                continue;
            }
            DatabaseField databaseField = field.getAnnotation(DatabaseField.class);

            output.add(getFiled(databaseField, field));
        }

        return output;
    }

    public String getFiled(DatabaseField databaseField, Field field) {
        return databaseField.column() + " " +
                databaseManager.getDataType(field.getType()) + " " +
                (databaseField.unique() ? "UNIQUE " : "") +
                (databaseField.autoGenerate() ? driver.getAutoIncrement() : "");
    }
}
