package com.example.rebornfinance.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rebornfinance.data.local.converters.RoomConverters
import com.example.rebornfinance.data.local.dao.CategoryDao
import com.example.rebornfinance.data.local.dao.MovementDao
import com.example.rebornfinance.data.local.dao.ProjectCostDao
import com.example.rebornfinance.data.local.dao.RebornProjectDao
import com.example.rebornfinance.data.local.entity.CategoryEntity
import com.example.rebornfinance.data.local.entity.MovementEntity
import com.example.rebornfinance.data.local.entity.ProjectCostEntity
import com.example.rebornfinance.data.local.entity.RebornProjectEntity
import com.example.rebornfinance.domain.model.MovementType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MovementEntity::class,
        CategoryEntity::class,
        RebornProjectEntity::class,
        ProjectCostEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movementDao(): MovementDao
    abstract fun categoryDao(): CategoryDao
    abstract fun rebornProjectDao(): RebornProjectDao
    abstract fun projectCostDao(): ProjectCostDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `reborn_projects` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `kitName` TEXT NOT NULL,
                        `sculptorName` TEXT NOT NULL,
                        `sizeInches` REAL NOT NULL,
                        `startDate` INTEGER NOT NULL,
                        `status` TEXT NOT NULL,
                        `predictedSalePriceCents` INTEGER,
                        `actualSalePriceCents` INTEGER,
                        `saleDate` INTEGER,
                        `notes` TEXT,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `project_costs` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `projectId` INTEGER NOT NULL,
                        `concept` TEXT NOT NULL,
                        `amountCents` INTEGER NOT NULL,
                        `date` INTEGER NOT NULL,
                        `notes` TEXT,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reborn_finance_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialCategories(database.categoryDao())
                    }
                }
            }

            private suspend fun populateInitialCategories(dao: CategoryDao) {
                val initialCategories = listOf(
                    // Gastos
                    CategoryEntity(name = "Materiales", type = MovementType.EXPENSE),
                    CategoryEntity(name = "Pinturas y barnices", type = MovementType.EXPENSE),
                    CategoryEntity(name = "Ojos", type = MovementType.EXPENSE),
                    CategoryEntity(name = "Pelo", type = MovementType.EXPENSE),
                    CategoryEntity(name = "Kits", type = MovementType.EXPENSE),
                    CategoryEntity(name = "Herramientas", type = MovementType.EXPENSE),
                    CategoryEntity(name = "Envíos", type = MovementType.EXPENSE),
                    CategoryEntity(name = "Embalaje", type = MovementType.EXPENSE),
                    CategoryEntity(name = "Comisiones", type = MovementType.EXPENSE),
                    CategoryEntity(name = "Gastos personales", type = MovementType.EXPENSE),
                    CategoryEntity(name = "Otros", type = MovementType.EXPENSE),
                    // Ingresos
                    CategoryEntity(name = "Venta de reborns", type = MovementType.INCOME),
                    CategoryEntity(name = "Venta de materiales", type = MovementType.INCOME),
                    CategoryEntity(name = "Otros trabajos", type = MovementType.INCOME),
                    CategoryEntity(name = "Otros ingresos", type = MovementType.INCOME),
                    // Reembolsos
                    CategoryEntity(name = "Reembolso de compras", type = MovementType.REFUND),
                    CategoryEntity(name = "Otros reembolsos", type = MovementType.REFUND)
                )
                dao.insertCategories(initialCategories)
            }
        }
    }
}
