package com.example.rebornfinance.feature.movements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rebornfinance.domain.model.InventoryCatalog
import com.example.rebornfinance.domain.model.MovementType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementFormScreen(
    viewModel: MovementFormViewModel,
    onNavigateBack: () -> Unit
) {
    val type by viewModel.type.collectAsState()
    val amountStr by viewModel.amountStr.collectAsState()
    val description by viewModel.description.collectAsState()
    val category by viewModel.category.collectAsState()
    val subCategory by viewModel.subCategory.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }
    var expandedSubCategoryDropdown by remember { mutableStateOf(false) }

    val filteredCategories = categories.filter { it.type == type }
    val availableSubCategories = InventoryCatalog.categoriesWithSubcategories[category] ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditMode) "Editar movimiento" else "Nuevo movimiento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Selector
            Text("Tipo de movimiento", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = type == MovementType.EXPENSE,
                    onClick = {
                        viewModel.type.value = MovementType.EXPENSE
                        viewModel.category.value = ""
                        viewModel.subCategory.value = null
                    },
                    label = { Text("Gasto") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = type == MovementType.INCOME,
                    onClick = {
                        viewModel.type.value = MovementType.INCOME
                        viewModel.category.value = ""
                        viewModel.subCategory.value = null
                    },
                    label = { Text("Ingreso") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = type == MovementType.REFUND,
                    onClick = {
                        viewModel.type.value = MovementType.REFUND
                        viewModel.category.value = ""
                        viewModel.subCategory.value = null
                    },
                    label = { Text("Reembolso") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Amount
            OutlinedTextField(
                value = amountStr,
                onValueChange = { viewModel.amountStr.value = it },
                label = { Text("Importe (€)") },
                placeholder = { Text("Ej: 35,00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.description.value = it },
                label = { Text("Descripción") },
                placeholder = { Text("Ej: Mohair premium, Ojos cristal...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategoryDropdown,
                onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría principal") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoryDropdown,
                    onDismissRequest = { expandedCategoryDropdown = false }
                ) {
                    filteredCategories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                viewModel.category.value = cat.name
                                viewModel.subCategory.value = null
                                expandedCategoryDropdown = false
                            }
                        )
                    }
                }
            }

            // SubCategory Dropdown (Cascading)
            if (availableSubCategories.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = expandedSubCategoryDropdown,
                    onExpandedChange = { expandedSubCategoryDropdown = !expandedSubCategoryDropdown }
                ) {
                    OutlinedTextField(
                        value = subCategory ?: "Selecciona subcategoría",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Subcategoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubCategoryDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSubCategoryDropdown,
                        onDismissRequest = { expandedSubCategoryDropdown = false }
                    ) {
                        availableSubCategories.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(sub) },
                                onClick = {
                                    viewModel.subCategory.value = sub
                                    expandedSubCategoryDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.notes.value = it },
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.saveMovement(
                        onSuccess = onNavigateBack,
                        onError = { err -> errorMessage = err }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar movimiento", style = MaterialTheme.typography.titleLarge, fontSize = 16.sp)
            }
        }
    }
}
