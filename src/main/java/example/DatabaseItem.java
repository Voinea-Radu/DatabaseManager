package example;

import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.dto.entry.impl.IntegerDatabaseEntry;

@DatabaseTable(table = "database_item")
public class DatabaseItem extends IntegerDatabaseEntry {

    @DatabaseField(column = "data1")
    public String data1;
    @DatabaseField(column = "data2")
    public String data2;

    public DatabaseItem(String data1, String data2) {
        super(ExampleMain.instance);
        this.data1 = data1;
        this.data2 = data2;
    }

    public DatabaseItem() {
        super(ExampleMain.instance);
    }


}
