package com.example.rebornfinance.feature.movements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.core.money.MoneyUtils
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

                    if (type.value == MovementType.EXPENSE) {
                        val cat = category.value
                        if (cat.equals("Ojos", true)) {
                            val eyeMat = EyeMaterialEntity(
                                name = description.value.trim(),
                                color = "Estándar",
                                type = "Cristal/Acrílico",
                                modality = EyeModality.PAIRS,
                                createdAt = now,
                                updatedAt = now
                            )
                            val eyeId = database.eyeMaterialDao().insertEyeMaterial(eyeMat)
                            database.eyePurchaseLotDao().insertLot(
                                EyePurchaseLotEntity(
                                    eyeMaterialId = eyeId,
                                    purchasedQuantity = 1L,
                                    remainingQuantity = 1L,
                                    paidAmountCents = cents,
                                    purchaseDate = dateMillis.value,
                                    createdAt = now
                                )
                            )
                        } else if (cat.equals("Pelo", true)) {
                            val hairMat = HairMaterialEntity(
                                name = description.value.trim(),
                                hairType = "Mohair",
                                color = "Estándar",
                                createdAt = now,
                                updatedAt = now
                            )
                            val hairId = database.hairMaterialDao().insertHairMaterial(hairMat)
                            database.hairPurchaseLotDao().insertLot(
                                HairPurchaseLotEntity(
                                    hairMaterialId = hairId,
                                    purchasedQuantityGrams = 10000L, // 10g
                                    remainingQuantityGrams = 10000L,
                                    paidAmountCents = cents,
                                    purchaseDate = dateMillis.value,
                                    createdAt = now
                                )
                            )
                        } else if (cat.equals("Materiales", true) || cat.equals("Pinturas y barnices", true)) {
                            val mat = MaterialEntity(
                                name = description.value.trim(),
                                category = cat,
                                unit = "ml",
                                totalPurchased = 10000L,
                                availableQuantity = 10000L,
                                createdAt = now,
                                updatedAt = now
                            )
                            val matId = database.materialDao().insertMaterial(mat)
                            database.materialPurchaseLotDao().insertLot(
                                MaterialPurchaseLotEntity(
                                    materialId = matId,
                                    purchasedQuantity = 10000L,
                                    remainingQuantity = 10000L,
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
