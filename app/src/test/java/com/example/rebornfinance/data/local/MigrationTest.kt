package com.example.rebornfinance.data.local

import org.junit.Assert.assertNotNull
import org.junit.Test

class MigrationTest {

    @Test
    fun testMigration7to8Exists() {
        val migration = AppDatabase.MIGRATION_7_8
        assertNotNull(migration)
    }
}
