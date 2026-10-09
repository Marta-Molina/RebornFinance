package com.example.rebornfinance.feature.movements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import com.example.rebornfinance.feature.home.MovementItemRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementsScreen(
    viewModel: MovementsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    val movements by viewModel.filteredMovements.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedTypeFilter.collectAsState()

    var movementToDelete by remember { mutableStateOf<Movement?>(null) }

    if (movementToDelete != null) {
        AlertDialog(
            onDismissRequest = { movementToDelete = null },
            title = { Text("Eliminar movimiento") },
            text = { Text("¿Estás segura de que deseas eliminar '${movementToDelete?.description}'? Esta acción recalculará el saldo.") },
            confirmButton = {
                TextButton(onClick = {
                    movementToDelete?.let { viewModel.deleteMovement(it) }
                    movementToDelete = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { movementToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de movimientos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por descripción, nota...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { viewModel.setTypeFilter(null) },
                    label = { Text("Todos") }
                )
                FilterChip(
                    selected = selectedType == MovementType.INCOME,
                    onClick = { viewModel.setTypeFilter(MovementType.INCOME) },
                    label = { Text("Ingresos") }
                )
                FilterChip(
                    selected = selectedType == MovementType.EXPENSE,
                    onClick = { viewModel.setTypeFilter(MovementType.EXPENSE) },
                    label = { Text("Gastos") }
                )
                FilterChip(
                    selected = selectedType == MovementType.REFUND,
                    onClick = { viewModel.setTypeFilter(MovementType.REFUND) },
                    label = { Text("Reembolsos") }
                )
            }

            if (movements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron movimientos.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(movements) { movement ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            MovementItemRow(
                                movement = movement,
                                onClick = { onNavigateToEdit(movement.id) }
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
