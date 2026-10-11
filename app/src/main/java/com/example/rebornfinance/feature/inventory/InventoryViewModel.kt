package com.example.rebornfinance.feature.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.repository.EyeMaterialRepositoryImpl
import com.example.rebornfinance.data.repository.HairMaterialRepositoryImpl
import com.example.rebornfinance.data.repository.MaterialRepositoryImpl
import com.example.rebornfinance.domain.model.EyeMaterial
import com.example.rebornfinance.domain.model.HairMaterial
import com.example.rebornfinance.domain.model.InventoryCatalog
import com.example.rebornfinance.domain.model.Material
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val materialRepository = MaterialRepositoryImpl(
        database,
        database.materialDao(),
        database.materialPurchaseLotDao(),
        database.materialConsumptionRuleDao(),
        database.projectMaterialConsumptionDao(),
        database.movementDao()
    )
    private val eyeRepository = EyeMaterialRepositoryImpl(
        database,
        database.eyeMaterialDao(),
        database.eyePurchaseLotDao(),
        database.projectEyeAssignmentDao(),
        database.movementDao()
    )
    private val hairRepository = HairMaterialRepositoryImpl(
        database,
        database.hairMaterialDao(),
        database.hairPurchaseLotDao(),
        database.projectHairConsumptionDao(),
        database.movementDao()
    )

    val generalMaterials: StateFlow<List<Material>> = materialRepository.getAllMaterials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val eyeMaterials: StateFlow<List<EyeMaterial>> = eyeRepository.getAllEyeMaterials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hairMaterials: StateFlow<List<HairMaterial>> = hairRepository.getAllHairMaterials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val catalog = InventoryCatalog.categoriesWithSubcategories

    var selectedCategory = MutableStateFlow<String?>(null)
    var selectedSubCategory = MutableStateFlow<String?>(null)

    fun updatePhoto(materialId: Long, uri: String, isGeneral: Boolean) {
        viewModelScope.launch {
            if (isGeneral) {
                materialRepository.getMaterialById(materialId)?.let { mat ->
                    materialRepository.updateMaterial(mat.copy(photoUri = uri, updatedAt = System.currentTimeMillis()))
                }
            }
        }
    }
}
