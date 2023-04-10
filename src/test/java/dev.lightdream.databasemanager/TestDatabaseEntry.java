package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.dto.entry.impl.IntegerDatabaseEntry;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@DatabaseTable(name = "database_item")
public class TestDatabaseEntry extends IntegerDatabaseEntry {

    @DatabaseField(column = "data1")
    public String data1;
    @DatabaseField(column = "data2")
    public String data2;
    @DatabaseField(column = "uuid")
    public UUID uuid;
    @DatabaseField(column = "string_list")
    public List<String> stringList;
    @DatabaseField(column = "uuid_list")
    public List<UUID> uuidList;
    @DatabaseField(column = "integer")
    public Integer integer;

    public TestDatabaseEntry(String data1, String data2, UUID uuid, List<String> stringList, List<UUID> uuidList, Integer integer) {
        super(TestDatabaseMain.instance);
        this.data1 = data1;
        this.data2 = data2;
        this.uuid = uuid;
        this.stringList = stringList;
        this.uuidList = uuidList;
        this.integer = integer;
    }

    public TestDatabaseEntry() {
        super(TestDatabaseMain.instance);
    }


}
