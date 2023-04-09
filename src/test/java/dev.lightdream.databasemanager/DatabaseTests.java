package dev.lightdream.databasemanager;

import dev.lightdream.databasemanager.dto.QueryConstrains;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseTests {

    private static TestDatabaseMain main;

    @BeforeAll
    public static void init() {
        main = new TestDatabaseMain();
    }

    @AfterAll
    public static void cleanup() {
        // Delete the test database
        File file = new File(main.getDataFolder().getAbsolutePath() + "/test.db");
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

    @Order(1)
    @Test
    public void testCreateTable() {
        assertDoesNotThrow(() -> main.getDatabaseManager().createTable(TestDatabaseEntry.class));
    }

    @Test
    @Order(2)
    public void testAndQuery(){
        String query = new QueryConstrains()
                .equals("data1", "test1")
                .equals("data2", "test2").getFinalQuery();
        assertEquals("data1=\"test1\" AND data2=\"test2\"", query);
    }

    @Test
    @Order(3)
    public void testOrQuery(){
        String query = new QueryConstrains().or(
                        new QueryConstrains().equals("data1", "test1"),
                        new QueryConstrains().equals("data2", "test2")
                ).getFinalQuery();
        assertEquals("data1=\"test1\" OR data2=\"test2\"", query);
    }

}
