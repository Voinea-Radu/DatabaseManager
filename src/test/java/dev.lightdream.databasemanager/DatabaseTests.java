package dev.lightdream.databasemanager;

import com.google.gson.Gson;
import dev.lightdream.databasemanager.database.HibernateDatabaseManager;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseTests {

    private static TestDatabaseMain main;

    @BeforeAll
    public static void init() {
        deleteTestDatabase();

        main = new TestDatabaseMain();
    }

    @AfterAll
    public static void cleanup() {
        deleteTestDatabase();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteTestDatabase() {
        File file = new File(System.getProperty("user.dir") + "/test.db");
        file.delete();
    }

    @Test
    @Order(1)
    public void createEntry() {
        TestDatabaseEntry entry = new TestDatabaseEntry(
                "test_data_1",
                "test_data_2",
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                Arrays.asList("test_data_3", "test_data_4"),
                Arrays.asList(
                        UUID.fromString("00000000-0000-0000-0000-000000000000"),
                        UUID.fromString("00000000-0000-0000-0000-000000000001")
                ),
                69
        );
        entry.save();

        assertNotEquals(null, entry.id);
    }

    @Test
    @Order(2)
    public void getEntry() {
        List<TestDatabaseEntry> entryList = main.getDatabaseManager().getAll(TestDatabaseEntry.class);

        assertEquals(1, entryList.size());
    }

    @Test
    @Order(3)
    public void checkData() {
        List<TestDatabaseEntry> entryList = main.getDatabaseManager().getAll(TestDatabaseEntry.class);

        assertEquals(1, entryList.size());

        TestDatabaseEntry entry = entryList.get(0);

        assertEquals("test_data_1", entry.data1);
        assertEquals("test_data_2", entry.data2);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), entry.uuid);
        assertEquals("test_data_3", new Gson().fromJson(entry.stringList, List.class).get(0));
        assertEquals("test_data_4", new Gson().fromJson(entry.stringList, List.class).get(1));
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), UUID.fromString((String) new Gson().fromJson(entry.uuidList, List.class).get(0)));
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), UUID.fromString((String) new Gson().fromJson(entry.uuidList, List.class).get(1)));

        assertEquals(69, entry.integer);
    }

    @Test
    @Order(3)
    public void checkSelect() {
        HibernateDatabaseManager.Query<TestDatabaseEntry> query = main.getDatabaseManager().get(TestDatabaseEntry.class);
        query.query.where(
                query.builder.equal(
                        query.root.get("data1"),
                        "test_data_1")
        );

        assertEquals(1, query.execute().size());
    }


    @Test
    @Order(4)
    public void updateEntry() {
        List<TestDatabaseEntry> entryList = main.getDatabaseManager().getAll(TestDatabaseEntry.class);

        assertEquals(1, entryList.size());

        TestDatabaseEntry entry = entryList.get(0);

        entry.data1 = "test_data_1_updated";
        entry.save();

        entryList = main.getDatabaseManager().getAll(TestDatabaseEntry.class);
        assertEquals(1, entryList.size());
        assertEquals("test_data_1_updated", entryList.get(0).data1);
    }

    @Test
    @Order(5)
    public void deleteEntry() {
        List<TestDatabaseEntry> entryList = main.getDatabaseManager().getAll(TestDatabaseEntry.class);

        assertEquals(1, entryList.size());

        TestDatabaseEntry entry = entryList.get(0);
        entry.delete();

        entryList = main.getDatabaseManager().getAll(TestDatabaseEntry.class);
        assertEquals(0, entryList.size());
    }


}
