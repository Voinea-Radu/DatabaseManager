package example;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.database.HibernateDatabaseManager;

import java.util.List;

public class ExampleDatabaseManager extends HibernateDatabaseManager {
    public ExampleDatabaseManager(DatabaseMain main) {
        super(main);
    }

    @Override
    protected List<Class<?>> getClasses() {
        return List.of(ExampleDatabaseItem.class);
    }

}
