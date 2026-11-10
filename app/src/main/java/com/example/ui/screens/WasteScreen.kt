package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.InventoryItem
import com.example.data.model.ProductCategory
import com.example.data.model.WasteReason
import com.example.data.model.WasteRecord
import com.example.ui.theme.LossRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WasteScreen(
    wasteRecords: List<WasteRecord>,
    inventory: List<InventoryItem>,
    prefilledInventoryItem: InventoryItem? = null,
    onAddWaste: (
        productName: String,
        category: ProductCategory,
        quantity: Double,
        unit: String,
        estimatedCost: Double,
        reason: WasteReason,
        notes: String
    ) -> Unit,
    onDeleteWaste: (WasteRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(prefilledInventoryItem != null) }
    val totalWasteCost = wasteRecords.sumOf { it.estimatedCost }

    Box(modifier = modifier.fillMaxSize().testTag("waste_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Waste Hero Banner
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFFFFF0EE)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = LossRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "COSTO TOTAL DE MERMAS Y DESPERDICIOS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = LossRed.copy(alpha = 0.85f)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "-$${formatMoney(totalWasteCost)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = LossRed
                        )
                        Text(
                            text = "Se descuenta directamente del stock de inventario",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Waste Records List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (wasteRecords.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "¡Excelente! No hay mermas registradas recientemente.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(wasteRecords, key = { it.id }) { waste ->
                        WasteItemCard(waste = waste, onDelete = { onDeleteWaste(waste) })
                    }
                }
            }
        }

        // Add Waste FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = LossRed,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_add_waste")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Registrar nueva merma")
        }
    }

    if (showAddDialog) {
        AddWasteDialog(
            inventory = inventory,
            prefilledItem = prefilledInventoryItem,
            onDismiss = { showAddDialog = false },
            onSave = { prod, cat, qty, u, cost, reason, notes ->
                onAddWaste(prod, cat, qty, u, cost, reason, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun WasteItemCard(
    waste: WasteRecord,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM - hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("waste_card_${waste.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = waste.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${waste.quantity} ${waste.unit} desperdiciados",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "-$${formatMoney(waste.estimatedCost)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = LossRed
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reason Badge
                Box(
                    modifier = Modifier
                        .background(LossRed.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = waste.reason.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = LossRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateFormat.format(Date(waste.dateMillis)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar registro de merma",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (waste.notes.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Detalle: ${waste.notes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWasteDialog(
    inventory: List<InventoryItem>,
    prefilledItem: InventoryItem?,
    onDismiss: () -> Unit,
    onSave: (String, ProductCategory, Double, String, Double, WasteReason, String) -> Unit
) {
    var productName by remember { mutableStateOf(prefilledItem?.productName ?: "") }
    var category by remember { mutableStateOf(prefilledItem?.category ?: ProductCategory.FRUTAS_VERDURAS) }
    var quantityText by remember { mutableStateOf("1.0") }
    var unit by remember { mutableStateOf(prefilledItem?.unit ?: "kg") }
    var unitCostText by remember { mutableStateOf(prefilledItem?.unitCost?.toString() ?: "25.0") }
    var reason by remember { mutableStateOf(WasteReason.CADUCIDAD) }
    var notes by remember { mutableStateOf("") }

    var reasonExpanded by remember { mutableStateOf(false) }

    val qty = quantityText.toDoubleOrNull() ?: 1.0
    val cost = unitCostText.toDoubleOrNull() ?: 0.0
    val totalEstimatedCost = qty * cost

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Registrar Merma / Desperdicio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Text(
                    text = "Se descontará la cantidad del stock en inventario y se sumará a las pérdidas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Producto o Insumo merma *") },
                    placeholder = { Text("Ej: Tomate Huaje, Arroz, Carne") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                // Quantity & Unit & Cost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unidad") },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )

                    OutlinedTextField(
                        value = unitCostText,
                        onValueChange = { unitCostText = it },
                        label = { Text("Costo Unit.") },
                        prefix = { Text("$") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1.1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Reason Dropdown
                ExposedDropdownMenuBox(
                    expanded = reasonExpanded,
                    onExpandedChange = { reasonExpanded = !reasonExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = reason.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Motivo del Desperdicio") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reasonExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = reasonExpanded,
                        onDismissRequest = { reasonExpanded = false }
                    ) {
                        WasteReason.entries.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r.displayName) },
                                onClick = {
                                    reason = r
                                    reasonExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Cost banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LossRed.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pérdida Económica:", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "-$${formatMoney(totalEstimatedCost)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = LossRed
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observaciones / Explicación") },
                    placeholder = { Text("Ej: Se quemó en preparación o caducó por calor") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (productName.isNotBlank() && qty > 0) {
                                onSave(productName.trim(), category, qty, unit.trim(), totalEstimatedCost, reason, notes.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                        enabled = productName.isNotBlank() && qty > 0
                    ) {
                        Text("Registrar Merma")
                    }
                }
            }
        }
    }
}
