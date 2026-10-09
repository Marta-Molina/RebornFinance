package com.example.rebornfinance.feature.projects

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
import com.example.rebornfinance.domain.model.ProjectStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormScreen(
    viewModel: ProjectFormViewModel,
    onNavigateBack: () -> Unit
) {
    val name by viewModel.name.collectAsState()
    val kitName by viewModel.kitName.collectAsState()
    val sculptorName by viewModel.sculptorName.collectAsState()
    val sizeInchesStr by viewModel.sizeInchesStr.collectAsState()
    val status by viewModel.status.collectAsState()
    val predictedPriceStr by viewModel.predictedPriceStr.collectAsState()
    val notes by viewModel.notes.collectAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedStatusDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditMode) "Editar proyecto reborn" else "Nuevo proyecto reborn", fontWeight = FontWeight.Bold) },
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
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.name.value = it },
                label = { Text("Nombre del proyecto *") },
                placeholder = { Text("Ej: Lucía") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = kitName,
                onValueChange = { viewModel.kitName.value = it },
                label = { Text("Nombre del kit") },
                placeholder = { Text("Ej: June Awake Real Effect") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = sculptorName,
                onValueChange = { viewModel.sculptorName.value = it },
                label = { Text("Escultora") },
                placeholder = { Text("Ej: Elisa Marx") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = sizeInchesStr,
                onValueChange = { viewModel.sizeInchesStr.value = it },
                label = { Text("Tamaño en pulgadas") },
                placeholder = { Text("Ej: 20") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Status Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedStatusDropdown,
                onExpandedChange = { expandedStatusDropdown = !expandedStatusDropdown }
            ) {
                OutlinedTextField(
                    value = status.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatusDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedStatusDropdown,
                    onDismissRequest = { expandedStatusDropdown = false }
                ) {
                    ProjectStatus.values().forEach { st ->
                        DropdownMenuItem(
                            text = { Text(st.displayName) },
                            onClick = {
                                viewModel.status.value = st
                                expandedStatusDropdown = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = predictedPriceStr,
                onValueChange = { viewModel.predictedPriceStr.value = it },
                label = { Text("Precio de venta previsto (€)") },
                placeholder = { Text("Ej: 450,00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

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
                    viewModel.saveProject(
                        onSuccess = onNavigateBack,
                        onError = { err -> errorMessage = err }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar proyecto", style = MaterialTheme.typography.titleLarge, fontSize = 16.sp)
            }
        }
    }
}
