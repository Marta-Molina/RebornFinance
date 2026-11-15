package com.example.rebornfinance.data.local

import org.junit.Assert.assertNotNull
import org.junit.Test

class MigrationTest {

    @Test
    fun testMigration12to13Exists() {
        val migration = AppDatabase.MIGRATION_12_13
        assertNotNull(migration)
    }

    @Test
    fun testMigration13to14Exists() {
        val migration = AppDatabase.MIGRATION_13_14
        assertNotNull(migration)
    }
}
