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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.domain.model.EyeModality
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
    val dateMillis by viewModel.dateMillis.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val registerInInventory by viewModel.registerInInventory.collectAsState()
    val inventoryFamily by viewModel.inventoryFamily.collectAsState()
    val inventorySubCategory by viewModel.inventorySubCategory.collectAsState()
    val needleVariant by viewModel.needleVariant.collectAsState()
    val eyeModality by viewModel.eyeModality.collectAsState()
    val selectedUnit by viewModel.selectedUnit.collectAsState()
    val quantityStr by viewModel.quantityStr.collectAsState()

    val isLinkedToInventory by viewModel.isLinkedToInventory.collectAsState()
    val isLotPartiallyConsumed by viewModel.isLotPartiallyConsumed.collectAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }
    var expandedFamilyDropdown by remember { mutableStateOf(false) }
    var expandedSubCategoryDropdown by remember { mutableStateOf(false) }
    var expandedNeedleDropdown by remember { mutableStateOf(false) }
    var expandedUnitDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val filteredCategories = categories.filter { it.type == type }
    val availableFamilies = InventoryCatalog.masterCatalog.keys.toList()
    val availableSubCategories = InventoryCatalog.masterCatalog[inventoryFamily] ?: emptyList()
    val availableUnits = InventoryCatalog.validUnitsPerFamily[inventoryFamily] ?: listOf("unidades")
    val isExpense = type == MovementType.EXPENSE
    val isQuantityProtected = viewModel.isEditMode && isLotPartiallyConsumed

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selected ->
                        viewModel.dateMillis.value = selected
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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
            if (isQuantityProtected) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Parte de este lote ya ha sido consumida en proyectos reborn. Por coherencia de costes, el importe y la cantidad están protegidos, pero puedes actualizar la descripción, fecha y notas.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Type Selector (Disabled in edit mode to preserve integrity)
            if (!viewModel.isEditMode) {
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
                        },
                        label = { Text("Gasto") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == MovementType.INCOME,
                        onClick = {
                            viewModel.type.value = MovementType.INCOME
                            viewModel.category.value = ""
                            viewModel.registerInInventory.value = false
                        },
                        label = { Text("Ingreso") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == MovementType.REFUND,
                        onClick = {
                            viewModel.type.value = MovementType.REFUND
                            viewModel.category.value = ""
                            viewModel.registerInInventory.value = false
                        },
                        label = { Text("Reembolso") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Date with Picker dialog
            OutlinedTextField(
                value = DateUtils.formatDate(dateMillis),
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de operación (DD/MM/AAAA)") },
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) {
                        Text("Cambiar", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Amount
            OutlinedTextField(
                value = amountStr,
                onValueChange = { if (!isQuantityProtected) viewModel.amountStr.value = it },
                readOnly = isQuantityProtected,
                label = { Text("Importe total (€) *") },
                placeholder = { Text("Ej: 12,50") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Description / Name
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.description.value = it },
                label = { Text(if (isExpense && registerInInventory) "Nombre del material o producto *" else "Descripción o concepto *") },
                placeholder = { Text(if (isExpense && registerInInventory) "Ej: Ojos de cristal azules 20mm, Mohair rubio..." else "Ej: Transporte, luz taller...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Financial Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategoryDropdown,
                onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown }
            ) {
                OutlinedTextField(
                    value = category.ifBlank { "Selecciona categoría financiera" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría financiera contable *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
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
                                expandedCategoryDropdown = false
                            }
                        )
                    }
                }
            }

            // Register in Inventory Checkbox for Expenses
            if (isExpense && (!viewModel.isEditMode || !isLinkedToInventory)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = registerInInventory,
                            onCheckedChange = { viewModel.registerInInventory.value = it }
                        )
                        Column {
                            Text("Registrar compra en el inventario del taller", fontWeight = FontWeight.SemiBold)
                            Text("Crea un lote físico con existencias reales y trazabilidad FIFO.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Technical Inventory Section if registerInInventory is true
            if (isExpense && registerInInventory) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Catálogo Técnico de Inventario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        // 1. Technical Family (13 families)
                        ExposedDropdownMenuBox(
                            expanded = expandedFamilyDropdown && !isQuantityProtected,
                            onExpandedChange = { if (!isQuantityProtected) expandedFamilyDropdown = !expandedFamilyDropdown }
                        ) {
                            OutlinedTextField(
                                value = inventoryFamily,
                                onValueChange = {},
                                readOnly = true,
                                enabled = !isQuantityProtected,
                                label = { Text("Familia técnica *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFamilyDropdown) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedFamilyDropdown,
                                onDismissRequest = { expandedFamilyDropdown = false }
                            ) {
                                availableFamilies.forEach { fam ->
                                    DropdownMenuItem(
                                        text = { Text(fam) },
                                        onClick = {
                                            viewModel.onFamilySelected(fam)
                                            expandedFamilyDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // 2. Cascading Subcategory
                        if (availableSubCategories.isNotEmpty()) {
                            ExposedDropdownMenuBox(
                                expanded = expandedSubCategoryDropdown && !isQuantityProtected,
                                onExpandedChange = { if (!isQuantityProtected) expandedSubCategoryDropdown = !expandedSubCategoryDropdown }
                            ) {
                                OutlinedTextField(
                                    value = inventorySubCategory ?: "Selecciona subcategoría",
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = !isQuantityProtected,
                                    label = { Text("Subcategoría técnica *") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubCategoryDropdown) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
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
                                                viewModel.onSubCategorySelected(sub)
                                                expandedSubCategoryDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // 3. Special Case: Needles Variants
                        if (inventoryFamily == "Agujas y accesorios de injerto" && inventorySubCategory == "Agujas") {
                            ExposedDropdownMenuBox(
                                expanded = expandedNeedleDropdown && !isQuantityProtected,
                                onExpandedChange = { if (!isQuantityProtected) expandedNeedleDropdown = !expandedNeedleDropdown }
                            ) {
                                OutlinedTextField(
                                    value = needleVariant ?: "Selecciona calibre de aguja",
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = !isQuantityProtected,
                                    label = { Text("Calibre y número de barbas") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedNeedleDropdown) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedNeedleDropdown,
                                    onDismissRequest = { expandedNeedleDropdown = false }
                                ) {
                                    InventoryCatalog.needleSizes.forEach { variant ->
                                        DropdownMenuItem(
                                            text = { Text(variant) },
                                            onClick = {
                                                viewModel.needleVariant.value = variant
                                                expandedNeedleDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // 4. Special Case: Eyes Modality (Pairs vs Units)
                        if (inventoryFamily == "Ojos") {
                            Text("Modalidad de compra de ojos:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedUnit == "pares",
                                    onClick = {
                                        viewModel.selectedUnit.value = "pares"
                                        viewModel.eyeModality.value = EyeModality.PAIRS
                                    },
                                    label = { Text("Pares") },
                                    enabled = !isQuantityProtected,
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = selectedUnit == "unidades",
                                    onClick = {
                                        viewModel.selectedUnit.value = "unidades"
                                        viewModel.eyeModality.value = EyeModality.UNITS
                                    },
                                    label = { Text("Unidades individuales") },
                                    enabled = !isQuantityProtected,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // 5. Quantity and Unit Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = quantityStr,
                                onValueChange = { if (!isQuantityProtected) viewModel.quantityStr.value = it },
                                readOnly = isQuantityProtected,
                                label = { Text("Cantidad *") },
                                placeholder = {
                                    val isDiv = InventoryCatalog.isUnitDivisible(selectedUnit)
                                    Text(if (isDiv) "Ej: 2,5 o 10" else "Ej: 1 o 2")
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.2f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Unit Dropdown
                            ExposedDropdownMenuBox(
                                expanded = expandedUnitDropdown && !isQuantityProtected,
                                onExpandedChange = { if (!isQuantityProtected) expandedUnitDropdown = !expandedUnitDropdown },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = selectedUnit,
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = !isQuantityProtected,
                                    label = { Text("Unidad") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnitDropdown) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedUnitDropdown,
                                    onDismissRequest = { expandedUnitDropdown = false }
                                ) {
                                    availableUnits.forEach { u ->
                                        DropdownMenuItem(
                                            text = { Text(u) },
                                            onClick = {
                                                viewModel.selectedUnit.value = u
                                                expandedUnitDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.notes.value = it },
                label = { Text("Notas y observaciones (opcional)") },
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
