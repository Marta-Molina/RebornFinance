package com.example.rebornfinance.feature.smartinput

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rebornfinance.core.money.MoneyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartInputScreen(
    viewModel: SmartInputViewModel,
    onNavigateBack: () -> Unit
) {
    val inputText by viewModel.inputText.collectAsState()
    val proposal by viewModel.proposal.collectAsState()
    val status by viewModel.statusMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entrada inteligente (Lenguaje Natural)", fontWeight = FontWeight.Bold) },
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Escribe una operación en lenguaje natural",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Ejemplos: \"He gastado 35 euros en silicona\", \"Ayer vendí el reborn Lucía por 450 euros\", \"Me han devuelto 12,50 euros\".",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.inputText.value = it },
                        label = { Text("Frase") },
                        placeholder = { Text("Ej: He gastado 25 euros en pinturas...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Button(
                        onClick = { viewModel.analyzeInput() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Analizar frase")
                    }
                }
            }

            if (status != null) {
                Text(
                    text = status!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (proposal != null) {
                val prop = proposal!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Vista previa detectada",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(text = "Tipo: ${prop.operationType}")
                        Text(text = "Descripción: ${prop.description}")
                        Text(text = "Importe: ${prop.amountCents?.let { MoneyUtils.formatCents(it) } ?: "No detectado"}")
                        Text(text = "Categoría: ${prop.category}")
                        if (prop.projectName != null) {
                            Text(text = "Proyecto relacionado: ${prop.projectName}")
                        }
                        if (prop.explanation != null) {
                            Text(
                                text = prop.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.cancelProposal() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancelar")
                            }
                            Button(
                                onClick = {
                                    viewModel.confirmAndSave(onSuccess = onNavigateBack)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}
