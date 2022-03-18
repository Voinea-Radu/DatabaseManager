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
public class DatabaseManagerImpl extends ProgrammaticHikariDatabaseManager {

    public DatabaseManagerImpl(DatabaseMain main) {
        super(main);
    }

    @SuppressWarnings("InfiniteRecursion")
    @Override
    public void setup() {
        setup(User.class);
    }
}
```

## Example Main
```java
public class Main implements DatabaseMain, LoggableMain {

    public static Main instance;

    private final SQLConfig sqlConfig = new SQLConfig();
    private final DriverConfig driverConfig = new DriverConfig();

    public DatabaseManagerImpl databaseManager;

    public Main() {
        instance = this;

        Debugger.init(this);
        Logger.init(this);
        databaseManager = new DatabaseManagerImpl(this);
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
    public DriverConfig getDriverConfig() {
        return driverConfig;
    }

    @Override
    public IDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public boolean debug() {
        return true;
    }

    @Override
    public void log(String s) {
        System.out.println(s);
    }
}
```

## Example database entry
```java
@DatabaseTable(table = "users")
public class User extends DatabaseEntry {

    @DatabaseField(columnName = "name")
    public String name;
    @DatabaseField(columnName = "money")
    public double money;

    public User(String name, double money) {
        super(Main.instance);
        this.name = name;
        this.money = money;
    }

    public User() {
        super(Main.instance);
    }
}
```

## Versioning
- 1.x - Statically based HikariDatabaseManager
- 2.x - Dynamically based ProgrammaticHikariDatabaseManager 

## Building with Drivers

```xml
<plugins>
    <!-- Other plugins -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.2.4</version>
        <executions>
            <execution>
                <id>no-drivers</id>
                <phase>package</phase>
                <goals>
                    <goal>shade</goal>
                </goals>
                <configuration>
                    <finalName>${project.artifactId}-no-drivers-${project.version}</finalName>
                    <artifactSet>
                        <excludes>
                            <!-- SQLite -->
                            <exclude>org.xerial:sqlite-jdbc</exclude>
    
                            <!-- MYSQL -->
                            <exclude>mysql:mysql-connector-java</exclude>
                        </excludes>
                    </artifactSet>
                </configuration>
            </execution>
            <execution>
                <id>sqlite-drivers</id>
                <phase>package</phase>
                <goals>
                    <goal>shade</goal>
                </goals>
                <configuration>
                    <finalName>${project.artifactId}-sqlite-drivers-${project.version}</finalName>
                    <artifactSet>
                        <excludes>
                            <!-- MYSQL -->
                            <exclude>mysql:mysql-connector-java</exclude>
                        </excludes>
                    </artifactSet>
                </configuration>
            </execution>
            <execution>
                <id>mysql-drivers</id>
                <phase>package</phase>
                <goals>
                    <goal>shade</goal>
                </goals>
                <configuration>
                    <finalName>${project.artifactId}-mysql-drivers-${project.version}</finalName>
                    <artifactSet>
                        <excludes>
                            <!-- SQLite -->
                            <exclude>org.xerial:sqlite-jdbc</exclude>
                        </excludes>
                    </artifactSet>
                </configuration>
            </execution>
            <execution>
                <id>all-drivers</id>
                <phase>package</phase>
                <goals>
                    <goal>shade</goal>
                </goals>
                <configuration>
                    <finalName>${project.artifactId}-all-drivers-${project.version}</finalName>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```