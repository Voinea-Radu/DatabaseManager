package example;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.database.ProgrammaticDatabaseManager;

public class ExampleDatabaseManager extends ProgrammaticDatabaseManager {
    public ExampleDatabaseManager(DatabaseMain main) {
        super(main);

        // You can register custom serializers using this. By default, for any unknown type it will use GSON to serialize
        // and deserialize
        registerDataType(ExampleDatabaseItem.class, "TEXT");

        registerSDPair(ExampleDatabaseItem.class, item -> item.data1 + "," + item.data2, s -> {
            String[] split = ((String) s).split(",");
            return new ExampleDatabaseItem(split[0], split[1]);
        });
    }

}
