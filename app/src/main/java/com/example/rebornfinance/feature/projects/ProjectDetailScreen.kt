package com.example.rebornfinance.feature.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.core.money.MoneyUtils
import com.example.rebornfinance.domain.calculator.ProjectCalculator
import com.example.rebornfinance.domain.model.ProjectCost
import com.example.rebornfinance.domain.model.ProjectEyeAssignment
import com.example.rebornfinance.domain.model.ProjectHairConsumption
import com.example.rebornfinance.domain.model.ProjectMaterialConsumption
import com.example.rebornfinance.domain.model.ProjectStatus
import com.example.rebornfinance.domain.model.RebornProject
import com.example.rebornfinance.ui.theme.MutedRed
import com.example.rebornfinance.ui.theme.SageGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    viewModel: ProjectDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    val project by viewModel.project.collectAsState()
    val costs by viewModel.costs.collectAsState()
    val materialConsumptions by viewModel.materialConsumptions.collectAsState()
    val eyeAssignments by viewModel.eyeAssignments.collectAsState()
    val hairConsumptions by viewModel.hairConsumptions.collectAsState()
    val allEyeMaterials by viewModel.allEyeMaterials.collectAsState()
    val allHairMaterials by viewModel.allHairMaterials.collectAsState()

    var showAddCostDialog by remember { mutableStateOf(false) }
    var showRegisterSaleDialog by remember { mutableStateOf(false) }
    var showDeleteProjectDialog by remember { mutableStateOf(false) }
    var showEyeSelectDialog by remember { mutableStateOf(false) }
    var showHairSelectDialog by remember { mutableStateOf(false) }

    var costToDelete by remember { mutableStateOf<ProjectCost?>(null) }
    var consumptionToDelete by remember { mutableStateOf<ProjectMaterialConsumption?>(null) }
    var eyeAssignmentToCancel by remember { mutableStateOf<ProjectEyeAssignment?>(null) }
    var hairConsumptionToCancel by remember { mutableStateOf<ProjectHairConsumption?>(null) }
    var materialActionError by remember { mutableStateOf<String?>(null) }

    // Add Cost Dialog state
    var conceptInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var costError by remember { mutableStateOf<String?>(null) }

    // Register Sale Dialog state
    var salePriceInput by remember { mutableStateOf("") }
    var saleError by remember { mutableStateOf<String?>(null) }

    // Eye assign state
    var selectedEyeQty by remember { mutableStateOf("1") }

    // Hair assign state
    var selectedHairGrams by remember { mutableStateOf("5") }

    if (showEyeSelectDialog) {
        AlertDialog(
            onDismissRequest = { showEyeSelectDialog = false },
            title = { Text("Seleccionar ojos del inventario") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (allEyeMaterials.isEmpty()) {
                        Text("No hay referencias de ojos en el inventario. Añade una comprando ojos desde 'Nuevo movimiento'.")
                    } else {
                        allEyeMaterials.forEach { eye ->
                            OutlinedButton(
                                onClick = {
                                    val qty = selectedEyeQty.toLongOrNull() ?: 1L
                                    viewModel.assignEyes(
                                        eyeMaterialId = eye.id,
                                        quantity = qty,
                                        onSuccess = { showEyeSelectDialog = false; materialActionError = null },
                                        onError = { err -> materialActionError = err }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("${eye.name} (${eye.color})")
                            }
                        }
                    }
                    OutlinedTextField(
                        value = selectedEyeQty,
                        onValueChange = { selectedEyeQty = it },
                        label = { Text("Cantidad / Pares") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showEyeSelectDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showHairSelectDialog) {
        AlertDialog(
            onDismissRequest = { showHairSelectDialog = false },
            title = { Text("Seleccionar pelo del inventario") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (allHairMaterials.isEmpty()) {
                        Text("No hay referencias de pelo en el inventario. Añade pelo comprando desde 'Nuevo movimiento'.")
                    } else {
                        allHairMaterials.forEach { hair ->
                            OutlinedButton(
                                onClick = {
                                    val grams = (selectedHairGrams.toDoubleOrNull() ?: 5.0) * 1000.0
                                    viewModel.assignHair(
                                        hairMaterialId = hair.id,
                                        quantityGrams = grams.toLong(),
                                        onSuccess = { showHairSelectDialog = false; materialActionError = null },
                                        onError = { err -> materialActionError = err }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("${hair.name} (${hair.hairType} - ${hair.color})")
                            }
                        }
                    }
                    OutlinedTextField(
                        value = selectedHairGrams,
                        onValueChange = { selectedHairGrams = it },
                        label = { Text("Gramos a usar") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHairSelectDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showAddCostDialog) {
        AlertDialog(
            onDismissRequest = { showAddCostDialog = false; costError = null },
            title = { Text("Añadir coste al proyecto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = conceptInput,
                        onValueChange = { conceptInput = it },
                        label = { Text("Concepto (ej: Cuerpo, Envío...)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Importe (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    if (costError != null) {
                        Text(text = costError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addCost(
                        concept = conceptInput,
                        amountStr = amountInput,
                        notes = null,
                        onSuccess = {
                            showAddCostDialog = false
                            conceptInput = ""
                            amountInput = ""
                            costError = null
                        },
                        onError = { err -> costError = err }
                    )
                }) {
                    Text("Añadir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCostDialog = false; costError = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showRegisterSaleDialog) {
        AlertDialog(
            onDismissRequest = { showRegisterSaleDialog = false; saleError = null },
            title = { Text("Registrar venta real") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Esto creará un movimiento de ingreso financiero vinculado al proyecto y marcará el reborn como Vendido.")
                    OutlinedTextField(
                        value = salePriceInput,
                        onValueChange = { salePriceInput = it },
                        label = { Text("Precio de venta real (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    if (saleError != null) {
                        Text(text = saleError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.registerSale(
                        salePriceStr = salePriceInput,
                        onSuccess = {
                            showRegisterSaleDialog = false
                            salePriceInput = ""
                            saleError = null
                        },
                        onError = { err -> saleError = err }
                    )
                }) {
                    Text("Registrar venta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterSaleDialog = false; saleError = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDeleteProjectDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteProjectDialog = false },
            title = { Text("Eliminar proyecto") },
            text = { Text("¿Estás segura de que deseas eliminar este proyecto y todos sus costes asociados?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProject(onSuccess = onNavigateBack)
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteProjectDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (costToDelete != null) {
        AlertDialog(
            onDismissRequest = { costToDelete = null },
            title = { Text("Eliminar coste") },
            text = { Text("¿Eliminar '${costToDelete?.concept}' de ${MoneyUtils.formatCents(costToDelete?.amountCents ?: 0L)}?") },
            confirmButton = {
                TextButton(onClick = {
                    costToDelete?.let { viewModel.deleteCost(it) }
                    costToDelete = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { costToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (consumptionToDelete != null) {
        AlertDialog(
            onDismissRequest = { consumptionToDelete = null },
            title = { Text("Eliminar consumo de material") },
            text = { Text("¿Eliminar el consumo de '${consumptionToDelete?.category}'?") },
            confirmButton = {
                TextButton(onClick = {
                    consumptionToDelete?.let { viewModel.deleteMaterialConsumption(it) }
                    consumptionToDelete = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { consumptionToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (eyeAssignmentToCancel != null) {
        AlertDialog(
            onDismissRequest = { eyeAssignmentToCancel = null },
            title = { Text("Cancelar asignación de ojos") },
            text = { Text("¿Cancelar esta asignación y devolver el stock al inventario?") },
            confirmButton = {
                TextButton(onClick = {
                    eyeAssignmentToCancel?.let { viewModel.cancelEyeAssignment(it.id) }
                    eyeAssignmentToCancel = null
                }) {
                    Text("Cancelar asignación", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { eyeAssignmentToCancel = null }) {
                    Text("Volver")
                }
            }
        )
    }

    if (hairConsumptionToCancel != null) {
        AlertDialog(
            onDismissRequest = { hairConsumptionToCancel = null },
            title = { Text("Cancelar consumo de pelo") },
            text = { Text("¿Cancelar este consumo y devolver el pelo al inventario?") },
            confirmButton = {
                TextButton(onClick = {
                    hairConsumptionToCancel?.let { viewModel.cancelHairConsumption(it.id) }
                    hairConsumptionToCancel = null
                }) {
                    Text("Cancelar consumo", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { hairConsumptionToCancel = null }) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.name ?: "Detalle del proyecto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (project != null) {
                        IconButton(onClick = { onNavigateToEdit(project!!.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { showDeleteProjectDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        val currentProject = project
        if (currentProject == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val totalCost = ProjectCalculator.calculateTotalCost(costs, materialConsumptions, eyeAssignments, hairConsumptions)
            val estProfit = ProjectCalculator.calculateEstimatedProfit(currentProject.predictedSalePriceCents, totalCost)
            val saleProfit = ProjectCalculator.calculateSaleProfit(currentProject.actualSalePriceCents, totalCost)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentProject.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                StatusBadge(status = currentProject.status)
                            }
                            Text(
                                text = "Kit: ${currentProject.kitName.ifBlank { "Sin especificar" }}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Escultora: ${currentProject.sculptorName.ifBlank { "Sin especificar" }} • Tamaño: ${currentProject.sizeInches} pulgadas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Sistema de pintura: ${when(currentProject.paintingSystem) { "HEAT_SET" -> "Termosellable"; "AIR_DRY" -> "Secado al aire"; else -> "No especificado" }}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Fecha de inicio: ${DateUtils.formatDate(currentProject.startDate)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (currentProject.notes?.isNotBlank() == true) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notas: ${currentProject.notes}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                item {
                    // Financial summary card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Balance económico",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Coste total fabricación:")
                                Text(MoneyUtils.formatCents(totalCost), fontWeight = FontWeight.Bold)
                            }
                            currentProject.predictedSalePriceCents?.let { pred ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Precio venta previsto:")
                                    Text(MoneyUtils.formatCents(pred), fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Beneficio estimado:")
                                    Text(
                                        text = estProfit?.let { MoneyUtils.formatCents(it) } ?: "Pendiente",
                                        fontWeight = FontWeight.Bold,
                                        color = if ((estProfit ?: 0L) >= 0) SageGreen else MutedRed
                                    )
                                }
                            }
                            currentProject.actualSalePriceCents?.let { actual ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Precio venta real:")
                                    Text(MoneyUtils.formatCents(actual), fontWeight = FontWeight.Bold, color = SageGreen)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Beneficio de venta real:")
                                    Text(
                                        text = saleProfit?.let { MoneyUtils.formatCents(it) } ?: "Pendiente",
                                        fontWeight = FontWeight.Bold,
                                        color = if ((saleProfit ?: 0L) >= 0) SageGreen else MutedRed
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    // Material Consumptions Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Materiales y Acabados",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.assignMaterialConsumption(
                                    "Imprimación",
                                    onSuccess = { materialActionError = null },
                                    onError = { err -> materialActionError = err }
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+ Imprimación")
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.assignMaterialConsumption(
                                    "Pintura",
                                    onSuccess = { materialActionError = null },
                                    onError = { err -> materialActionError = err }
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+ Pintura")
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.assignMaterialConsumption(
                                    "Barniz",
                                    onSuccess = { materialActionError = null },
                                    onError = { err -> materialActionError = err }
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+ Barniz")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showEyeSelectDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Elegir Ojos...")
                        }
                        Button(
                            onClick = { showHairSelectDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Elegir Pelo...")
                        }
                    }
                    if (materialActionError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = materialActionError!!, color = MaterialTheme.colorScheme.error)
                    }
                }

                if (materialConsumptions.isNotEmpty()) {
                    items(materialConsumptions) { mc ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Material: ${mc.category}", fontWeight = FontWeight.Medium)
                                    Text(
                                        text = "Consumo: ${mc.consumedQuantity / 1000.0} ${mc.unit} (FIFO)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = MoneyUtils.formatCents(mc.assignedCostCents),
                                        fontWeight = FontWeight.Bold,
                                        color = SageGreen
                                    )
                                    IconButton(onClick = { consumptionToDelete = mc }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar consumo", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                // Eye assignments
                if (eyeAssignments.isNotEmpty()) {
                    item {
                        Text(
                            text = "Ojos asignados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    items(eyeAssignments) { assignment ->
                        if (!assignment.isCancelled) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Ojos (Cantidad: ${assignment.quantity})", fontWeight = FontWeight.Medium)
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = MoneyUtils.formatCents(assignment.assignedCostCents),
                                            fontWeight = FontWeight.Bold,
                                            color = SageGreen
                                        )
                                        IconButton(onClick = { eyeAssignmentToCancel = assignment }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Cancelar asignación de ojos", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Hair consumptions
                if (hairConsumptions.isNotEmpty()) {
                    item {
                        Text(
                            text = "Pelo asignado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    items(hairConsumptions) { hair ->
                        if (!hair.isCancelled) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Pelo (${hair.consumedQuantityGrams / 1000.0} g)", fontWeight = FontWeight.Medium)
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = MoneyUtils.formatCents(hair.assignedCostCents),
                                            fontWeight = FontWeight.Bold,
                                            color = SageGreen
                                        )
                                        IconButton(onClick = { hairConsumptionToCancel = hair }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Cancelar consumo de pelo", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Otros costes manuales",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Button(onClick = { showAddCostDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Añadir coste")
                        }
                    }
                }

                if (costs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay costes manuales adicionales registrados.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(costs) { cost ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = cost.concept, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = DateUtils.formatDate(cost.date),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = MoneyUtils.formatCents(cost.amountCents),
                                        fontWeight = FontWeight.Bold,
                                        color = MutedRed
                                    )
                                    IconButton(onClick = { costToDelete = cost }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar coste", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (currentProject.status != ProjectStatus.SOLD) {
                        Button(
                            onClick = { showRegisterSaleDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                        ) {
                            Text("Registrar venta real")
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
