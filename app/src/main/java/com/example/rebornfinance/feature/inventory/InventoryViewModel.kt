package com.example.rebornfinance.feature.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.local.entity.EyeMaterialEntity
import com.example.rebornfinance.data.local.entity.EyePurchaseLotEntity
import com.example.rebornfinance.data.local.entity.HairMaterialEntity
import com.example.rebornfinance.data.local.entity.HairPurchaseLotEntity
import com.example.rebornfinance.data.local.entity.MaterialEntity
import com.example.rebornfinance.data.local.entity.MaterialPurchaseLotEntity
import com.example.rebornfinance.data.repository.EyeMaterialRepositoryImpl
import com.example.rebornfinance.data.repository.HairMaterialRepositoryImpl
import com.example.rebornfinance.data.repository.MaterialRepositoryImpl
import com.example.rebornfinance.domain.model.EyeMaterial
import com.example.rebornfinance.domain.model.EyeModality
import com.example.rebornfinance.domain.model.HairMaterial
import com.example.rebornfinance.domain.model.Material
import com.example.rebornfinance.domain.model.Movement
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

    val allCategories = listOf(
        "Todas",
        "Kits y esculturas",
        "Cuerpos y extremidades",
        "Ojos",
        "Pelo y cabello",
        "Imprimaciones",
        "Pinturas y pigmentos",
        "Barnices y acabados",
        "Rellenos y pesos",
        "Adhesivos y consumibles",
        "Herramientas reutilizables",
        "Agujas y accesorios",
        "Ropa y complementos",
        "Embalaje y envíos",
        "Otros materiales"
    )

    var selectedCategoryFilter = MutableStateFlow("Todas")
}
