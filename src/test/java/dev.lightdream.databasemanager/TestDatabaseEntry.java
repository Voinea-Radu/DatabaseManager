package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.dto.entry.impl.IntegerDatabaseEntry;
import example.ExampleMain;

@DatabaseTable(name = "database_item")
public class TestDatabaseEntry extends IntegerDatabaseEntry {

    @DatabaseField(column = "data1")
    public String data1;
    @DatabaseField(column = "data2")
    public String data2;

    public TestDatabaseEntry(String data1, String data2) {
        super(ExampleMain.instance);
        this.data1 = data1;
        this.data2 = data2;
    }

    public TestDatabaseEntry() {
        super(ExampleMain.instance);
    }


}
