package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.dto.QueryConstrains;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    @Order(1)
    @Test
    public void createTable() {
        assertDoesNotThrow(() -> main.getDatabaseManager().createTable(TestDatabaseEntry.class));
    }

    @Test
    @Order(2)
    public void equals() {
        String query = new QueryConstrains()
                .equals("data1", "test")
                .getFinalQuery();
        assertEquals("data1=\"test\"", query);
    }

    @Test
    @Order(2)
    public void bigger() {
        String query = new QueryConstrains()
                .bigger("data1", 1)
                .getFinalQuery();
        assertEquals("data1>1.0", query);
    }

    @Test
    @Order(2)
    public void biggerOrEquals() {
        String query = new QueryConstrains()
                .biggerOrEquals("data1", 1)
                .getFinalQuery();
        assertEquals("data1>=1.0", query);
    }

    @Test
    @Order(2)
    public void smaller() {
        String query = new QueryConstrains()
                .smaller("data1", 1)
                .getFinalQuery();
        assertEquals("data1<1.0", query);
    }

    @Test
    @Order(2)
    public void smallerOrEquals() {
        String query = new QueryConstrains()
                .smallerOrEquals("data1", 1)
                .getFinalQuery();
        assertEquals("data1<=1.0", query);
    }

    @Test
    @Order(2)
    public void and() {
        String query = new QueryConstrains()
                .equals("data1", "test1")
                .equals("data2", "test2").getFinalQuery();
        assertEquals("data1=\"test1\" AND data2=\"test2\"", query);
    }

    @Test
    @Order(2)
    public void or() {
        String query = new QueryConstrains().or(
                new QueryConstrains().equals("data1", "test1"),
                new QueryConstrains().equals("data2", "test2")
        ).getFinalQuery();
        assertEquals("data1=\"test1\" OR data2=\"test2\"", query);
    }

    @Test
    @Order(3)
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

        assertNotEquals(0, entry.getID());
    }

    @Test
    @Order(4)
    public void getEntry() {
        List<TestDatabaseEntry> entryList = main.getDatabaseManager().get(TestDatabaseEntry.class).query();

        assertEquals(1, entryList.size());
    }

    @Test
    @Order(5)
    public void checkData() {
        List<TestDatabaseEntry> entryList = main.getDatabaseManager().get(TestDatabaseEntry.class).query();

        assertEquals(1, entryList.size());

        TestDatabaseEntry entry = entryList.get(0);

        assertEquals("test_data_1", entry.data1);
        assertEquals("test_data_2", entry.data2);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), entry.uuid);
        assertEquals(Arrays.asList("test_data_3", "test_data_4"), entry.stringList);
        assertEquals(Arrays.asList(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                UUID.fromString("00000000-0000-0000-0000-000000000001")
        ), entry.uuidList);
        assertEquals(69, entry.integer);
    }


    @Test
    @Order(6)
    public void updateEntry() {
        List<TestDatabaseEntry> entryList = main.getDatabaseManager().get(TestDatabaseEntry.class).query();

        assertEquals(1, entryList.size());

        TestDatabaseEntry entry = entryList.get(0);

        entry.data1 = "test_data_1_updated";
        entry.save();

        entryList = main.getDatabaseManager().get(TestDatabaseEntry.class).query();
        assertEquals(1, entryList.size());
        assertEquals("test_data_1_updated", entryList.get(0).data1);
    }

    @Test
    @Order(7)
    public void deleteEntry() {
        List<TestDatabaseEntry> entryList = main.getDatabaseManager().get(TestDatabaseEntry.class).query();

        assertEquals(1, entryList.size());

        TestDatabaseEntry entry = entryList.get(0);
        entry.delete();

        entryList = main.getDatabaseManager().get(TestDatabaseEntry.class).query();
        assertEquals(0, entryList.size());
    }


}
