package com.example.rebornfinance.feature.inventory

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
    initialCategory: String? = null,
    initialSubCategory: String? = null,
    onNavigateBack: () -> Unit
) {
    val generalMaterials by viewModel.generalMaterials.collectAsState()
    val eyeMaterials by viewModel.eyeMaterials.collectAsState()
    val hairMaterials by viewModel.hairMaterials.collectAsState()

    var selectedCategory by remember { mutableStateOf(initialCategory ?: "Todas") }
    var selectedSubCategory by remember { mutableStateOf(initialSubCategory) }

    val catalog = viewModel.catalog
    val categories = listOf("Todas") + catalog.keys.toList()
    val subCategories = catalog[selectedCategory] ?: emptyList()

    val filteredGeneral = generalMaterials.filter { mat ->
        val catMatch = selectedCategory == "Todas" || mat.category.equals(selectedCategory, true)
        val subMatch = selectedSubCategory == null || mat.subCategory.equals(selectedSubCategory, true)
        catMatch && subMatch
    }

    val filteredEyes = eyeMaterials.filter { eye ->
        val catMatch = selectedCategory == "Todas" || selectedCategory == "Ojos"
        val subMatch = selectedSubCategory == null || eye.subCategory.equals(selectedSubCategory, true)
        catMatch && subMatch
    }

    val filteredHair = hairMaterials.filter { hair ->
        val catMatch = selectedCategory == "Todas" || selectedCategory == "Pelo y cabello"
        val subMatch = selectedSubCategory == null || hair.subCategory.equals(selectedSubCategory, true)
        catMatch && subMatch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedSubCategory != null) "$selectedCategory > $selectedSubCategory" else if (selectedCategory != "Todas") selectedCategory else "Inventario y Catálogo", fontWeight = FontWeight.Bold) },
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
            Text("Categorías principales:", style = MaterialTheme.typography.labelMedium)

            // Horizontal scrollable categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = cat
                            selectedSubCategory = null
                        },
                        label = { Text(cat) }
                    )
                }
            }

            // Subcategories if category selected
            if (subCategories.isNotEmpty()) {
                Text("Subcategorías de $selectedCategory:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSubCategory == null,
                        onClick = { selectedSubCategory = null },
                        label = { Text("Todas") }
                    )
                    subCategories.forEach { sub ->
                        FilterChip(
                            selected = selectedSubCategory == sub,
                            onClick = { selectedSubCategory = sub },
                            label = { Text(sub) }
                        )
                    }
                }
            }

            val totalItems = filteredGeneral.size + filteredEyes.size + filteredHair.size

            if (totalItems == 0) {
                EmptyInventoryBox()
            } else {
                // 2 cards per row using LazyVerticalGrid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredGeneral) { mat ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = mat.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Cat: ${mat.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (mat.subCategory != null) {
                                    Text(text = "Sub: ${mat.subCategory}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Stock: ${mat.availableQuantity / 1000.0} ${mat.unit}", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    items(filteredEyes) { eye ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = eye.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Color: ${eye.color}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Tipo: ${eye.type}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Modalidad: ${eye.modality.displayName}", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    items(filteredHair) { hair ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = hair.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Tipo: ${hair.hairType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Color: ${hair.color}", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
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
            text = "No hay artículos en esta selección.\nRegistra compras desde 'Nuevo movimiento'.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
