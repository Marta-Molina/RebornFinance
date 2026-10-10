package com.example.rebornfinance.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rebornfinance.data.local.converters.RoomConverters
import com.example.rebornfinance.data.local.dao.BudgetDao
import com.example.rebornfinance.data.local.dao.CategoryDao
import com.example.rebornfinance.data.local.dao.EnvelopeDao
import com.example.rebornfinance.data.local.dao.EnvelopeOperationDao
import com.example.rebornfinance.data.local.dao.EyeMaterialDao
import com.example.rebornfinance.data.local.dao.EyePurchaseLotDao
import com.example.rebornfinance.data.local.dao.HairMaterialDao
import com.example.rebornfinance.data.local.dao.HairPurchaseLotDao
import com.example.rebornfinance.data.local.dao.InventoryAuditMovementDao
import com.example.rebornfinance.data.local.dao.MaterialConsumptionRuleDao
import com.example.rebornfinance.data.local.dao.MaterialDao
import com.example.rebornfinance.data.local.dao.MaterialPurchaseLotDao
import com.example.rebornfinance.data.local.dao.MovementDao
import com.example.rebornfinance.data.local.dao.ProjectCostDao
import com.example.rebornfinance.data.local.dao.ProjectEyeAssignmentDao
import com.example.rebornfinance.data.local.dao.ProjectHairConsumptionDao
import com.example.rebornfinance.data.local.dao.ProjectMaterialConsumptionDao
import com.example.rebornfinance.data.local.dao.RebornProjectDao
import com.example.rebornfinance.data.local.dao.SavingsGoalDao
import com.example.rebornfinance.data.local.entity.BudgetEntity
import com.example.rebornfinance.data.local.entity.CategoryEntity
import com.example.rebornfinance.data.local.entity.EnvelopeEntity
import com.example.rebornfinance.data.local.entity.EnvelopeOperationEntity
import com.example.rebornfinance.data.local.entity.EyeMaterialEntity
import com.example.rebornfinance.data.local.entity.EyePurchaseLotEntity
import com.example.rebornfinance.data.local.entity.HairMaterialEntity
import com.example.rebornfinance.data.local.entity.HairPurchaseLotEntity
import com.example.rebornfinance.data.local.entity.InventoryAuditMovementEntity
import com.example.rebornfinance.data.local.entity.MaterialAdjustmentEntity
import com.example.rebornfinance.data.local.entity.MaterialConsumptionRuleEntity
import com.example.rebornfinance.data.local.entity.MaterialEntity
import com.example.rebornfinance.data.local.entity.MaterialPurchaseLotEntity
import com.example.rebornfinance.data.local.entity.MovementEntity
import com.example.rebornfinance.data.local.entity.ProjectCostEntity
import com.example.rebornfinance.data.local.entity.ProjectEyeAssignmentEntity
import com.example.rebornfinance.data.local.entity.ProjectHairConsumptionEntity
import com.example.rebornfinance.data.local.entity.ProjectMaterialConsumptionEntity
import com.example.rebornfinance.data.local.entity.RebornProjectEntity
import com.example.rebornfinance.data.local.entity.SavingsGoalEntity
import com.example.rebornfinance.domain.model.MovementType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MovementEntity::class,
        CategoryEntity::class,
        RebornProjectEntity::class,
        ProjectCostEntity::class,
        MaterialEntity::class,
        MaterialPurchaseLotEntity::class,
        MaterialConsumptionRuleEntity::class,
        ProjectMaterialConsumptionEntity::class,
        MaterialAdjustmentEntity::class,
        EyeMaterialEntity::class,
        EyePurchaseLotEntity::class,
        ProjectEyeAssignmentEntity::class,
        HairMaterialEntity::class,
        HairPurchaseLotEntity::class,
        ProjectHairConsumptionEntity::class,
        InventoryAuditMovementEntity::class,
        EnvelopeEntity::class,
        EnvelopeOperationEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movementDao(): MovementDao
    abstract fun categoryDao(): CategoryDao
    abstract fun rebornProjectDao(): RebornProjectDao
    abstract fun projectCostDao(): ProjectCostDao
    abstract fun materialDao(): MaterialDao
    abstract fun materialPurchaseLotDao(): MaterialPurchaseLotDao
    abstract fun materialConsumptionRuleDao(): MaterialConsumptionRuleDao
    abstract fun projectMaterialConsumptionDao(): ProjectMaterialConsumptionDao
    abstract fun eyeMaterialDao(): EyeMaterialDao
    abstract fun eyePurchaseLotDao(): EyePurchaseLotDao
    abstract fun projectEyeAssignmentDao(): ProjectEyeAssignmentDao
    abstract fun hairMaterialDao(): HairMaterialDao
    abstract fun hairPurchaseLotDao(): HairPurchaseLotDao
    abstract fun projectHairConsumptionDao(): ProjectHairConsumptionDao
    abstract fun inventoryAuditMovementDao(): InventoryAuditMovementDao
    abstract fun envelopeDao(): EnvelopeDao
    abstract fun envelopeOperationDao(): EnvelopeOperationDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `materials` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `brand` TEXT,
                        `unit` TEXT NOT NULL,
                        `totalPurchased` INTEGER NOT NULL,
                        `availableQuantity` INTEGER NOT NULL,
                        `isActive` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `material_purchase_lots` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `materialId` INTEGER NOT NULL,
                        `purchasedQuantity` INTEGER NOT NULL,
                        `remainingQuantity` INTEGER NOT NULL,
                        `paidAmountCents` INTEGER NOT NULL,
                        `purchaseDate` INTEGER NOT NULL,
                        `notes` TEXT,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `material_consumption_rules` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `category` TEXT NOT NULL,
                        `minInches` REAL NOT NULL,
                        `maxInches` REAL NOT NULL,
                        `estimatedConsumption` INTEGER NOT NULL,
                        `unit` TEXT NOT NULL,
                        `isActive` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `project_material_consumptions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `projectId` INTEGER NOT NULL,
                        `materialId` INTEGER,
                        `category` TEXT NOT NULL,
                        `consumedQuantity` INTEGER NOT NULL,
                        `unit` TEXT NOT NULL,
                        `unitCostCents` INTEGER NOT NULL,
                        `assignedCostCents` INTEGER NOT NULL,
                        `isAutomatic` INTEGER NOT NULL,
                        `manualReason` TEXT,
                        `date` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `material_adjustments` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `materialId` INTEGER NOT NULL,
                        `quantityDelta` INTEGER NOT NULL,
                        `reason` TEXT NOT NULL,
                        `date` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `eye_materials` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `color` TEXT NOT NULL,
                        `diameterMm` REAL,
                        `type` TEXT NOT NULL,
                        `modality` TEXT NOT NULL,
                        `brand` TEXT,
                        `notes` TEXT,
                        `isActive` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `eye_purchase_lots` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `eyeMaterialId` INTEGER NOT NULL,
                        `purchasedQuantity` INTEGER NOT NULL,
                        `remainingQuantity` INTEGER NOT NULL,
                        `paidAmountCents` INTEGER NOT NULL,
                        `purchaseDate` INTEGER NOT NULL,
                        `notes` TEXT,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `project_eye_assignments` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `projectId` INTEGER NOT NULL,
                        `eyeMaterialId` INTEGER NOT NULL,
                        `quantity` INTEGER NOT NULL,
                        `assignedCostCents` INTEGER NOT NULL,
                        `isCancelled` INTEGER NOT NULL,
                        `date` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `hair_materials` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `hairType` TEXT NOT NULL,
                        `color` TEXT NOT NULL,
                        `brand` TEXT,
                        `notes` TEXT,
                        `isActive` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `hair_purchase_lots` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `hairMaterialId` INTEGER NOT NULL,
                        `purchasedQuantityGrams` INTEGER NOT NULL,
                        `remainingQuantityGrams` INTEGER NOT NULL,
                        `paidAmountCents` INTEGER NOT NULL,
                        `purchaseDate` INTEGER NOT NULL,
                        `notes` TEXT,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `project_hair_consumptions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `projectId` INTEGER NOT NULL,
                        `hairMaterialId` INTEGER NOT NULL,
                        `consumedQuantityGrams` INTEGER NOT NULL,
                        `unitCostCentsPerGram` INTEGER NOT NULL,
                        `assignedCostCents` INTEGER NOT NULL,
                        `manualReason` TEXT,
                        `isCancelled` INTEGER NOT NULL,
                        `date` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `inventory_audit_movements` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `materialType` TEXT NOT NULL,
                        `materialId` INTEGER NOT NULL,
                        `projectId` INTEGER,
                        `movementType` TEXT NOT NULL,
                        `quantity` INTEGER NOT NULL,
                        `date` INTEGER NOT NULL,
                        `notes` TEXT,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `envelopes` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `description` TEXT,
                        `targetAmountCents` INTEGER,
                        `currentAmountCents` INTEGER NOT NULL,
                        `color` TEXT,
                        `isActive` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `envelope_operations` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `envelopeId` INTEGER NOT NULL,
                        `operationType` TEXT NOT NULL,
                        `targetEnvelopeId` INTEGER,
                        `amountCents` INTEGER NOT NULL,
                        `date` INTEGER NOT NULL,
                        `notes` TEXT,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `budgets` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `category` TEXT NOT NULL,
                        `limitAmountCents` INTEGER NOT NULL,
                        `period` TEXT NOT NULL,
                        `startDate` INTEGER NOT NULL,
                        `endDate` INTEGER NOT NULL,
                        `isActive` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `savings_goals` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `description` TEXT,
                        `targetAmountCents` INTEGER NOT NULL,
                        `currentAmountCents` INTEGER NOT NULL,
                        `linkedEnvelopeId` INTEGER,
                        `targetDate` INTEGER,
                        `status` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                """)
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reborn_projects ADD COLUMN paintingSystem TEXT NOT NULL DEFAULT 'NONE'")
                db.execSQL("""
                    INSERT OR IGNORE INTO material_consumption_rules (category, minInches, maxInches, estimatedConsumption, unit, isActive, createdAt)
                    VALUES 
                    ('Imprimación', 10.0, 26.0, 5000, 'ml', 1, ${System.currentTimeMillis()}),
                    ('Pintura', 15.0, 17.0, 4000, 'ml', 1, ${System.currentTimeMillis()}),
                    ('Pintura', 17.1, 19.0, 5000, 'ml', 1, ${System.currentTimeMillis()}),
                    ('Pintura', 19.1, 21.0, 6000, 'ml', 1, ${System.currentTimeMillis()}),
                    ('Pintura', 21.1, 26.0, 7000, 'ml', 1, ${System.currentTimeMillis()}),
                    ('Barniz', 15.0, 17.0, 1500, 'ml', 1, ${System.currentTimeMillis()}),
                    ('Barniz', 17.1, 19.0, 2000, 'ml', 1, ${System.currentTimeMillis()}),
                    ('Barniz', 19.1, 21.0, 2500, 'ml', 1, ${System.currentTimeMillis()}),
                    ('Barniz', 21.1, 26.0, 3000, 'ml', 1, ${System.currentTimeMillis()})
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
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
                        populateDefaultConsumptionRules(database.materialConsumptionRuleDao())
                    }
                }
            }

            private suspend fun populateInitialCategories(dao: CategoryDao) {
                val initialCategories = listOf(
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
                    CategoryEntity(name = "Venta de reborns", type = MovementType.INCOME),
                    CategoryEntity(name = "Venta de materiales", type = MovementType.INCOME),
                    CategoryEntity(name = "Otros trabajos", type = MovementType.INCOME),
                    CategoryEntity(name = "Otros ingresos", type = MovementType.INCOME),
                    CategoryEntity(name = "Reembolso de compras", type = MovementType.REFUND),
                    CategoryEntity(name = "Otros reembolsos", type = MovementType.REFUND)
                )
                dao.insertCategories(initialCategories)
            }

            private suspend fun populateDefaultConsumptionRules(dao: com.example.rebornfinance.data.local.dao.MaterialConsumptionRuleDao) {
                val now = System.currentTimeMillis()
                val defaultRules = listOf(
                    MaterialConsumptionRuleEntity(category = "Imprimación", minInches = 10.0, maxInches = 26.0, estimatedConsumption = 5000L, unit = "ml", createdAt = now),
                    MaterialConsumptionRuleEntity(category = "Pintura", minInches = 15.0, maxInches = 17.0, estimatedConsumption = 4000L, unit = "ml", createdAt = now),
                    MaterialConsumptionRuleEntity(category = "Pintura", minInches = 17.1, maxInches = 19.0, estimatedConsumption = 5000L, unit = "ml", createdAt = now),
                    MaterialConsumptionRuleEntity(category = "Pintura", minInches = 19.1, maxInches = 21.0, estimatedConsumption = 6000L, unit = "ml", createdAt = now),
                    MaterialConsumptionRuleEntity(category = "Pintura", minInches = 21.1, maxInches = 26.0, estimatedConsumption = 7000L, unit = "ml", createdAt = now),
                    MaterialConsumptionRuleEntity(category = "Barniz", minInches = 15.0, maxInches = 17.0, estimatedConsumption = 1500L, unit = "ml", createdAt = now),
                    MaterialConsumptionRuleEntity(category = "Barniz", minInches = 17.1, maxInches = 19.0, estimatedConsumption = 2000L, unit = "ml", createdAt = now),
                    MaterialConsumptionRuleEntity(category = "Barniz", minInches = 19.1, maxInches = 21.0, estimatedConsumption = 2500L, unit = "ml", createdAt = now),
                    MaterialConsumptionRuleEntity(category = "Barniz", minInches = 21.1, maxInches = 26.0, estimatedConsumption = 3000L, unit = "ml", createdAt = now)
                )
                for (rule in defaultRules) {
                    dao.insertRule(rule)
                }
            }
        }
    }
}
