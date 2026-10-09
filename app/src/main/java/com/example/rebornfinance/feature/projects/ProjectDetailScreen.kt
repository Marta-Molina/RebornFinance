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

    var showAddCostDialog by remember { mutableStateOf(false) }
    var showRegisterSaleDialog by remember { mutableStateOf(false) }
    var showDeleteProjectDialog by remember { mutableStateOf(false) }
    var costToDelete by remember { mutableStateOf<ProjectCost?>(null) }

    // Add Cost Dialog state
    var conceptInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var costError by remember { mutableStateOf<String?>(null) }

    // Register Sale Dialog state
    var salePriceInput by remember { mutableStateOf("") }
    var saleError by remember { mutableStateOf<String?>(null) }

    if (showAddCostDialog) {
        AlertDialog(
            onDismissRequest = { showAddCostDialog = false; costError = null },
            title = { Text("Añadir coste al proyecto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = conceptInput,
                        onValueChange = { conceptInput = it },
                        label = { Text("Concepto (ej: Kit, Envío...)") },
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
            val totalCost = ProjectCalculator.calculateTotalCost(costs)
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
                                Text("Coste total acumulado:")
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Costes del proyecto",
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
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay costes registrados para este proyecto.",
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
