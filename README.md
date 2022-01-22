# LightDream Database Manager
![Build](https://github.com/L1ghtDream/DatabaseManager/actions/workflows/build.yml/badge.svg)
```xml
<repositories>
    <repository>
        <id>lightdream-repo</id>
        <url>https://repo.lightdream.dev/repository/LightDream-API/</url>
    </repository>
    <!-- Other repositories -->
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>DatabaseManager</artifactId>
        <version>VERSION</version>
    </dependency>
    <!-- Other dependencies -->
</dependencies>
```

## Creating a DatabaseManager Implementation

```java
public class DatabaseManagerImp extends HikariDatabaseManager{

    public DatabaseManagerImp(DatabaseMain main) {
        super(main);
    }

    @Override
    public HashMap<Class<?>, LambdaExecutor> getSerializeMap() {
        return null;
    }

    @Override
    public HashMap<Class<?>, LambdaExecutor> getDeserializeMap() {
        return null;
    }

    @Override
    public void setup() {

    }
}
```

## Example Main
```java
public class Example implements DatabaseMain {

    private SQLConfig sqlConfig;
    private DatabaseManagerImp databaseManager;

    public Example() {
        enable();
    }

    public void enable() {
        this.sqlConfig = new SQLConfig();
        this.databaseManager = new DatabaseManagerImp(this);
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
    public IDatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
```


