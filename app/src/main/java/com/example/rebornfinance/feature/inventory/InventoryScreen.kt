package com.example.rebornfinance.feature.inventory

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
    var selectedCategory by remember { mutableStateOf("Todas") }

    val categories = viewModel.allCategories

    // Filter materials based on selected category
    val filteredGeneral = if (selectedCategory == "Todas") generalMaterials else generalMaterials.filter { it.category.equals(selectedCategory, true) }
    val filteredEyes = if (selectedCategory == "Todas" || selectedCategory == "Ojos") eyeMaterials else emptyList()
    val filteredHair = if (selectedCategory == "Todas" || selectedCategory == "Pelo y cabello") hairMaterials else emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario y Catálogo", fontWeight = FontWeight.Bold) },
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
            Text("Filtrar por familia / categoría:", style = MaterialTheme.typography.labelMedium)
            
            // Horizontal scrollable filter chips for all 13 categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }

            val totalItems = filteredGeneral.size + filteredEyes.size + filteredHair.size

            if (totalItems == 0) {
                EmptyInventoryBox()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filteredGeneral.isNotEmpty()) {
                        item {
                            Text("Materiales Generales (${filteredGeneral.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        items(filteredGeneral) { mat ->
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

                    if (filteredEyes.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Ojos (${filteredEyes.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        items(filteredEyes) { eye ->
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

                    if (filteredHair.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Pelo y Cabello (${filteredHair.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        items(filteredHair) { hair ->
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

@Composable
fun EmptyInventoryBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No hay artículos en esta categoría.\nRegistra compras desde 'Nuevo movimiento'.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
