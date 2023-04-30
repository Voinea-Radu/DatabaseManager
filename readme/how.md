### Creating the main
```java
@SuppressWarnings("unused")
public class ExampleMain implements DatabaseMain, LoggableMain {

    public static ExampleMain instance;

    private final SQLConfig sqlConfig;

    private final ExampleDatabaseManager databaseManager;

    public ExampleMain() {
        instance = this;
        Logger.init(this);

        // Usually this would be used with FileManager to be loaded from disk as a configuration
        // To see the FileManager go to https://github.com/L1ghtDream/FileManager
        // sqlConfig = FileManager.load(SQLConfig.class);
        sqlConfig = new SQLConfig();

        ClassLoader[] classLoaders = new ClassLoader[]{getClass().getClassLoader()};

        databaseManager = new ExampleDatabaseManager(this);
    }

    @Override
    public File getDataFolder() {
        return new File(System.getProperty("user.dir"));
    }

    @Override
    public SQLConfig getSqlConfig() {
        return sqlConfig;
    }

    @Override
    public HibernateDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public boolean debugToConsole() {
        return true;
    }
}
```

### DatabaseManager instance

```java
public class ExampleDatabaseManager extends HibernateDatabaseManager {
    public ExampleDatabaseManager(DatabaseMain main) {
        super(main);
    }

    @Override
    protected List<Class<?>> getClasses() {
        return List.of(ExampleDatabaseItem.class);
    }

}

```

### Database entry

```java
@Entity
@Table(name = "example_table",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"})
        }
)
public class ExampleDatabaseItem extends DatabaseEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, length = 11)
    public Integer id;
    @Column(name = "data_1")
    public String data1;
    @Column(name = "data_2")
    public String data2;

    public ExampleDatabaseItem(String data1, String data2) {
        super(ExampleMain.instance);
        this.data1 = data1;
        this.data2 = data2;
    }

    public ExampleDatabaseItem() {
        super(ExampleMain.instance);
    }

    @Override
    public Object getID() {
        return id;
    }
}
```