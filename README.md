# DatabaseManager

![Build](../../actions/workflows/build.yml/badge.svg)
![Version](https://img.shields.io/badge/Version-5.0.7-red.svg)

# Table Of Contents

1. [Description](#description)
2. [How to add to your project](#how-to-add-to-your-project)
3. [How to use](#how-to-use)

## Description

A database manager lib that is based on Hibernate and makes the setup of it more straight forward. 


## How to add to your project

The artifact can be found at the repository https://repo.lightdream.dev or https://jitpack.io (under
com.github.L1ghtDream instead of dev.lightdream)

### Maven

```xml

<repositories>
    <repository>
        <id>lightdream-repo</id>
        <url>https://repo.lightdream.dev/</url>
    </repository>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml

<dependencies>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>database-manager</artifactId>
        <version>5.0.7</version>
    </dependency>
    <dependency>
        <groupId>com.github.L1ghtDream</groupId>
        <artifactId>database-manager</artifactId>
        <version>5.0.7</version>
    </dependency>
</dependencies>
```

### Gradle - Groovy DSL

```groovy
repositories {
    maven { url "https://repo.lightdream.dev/" }
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "dev.lightdream:database-manager:5.0.7"
    implementation "com.github.L1ghtDream:database-manager:5.0.7"
}
```

### Gradle - Kotlin DSL

```kotlin
repositories {
    maven("https://repo.lightdream.dev/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("dev.lightdream:database-manager:5.0.7")
    implementation("com.github.L1ghtDream:database-manager:5.0.7")
}
```

If you want to use an older version that is not available in https://repo.lightdream.dev you can try
using https://archive-repo.lightdream.dev

## How to use

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
