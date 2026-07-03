package com.projectpulse.android.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProjectPulseDatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ProjectPulseDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To2_retainsTasksAndAddsPriorityColumn() {
        // Create a version 1 database and insert one task.
        helper.createDatabase(TEST_DB_NAME, 1).use { db ->
            db.execSQL(
                """
                INSERT INTO tasks (projectId, title, isCompleted, createdAt)
                VALUES (1, 'Migrate me', 0, 12345)
                """.trimIndent()
            )
        }

        // Re-open with version 2 and run the migration.
        helper.runMigrationsAndValidate(TEST_DB_NAME, 2, true, MIGRATION_1_2).use { db ->
            db.query("SELECT id, projectId, title, isCompleted, priority, createdAt FROM tasks")
                .use { cursor ->
                    assertEquals(1, cursor.count)
                    cursor.moveToFirst()
                    assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("projectId")))
                    assertEquals("Migrate me", cursor.getString(cursor.getColumnIndexOrThrow("title")))
                    assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")))
                    assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("priority")))
                    assertEquals(12345, cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")))
                }
        }
    }

    companion object {
        private const val TEST_DB_NAME = "migration-test.db"
    }
}
