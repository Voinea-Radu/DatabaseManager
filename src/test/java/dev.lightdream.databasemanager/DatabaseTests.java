package dev.lightdream.databasemanager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
        file.delete();
    }

    @Test
    public void testCreateTable() {
        assertDoesNotThrow(() -> {
            main.getDatabaseManager().createTable(TestDatabaseEntry.class);
        });
    }

}
