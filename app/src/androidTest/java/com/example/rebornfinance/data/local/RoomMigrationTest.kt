package com.example.rebornfinance.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate12To13() {
        val db = helper.createDatabase("test-db", 12).apply {
            execSQL("INSERT INTO categories (name, type, isActive) VALUES ('Materiales', 'EXPENSE', 1)")
            execSQL("INSERT INTO categories (name, type, isActive) VALUES ('Venta de reborns', 'INCOME', 1)")
            execSQL("INSERT INTO movements (type, amountCents, description, category, date, createdAt, updatedAt) VALUES ('EXPENSE', 1200, 'Compra ojos', 'Materiales', 1000, 1000, 1000)")
            close()
        }

        val migratedDb = helper.runMigrationsAndValidate("test-db", 13, true, AppDatabase.MIGRATION_12_13)
        
        val cursor = migratedDb.query("SELECT * FROM movements")
        assertTrue(cursor.moveToFirst())
        assertEquals("Materiales", cursor.getString(cursor.getColumnIndexOrThrow("category")))
        assertEquals("EXPENSE", cursor.getString(cursor.getColumnIndexOrThrow("type")))
        assertEquals(1200L, cursor.getLong(cursor.getColumnIndexOrThrow("amountCents")))
        cursor.close()
    }
}
