package com.example.rebornfinance.feature.movements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.core.money.MoneyUtils
import com.example.rebornfinance.core.validation.QuantityParser
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.local.entity.EyeMaterialEntity
import com.example.rebornfinance.data.local.entity.EyePurchaseLotEntity
import com.example.rebornfinance.data.local.entity.HairMaterialEntity
import com.example.rebornfinance.data.local.entity.HairPurchaseLotEntity
import com.example.rebornfinance.data.local.entity.MaterialEntity
import com.example.rebornfinance.data.local.entity.MaterialPurchaseLotEntity
import com.example.rebornfinance.data.repository.CategoryRepositoryImpl
import com.example.rebornfinance.data.repository.MovementRepositoryImpl
import com.example.rebornfinance.domain.model.Category
import com.example.rebornfinance.domain.model.EyeModality
import com.example.rebornfinance.domain.model.InventoryCatalog
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovementFormViewModel(application: Application, private val movementId: Long?) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val movementRepository = MovementRepositoryImpl(
        database.movementDao(),
        database,
        database.materialDao(),
        database.materialPurchaseLotDao(),
        database.eyePurchaseLotDao(),
        database.hairPurchaseLotDao()
    )
    private val categoryRepository = CategoryRepositoryImpl(database.categoryDao())

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var type = MutableStateFlow(MovementType.EXPENSE)
    var amountStr = MutableStateFlow("")
    var description = MutableStateFlow("")
    var category = MutableStateFlow("") // Financial category
    var dateMillis = MutableStateFlow(DateUtils.getCurrentTimestamp())
    var notes = MutableStateFlow("")

    // Inventory purchase specific fields
    var registerInInventory = MutableStateFlow(true)
    var inventoryFamily = MutableStateFlow("Kits y esculturas")
    var inventorySubCategory = MutableStateFlow<String?>("Edición limitada")
    var needleVariant = MutableStateFlow<String?>(null)
    var eyeModality = MutableStateFlow(EyeModality.PAIRS)
    var selectedUnit = MutableStateFlow("unidades")
    var quantityStr = MutableStateFlow("1")

    // Edit mode state
    var isEditMode = movementId != null && movementId > 0L
    var isLinkedToInventory = MutableStateFlow(false)
    var isLotPartiallyConsumed = MutableStateFlow(false)
    private var createdAtOriginal = DateUtils.getCurrentTimestamp()

    init {
        if (isEditMode) {
            viewModelScope.launch {
                movementRepository.getMovementById(movementId!!)?.let { mov ->
                    type.value = mov.type
                    amountStr.value = MoneyUtils.formatCents(mov.amountCents).replace(" €", "").trim()
                    description.value = mov.description
                    category.value = mov.category
                    dateMillis.value = mov.date
                    notes.value = mov.notes ?: ""
                    createdAtOriginal = mov.createdAt

                    // Check if linked to inventory lots
                    val gLots = database.materialPurchaseLotDao().getLotsByMovementId(movementId)
                    val eLots = database.eyePurchaseLotDao().getLotsByMovementId(movementId)
                    val hLots = database.hairPurchaseLotDao().getLotsByMovementId(movementId)

                    if (gLots.isNotEmpty()) {
                        isLinkedToInventory.value = true
                        val lot = gLots.first()
                        isLotPartiallyConsumed.value = lot.remainingQuantity < lot.purchasedQuantity
                        database.materialDao().getMaterialById(lot.materialId)?.let { mat ->
                            inventoryFamily.value = mat.category
                            inventorySubCategory.value = mat.subCategory
                            selectedUnit.value = mat.unit
                            quantityStr.value = QuantityParser.formatThousandths(lot.purchasedQuantity)
                        }
                    } else if (eLots.isNotEmpty()) {
                        isLinkedToInventory.value = true
                        val lot = eLots.first()
                        isLotPartiallyConsumed.value = lot.remainingQuantity < lot.purchasedQuantity
                        database.eyeMaterialDao().getEyeMaterialById(lot.eyeMaterialId)?.let { eye ->
                            inventoryFamily.value = "Ojos"
                            inventorySubCategory.value = eye.subCategory ?: eye.type
                            eyeModality.value = eye.modality
                            selectedUnit.value = if (eye.modality == EyeModality.PAIRS) "pares" else "unidades"
                            quantityStr.value = lot.purchasedQuantity.toString()
                        }
                    } else if (hLots.isNotEmpty()) {
                        isLinkedToInventory.value = true
                        val lot = hLots.first()
                        isLotPartiallyConsumed.value = lot.remainingQuantityGrams < lot.purchasedQuantityGrams
                        database.hairMaterialDao().getHairMaterialById(lot.hairMaterialId)?.let { hair ->
                            inventoryFamily.value = "Pelo y cabello"
                            inventorySubCategory.value = hair.subCategory ?: hair.hairType
                            selectedUnit.value = "g"
                            quantityStr.value = QuantityParser.formatThousandths(lot.purchasedQuantityGrams)
                        }
                    } else {
                        registerInInventory.value = false
                    }
                }
            }
        }
    }

    fun onFamilySelected(family: String) {
        inventoryFamily.value = family
        val subs = InventoryCatalog.masterCatalog[family] ?: emptyList()
        inventorySubCategory.value = subs.firstOrNull()
        val units = InventoryCatalog.validUnitsPerFamily[family] ?: listOf("unidades")
        selectedUnit.value = units.firstOrNull() ?: "unidades"

        if (family == "Agujas y accesorios de injerto") {
            needleVariant.value = InventoryCatalog.needleSizes.firstOrNull()
        } else {
            needleVariant.value = null
        }

        // Auto suggest matching financial category
        val suggestedCategory = when (family) {
            "Kits y esculturas" -> "Kits"
            "Ojos" -> "Ojos"
            "Pelo y cabello" -> "Pelo"
            "Pinturas y pigmentos", "Barnices y acabados" -> "Pinturas y barnices"
            "Herramientas reutilizables", "Agujas y accesorios de injerto" -> "Herramientas"
            "Embalaje y envíos" -> "Embalaje"
            else -> "Materiales"
        }
        category.value = suggestedCategory
    }

    fun onSubCategorySelected(sub: String) {
        inventorySubCategory.value = sub
        if (inventoryFamily.value == "Agujas y accesorios de injerto" && sub == "Agujas") {
            needleVariant.value = InventoryCatalog.needleSizes.firstOrNull()
        } else {
            needleVariant.value = null
        }
    }

    fun saveMovement(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cents = MoneyUtils.parseAmountToCents(amountStr.value)
        if (cents == null || cents <= 0L) {
            onError("Introduce un importe válido.")
            return
        }
        if (description.value.isBlank()) {
            onError("La descripción o nombre es obligatoria.")
            return
        }
        if (category.value.isBlank()) {
            onError("Selecciona una categoría financiera.")
            return
        }

        val isExpense = type.value == MovementType.EXPENSE
        val shouldCreateInventory = isExpense && registerInInventory.value

        var parsedGeneralQty: Long = 1000L
        var parsedEyeQty: Long = 1L
        var parsedHairQty: Long = 1000L

        if (shouldCreateInventory) {
            val family = inventoryFamily.value
            val unit = selectedUnit.value
            val isDivisible = InventoryCatalog.isUnitDivisible(unit)

            when (family) {
                "Ojos" -> {
                    val res = QuantityParser.parseInteger(quantityStr.value)
                    if (res.isFailure) {
                        onError("Para ojos la cantidad debe ser un número entero (pares o unidades).")
                        return
                    }
                    parsedEyeQty = res.getOrNull() ?: 1L
                }
                "Pelo y cabello" -> {
                    val res = QuantityParser.parseThousandths(quantityStr.value)
                    if (res.isFailure) {
                        onError("Introduce una cantidad válida en gramos de pelo (ej: 10 o 2,5).")
                        return
                    }
                    parsedHairQty = res.getOrNull() ?: 1000L
                }
                else -> {
                    if (isDivisible) {
                        val res = QuantityParser.parseThousandths(quantityStr.value)
                        if (res.isFailure) {
                            onError("Introduce una cantidad válida (ej: 50 o 2,5).")
                            return
                        }
                        parsedGeneralQty = res.getOrNull() ?: 1000L
                    } else {
                        val res = QuantityParser.parseInteger(quantityStr.value)
                        if (res.isFailure) {
                            onError("La unidad '$unit' no admite decimales. Introduce un número entero.")
                            return
                        }
                        parsedGeneralQty = (res.getOrNull() ?: 1L) * 1000L
                    }
                }
            }
        }

        viewModelScope.launch {
            val now = DateUtils.getCurrentTimestamp()
            database.withTransaction {
                val movement = Movement(
                    id = movementId ?: 0L,
                    type = type.value,
                    amountCents = cents,
                    description = description.value.trim(),
                    category = category.value,
                    date = dateMillis.value,
                    createdAt = if (isEditMode) createdAtOriginal else now,
                    updatedAt = now,
                    notes = notes.value.takeIf { it.isNotBlank() }
                )

                if (isEditMode) {
                    movementRepository.updateMovement(movement)

                    if (isLinkedToInventory.value && !isLotPartiallyConsumed.value) {
                        val gLots = database.materialPurchaseLotDao().getLotsByMovementId(movementId!!)
                        val eLots = database.eyePurchaseLotDao().getLotsByMovementId(movementId)
                        val hLots = database.hairPurchaseLotDao().getLotsByMovementId(movementId)

                        for (lot in gLots) {
                            val mat = database.materialDao().getMaterialById(lot.materialId)
                            if (mat != null) {
                                val delta = parsedGeneralQty - lot.purchasedQuantity
                                val newAvail = maxOf(0L, mat.availableQuantity + delta)
                                val newTotal = maxOf(0L, mat.totalPurchased + delta)
                                database.materialDao().updateMaterial(
                                    mat.copy(
                                        name = description.value.trim(),
                                        subCategory = inventorySubCategory.value,
                                        unit = selectedUnit.value,
                                        availableQuantity = newAvail,
                                        totalPurchased = newTotal,
                                        updatedAt = now
                                    )
                                )
                            }
                            database.materialPurchaseLotDao().updateLot(
                                lot.copy(
                                    paidAmountCents = cents,
                                    purchasedQuantity = parsedGeneralQty,
                                    remainingQuantity = parsedGeneralQty,
                                    purchaseDate = dateMillis.value
                                )
                            )
                        }

                        for (lot in eLots) {
                            database.eyeMaterialDao().getEyeMaterialById(lot.eyeMaterialId)?.let { eye ->
                                database.eyeMaterialDao().updateEyeMaterial(
                                    eye.copy(
                                        name = description.value.trim(),
                                        subCategory = inventorySubCategory.value,
                                        modality = if (selectedUnit.value.equals("pares", true) || eyeModality.value == EyeModality.PAIRS) EyeModality.PAIRS else EyeModality.UNITS,
                                        updatedAt = now
                                    )
                                )
                            }
                            database.eyePurchaseLotDao().updateLot(
                                lot.copy(
                                    paidAmountCents = cents,
                                    purchasedQuantity = parsedEyeQty,
                                    remainingQuantity = parsedEyeQty,
                                    purchaseDate = dateMillis.value
                                )
                            )
                        }

                        for (lot in hLots) {
                            database.hairMaterialDao().getHairMaterialById(lot.hairMaterialId)?.let { hair ->
                                database.hairMaterialDao().updateHairMaterial(
                                    hair.copy(
                                        name = description.value.trim(),
                                        subCategory = inventorySubCategory.value,
                                        updatedAt = now
                                    )
                                )
                            }
                            database.hairPurchaseLotDao().updateLot(
                                lot.copy(
                                    paidAmountCents = cents,
                                    purchasedQuantityGrams = parsedHairQty,
                                    remainingQuantityGrams = parsedHairQty,
                                    purchaseDate = dateMillis.value
                                )
                            )
                        }
                    }
                } else {
                    val movId = movementRepository.insertMovement(movement)

                    if (shouldCreateInventory) {
                        val family = inventoryFamily.value
                        val subCat = inventorySubCategory.value
                        val fullSubCategory = if (needleVariant.value != null) {
                            "${subCat ?: ""} (${needleVariant.value})".trim()
                        } else {
                            subCat
                        }

                        if (family == "Ojos") {
                            val eyeMat = EyeMaterialEntity(
                                name = description.value.trim(),
                                color = "",
                                type = fullSubCategory ?: "",
                                subCategory = fullSubCategory,
                                modality = if (selectedUnit.value.equals("pares", true) || eyeModality.value == EyeModality.PAIRS) EyeModality.PAIRS else EyeModality.UNITS,
                                createdAt = now,
                                updatedAt = now
                            )
                            val eyeId = database.eyeMaterialDao().insertEyeMaterial(eyeMat)
                            database.eyePurchaseLotDao().insertLot(
                                EyePurchaseLotEntity(
                                    eyeMaterialId = eyeId,
                                    movementId = movId,
                                    purchasedQuantity = parsedEyeQty,
                                    remainingQuantity = parsedEyeQty,
                                    paidAmountCents = cents,
                                    purchaseDate = dateMillis.value,
                                    createdAt = now
                                )
                            )
                        } else if (family == "Pelo y cabello") {
                            val hairMat = HairMaterialEntity(
                                name = description.value.trim(),
                                hairType = fullSubCategory ?: "",
                                color = "",
                                subCategory = fullSubCategory,
                                createdAt = now,
                                updatedAt = now
                            )
                            val hairId = database.hairMaterialDao().insertHairMaterial(hairMat)
                            database.hairPurchaseLotDao().insertLot(
                                HairPurchaseLotEntity(
                                    hairMaterialId = hairId,
                                    movementId = movId,
                                    purchasedQuantityGrams = parsedHairQty,
                                    remainingQuantityGrams = parsedHairQty,
                                    paidAmountCents = cents,
                                    purchaseDate = dateMillis.value,
                                    createdAt = now
                                )
                            )
                        } else {
                            val mat = MaterialEntity(
                                name = description.value.trim(),
                                category = family,
                                subCategory = fullSubCategory,
                                unit = selectedUnit.value,
                                totalPurchased = parsedGeneralQty,
                                availableQuantity = parsedGeneralQty,
                                createdAt = now,
                                updatedAt = now
                            )
                            val matId = database.materialDao().insertMaterial(mat)
                            database.materialPurchaseLotDao().insertLot(
                                MaterialPurchaseLotEntity(
                                    materialId = matId,
                                    movementId = movId,
                                    purchasedQuantity = parsedGeneralQty,
                                    remainingQuantity = parsedGeneralQty,
                                    paidAmountCents = cents,
                                    purchaseDate = dateMillis.value,
                                    createdAt = now
                                )
                            )
                        }
                    }
                }
            }
            onSuccess()
        }
    }
}
