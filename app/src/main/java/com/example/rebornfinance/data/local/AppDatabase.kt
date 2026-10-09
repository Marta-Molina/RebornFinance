package com.example.rebornfinance.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rebornfinance.data.local.converters.RoomConverters
import com.example.rebornfinance.data.local.dao.CategoryDao
import com.example.rebornfinance.data.local.dao.MovementDao
import com.example.rebornfinance.data.local.entity.CategoryEntity
import com.example.rebornfinance.data.local.entity.MovementEntity
import com.example.rebornfinance.domain.model.MovementType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [MovementEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movementDao(): MovementDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reborn_finance_db"
                )
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
