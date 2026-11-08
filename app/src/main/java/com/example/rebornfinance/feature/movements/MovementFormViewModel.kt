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
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovementFormViewModel(application: Application, private val movementId: Long?) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val movementRepository = MovementRepositoryImpl(database.movementDao())
    private val categoryRepository = CategoryRepositoryImpl(database.categoryDao())

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var type = MutableStateFlow(MovementType.EXPENSE)
    var amountStr = MutableStateFlow("")
    var description = MutableStateFlow("")
    var category = MutableStateFlow("")
    var subCategory = MutableStateFlow<String?>(null)
    var quantityStr = MutableStateFlow("1")
    var selectedUnit = MutableStateFlow("unidades")
    var registerInInventory = MutableStateFlow(true)
    var dateMillis = MutableStateFlow(DateUtils.getCurrentTimestamp())
    var notes = MutableStateFlow("")

    var isEditMode = movementId != null && movementId > 0L
    private var createdAtOriginal = DateUtils.getCurrentTimestamp()

    init {
        if (isEditMode) {
            viewModelScope.launch {
                movementRepository.getMovementById(movementId!!)?.let { mov ->
                    type.value = mov.type
                    amountStr.value = (mov.amountCents / 100.0).toString().replace('.', ',')
                    description.value = mov.description
                    category.value = mov.category
                    dateMillis.value = mov.date
                    notes.value = mov.notes ?: ""
                    createdAtOriginal = mov.createdAt
                }
            }
        }
    }

    fun saveMovement(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cents = MoneyUtils.parseAmountToCents(amountStr.value)
        if (cents == null || cents <= 0L) {
            onError("Introduce un importe válido.")
            return
        }
        if (description.value.isBlank()) {
            onError("La descripción es obligatoria.")
            return
        }
        if (category.value.isBlank()) {
            onError("Selecciona una categoría.")
            return
        }

        val isExpense = type.value == MovementType.EXPENSE
        val shouldCreateInventory = isExpense && registerInInventory.value

        if (isEditMode && shouldCreateInventory) {
            // Protect purchase editing for linked inventory items
            onError("La edición de compras vinculadas a inventario está restringida para mantener la coherencia de stock.")
            return
        }

        var parsedQty: Long = 1000L
        if (shouldCreateInventory) {
            val cat = category.value
            val result = when {
                cat.equals("Ojos", true) -> QuantityParser.parseInteger(quantityStr.value)
                cat.equals("Pelo y cabello", true) -> QuantityParser.parseThousandths(quantityStr.value)
                else -> QuantityParser.parseThousandths(quantityStr.value)
            }

            if (result.isFailure) {
                onError(result.exceptionOrNull()?.message ?: "Cantidad inválida.")
                return
            }
            parsedQty = result.getOrNull() ?: 1000L
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
                } else {
                    movementRepository.insertMovement(movement)

                    if (shouldCreateInventory) {
                        val cat = category.value
                        val subCat = subCategory.value

                        if (cat.equals("Ojos", true)) {
                            val eyeMat = EyeMaterialEntity(
                                name = description.value.trim(),
                                color = "",
                                type = subCat ?: "",
                                subCategory = subCat,
                                modality = if (selectedUnit.value.equals("pares", true)) EyeModality.PAIRS else EyeModality.UNITS,
                                createdAt = now,
                                updatedAt = now
                            )
                            val eyeId = database.eyeMaterialDao().insertEyeMaterial(eyeMat)
                            database.eyePurchaseLotDao().insertLot(
                                EyePurchaseLotEntity(
                                    eyeMaterialId = eyeId,
                                    purchasedQuantity = parsedQty,
                                    remainingQuantity = parsedQty,
                                    paidAmountCents = cents,
                                    purchaseDate = dateMillis.value,
                                    createdAt = now
                                )
                            )
                        } else if (cat.equals("Pelo y cabello", true)) {
                            val hairMat = HairMaterialEntity(
                                name = description.value.trim(),
                                hairType = subCat ?: "",
                                color = "",
                                subCategory = subCat,
                                createdAt = now,
                                updatedAt = now
                            )
                            val hairId = database.hairMaterialDao().insertHairMaterial(hairMat)
                            database.hairPurchaseLotDao().insertLot(
                                HairPurchaseLotEntity(
                                    hairMaterialId = hairId,
                                    purchasedQuantityGrams = parsedQty,
                                    remainingQuantityGrams = parsedQty,
                                    paidAmountCents = cents,
                                    purchaseDate = dateMillis.value,
                                    createdAt = now
                                )
                            )
                        } else {
                            val mat = MaterialEntity(
                                name = description.value.trim(),
                                category = cat,
                                subCategory = subCat,
                                unit = selectedUnit.value,
                                totalPurchased = parsedQty,
                                availableQuantity = parsedQty,
                                createdAt = now,
                                updatedAt = now
                            )
                            val matId = database.materialDao().insertMaterial(mat)
                            database.materialPurchaseLotDao().insertLot(
                                MaterialPurchaseLotEntity(
                                    materialId = matId,
                                    purchasedQuantity = parsedQty,
                                    remainingQuantity = parsedQty,
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
