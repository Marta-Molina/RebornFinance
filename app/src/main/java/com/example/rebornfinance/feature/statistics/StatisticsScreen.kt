package com.example.rebornfinance.feature.statistics

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.rebornfinance.core.money.MoneyUtils
import com.example.rebornfinance.core.util.PdfReportUtils
import com.example.rebornfinance.ui.theme.MutedRed
import com.example.rebornfinance.ui.theme.SageGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    onNavigateBack: () -> Unit
) {
    val stats by viewModel.statistics.collectAsState()
    val currentFilter by viewModel.selectedFilter.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas e Informes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Export PDF
                    IconButton(onClick = {
                        val pdfFile = PdfReportUtils.generateFinancialReportPdf(context, stats)
                        if (pdfFile != null) {
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Compartir informe PDF"))
                        }
                    }) {
                        Icon(Icons.Default.Info, contentDescription = "Exportar PDF")
                    }
                    // Export CSV
                    IconButton(onClick = {
                        val csv = viewModel.exportCsvData()
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_SUBJECT, "Movimientos_RebornFinance.csv")
                            putExtra(Intent.EXTRA_TEXT, csv)
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartir CSV de movimientos"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Exportar CSV")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentFilter == "THIS_MONTH",
                        onClick = { viewModel.setFilter("THIS_MONTH") },
                        label = { Text("Este mes") }
                    )
                    FilterChip(
                        selected = currentFilter == "LAST_MONTH",
                        onClick = { viewModel.setFilter("LAST_MONTH") },
                        label = { Text("Mes anterior") }
                    )
                    FilterChip(
                        selected = currentFilter == "THIS_YEAR",
                        onClick = { viewModel.setFilter("THIS_YEAR") },
                        label = { Text("Este año") }
                    )
                    FilterChip(
                        selected = currentFilter == "ALL",
                        onClick = { viewModel.setFilter("ALL") },
                        label = { Text("Todo") }
                    )
                }
            }

            item {
                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Resumen del periodo (${stats.movementCount} movimientos)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ingresos:")
                            Text(MoneyUtils.formatCents(stats.totalIncomeCents), fontWeight = FontWeight.Bold, color = SageGreen)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gastos:")
                            Text(MoneyUtils.formatCents(stats.totalExpenseCents), fontWeight = FontWeight.Bold, color = MutedRed)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Balance neto:", fontWeight = FontWeight.Bold)
                            Text(
                                text = MoneyUtils.formatCents(stats.netBalanceCents),
                                fontWeight = FontWeight.Bold,
                                color = if (stats.netBalanceCents >= 0) SageGreen else MutedRed
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Gastos por categoría",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (stats.expenseByCategory.isEmpty()) {
                item {
                    Text("No hay gastos registrados en este periodo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(stats.expenseByCategory.entries.toList()) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = entry.key, fontWeight = FontWeight.Medium)
                            Text(text = MoneyUtils.formatCents(entry.value), fontWeight = FontWeight.Bold, color = MutedRed)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
