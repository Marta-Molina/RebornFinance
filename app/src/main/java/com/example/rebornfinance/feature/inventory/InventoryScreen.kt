package com.example.rebornfinance.feature.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    onNavigateBack: () -> Unit
) {
    val generalMaterials by viewModel.generalMaterials.collectAsState()
    val eyeMaterials by viewModel.eyeMaterials.collectAsState()
    val hairMaterials by viewModel.hairMaterials.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario de Materiales", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("General") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Ojos") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Pelo") })
            }

            when (selectedTab) {
                0 -> {
                    if (generalMaterials.isEmpty()) {
                        EmptyInventoryBox()
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(generalMaterials) { mat ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = mat.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text(text = "Categoría: ${mat.category} • Disponible: ${mat.availableQuantity / 1000.0} ${mat.unit}")
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    if (eyeMaterials.isEmpty()) {
                        EmptyInventoryBox()
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(eyeMaterials) { eye ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = eye.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text(text = "Color: ${eye.color} • Tipo: ${eye.type} • Modalidad: ${eye.modality.displayName}")
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    if (hairMaterials.isEmpty()) {
                        EmptyInventoryBox()
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(hairMaterials) { hair ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = hair.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text(text = "Tipo: ${hair.hairType} • Color: ${hair.color}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyInventoryBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No hay artículos en el inventario.\nRegistra compras desde 'Nuevo movimiento'.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
