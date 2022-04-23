package dev.lightdream.databasemanager.dto;

public class DatabaseID<T> {

    public Class<T> clazz;
    public T value;

    @SuppressWarnings("unchecked")
    public DatabaseID(T value) {
        this.clazz = (Class<T>) value.getClass();
        this.value = value;
    }

}
