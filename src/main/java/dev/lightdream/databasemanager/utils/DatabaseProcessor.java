package dev.lightdream.databasemanager.utils;

import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.database.DatabaseHelper;
import dev.lightdream.databasemanager.database.IDatabaseManager;
import dev.lightdream.databasemanager.dto.Driver;
import dev.lightdream.databasemanager.dto.IDatabaseEntry;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

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

    public List<String> getFieldsWithProperty(Class<? extends IDatabaseEntry> clazz) {
        List<String> output = new ArrayList<>();

        for (Pair<Field, DatabaseField> pair : getDatabaseFields(clazz)) {
            output.add(getFiledWithProperty(pair.getSecond(), pair.getFirst()));
        }

        return output;
    }

    public String getFiledWithProperty(DatabaseField databaseField, Field field) {
        return databaseField.column() + " " +
                databaseManager.getDataType(field.getType()) + " " +
                (databaseField.unique() ? "UNIQUE " : "") +
                (databaseField.autoGenerate() ? driver.getAutoIncrement() : "");
    }

    public List<String> getFields(Class<? extends IDatabaseEntry> clazz) {
        List<String> output = new ArrayList<>();

        for (Pair<Field, DatabaseField> pair : getDatabaseFields(clazz)) {
            output.add(pair.getSecond().column());
        }

        return output;
    }

    @SneakyThrows
    public List<String> getValuesParsed(IDatabaseEntry entry) {
        List<String> output = new ArrayList<>();

        for (Pair<Field, DatabaseField> pair : getDatabaseFields(entry.getClass())) {
            output.add(DatabaseHelper.formatQueryArgument(pair.getFirst().get(entry)));
        }

        return output;
    }

    @SneakyThrows
    public List<Object> getValues(IDatabaseEntry entry) {
        List<Object> output = new ArrayList<>();

        for (Pair<Field, DatabaseField> pair : getDatabaseFields(entry.getClass())) {
            output.add(pair.getFirst().get(entry));
        }

        return output;
    }


    public List<Pair<Field, DatabaseField>> getDatabaseFields(Class<?> clazz) {
        List<Pair<Field, DatabaseField>> output = new ArrayList<>();

        for (Field field : clazz.getFields()) {
            if (!field.isAnnotationPresent(DatabaseField.class)) {
                continue;
            }
            DatabaseField databaseField = field.getAnnotation(DatabaseField.class);

            output.add(new Pair<>(field, databaseField));
        }

        return output;
    }

    public @Nullable String getKey(Class<? extends IDatabaseEntry> clazz) {
        for (Pair<Field, DatabaseField> pair : getDatabaseFields(clazz)) {
            if (pair.getSecond().primaryKey() && pair.getSecond().autoGenerate()) {
                return pair.getSecond().column();
            }
        }

        return null;
    }

}
