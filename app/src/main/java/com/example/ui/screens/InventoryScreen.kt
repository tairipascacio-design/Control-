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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import com.example.ui.theme.ProfitGreen

@Composable
fun InventoryScreen(
    inventory: List<InventoryItem>,
    onUpdateStock: (Long, Double, Double?) -> Unit,
    onRecordConsumption: (String, Double) -> Unit,
    onNavigateToWasteWithItem: (InventoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingItem by remember { mutableStateOf<InventoryItem?>(null) }
    var consumingItem by remember { mutableStateOf<InventoryItem?>(null) }

    val totalValuedStock = remember(inventory) { inventory.sumOf { it.totalValue } }

    Box(modifier = modifier.fillMaxSize().testTag("inventory_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = ProfitGreen)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "MERCANCÍA INVERTIDA EN ALMACÉN",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "$${formatMoney(totalValuedStock)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Este valor se suma a tu Ganancia Neta porque representa producto disponible en tu restaurante.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(inventory, key = { it.id }) { item ->
                    InventoryItemCard(
                        item = item,
                        onEdit = { editingItem = item },
                        onConsume = { consumingItem = item },
                        onWaste = { onNavigateToWasteWithItem(item) }
                    )
                }
            }
        }
    }

    editingItem?.let { item ->
        EditStockDialog(
            item = item,
            onDismiss = { editingItem = null },
            onSave = { newStock, newCost ->
                onUpdateStock(item.id, newStock, newCost)
                editingItem = null
            }
        )
    }

    consumingItem?.let { item ->
        KitchenUsageDialog(
            item = item,
            onDismiss = { consumingItem = null },
            onSave = { usedQty ->
                onRecordConsumption(item.productName, usedQty)
                consumingItem = null
            }
        )
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onEdit: () -> Unit,
    onConsume: () -> Unit,
    onWaste: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inventory_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.productName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${item.category.displayName} • ${item.destination.displayName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("$${formatMoney(item.totalValue)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = ProfitGreen)
                    Text("Valor en Almacén", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stock: ${item.currentStock} ${item.unit} (a $${formatMoney(item.unitCost)}/${item.unit})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row {
                    IconButton(onClick = onConsume, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = "Consumo en cocina", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onWaste, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Registrar merma", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar stock", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EditStockDialog(
    item: InventoryItem,
    onDismiss: () -> Unit,
    onSave: (Double, Double) -> Unit
) {
    var stockText by remember { mutableStateOf(item.currentStock.toString()) }
    var costText by remember { mutableStateOf(item.unitCost.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Ajustar Stock: ${item.productName}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock Actual (${item.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it },
                    label = { Text("Costo Unitario ($)") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(10.dp))
                    Button(onClick = {
                        val stock = stockText.toDoubleOrNull() ?: item.currentStock
                        val cost = costText.toDoubleOrNull() ?: item.unitCost
                        onSave(stock, cost)
                    }) { Text("Actualizar") }
                }
            }
        }
    }
}

@Composable
fun KitchenUsageDialog(
    item: InventoryItem,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var usageText by remember { mutableStateOf("1.0") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Usar en Cocina: ${item.productName}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Descuenta el insumo del almacén porque ya se preparó para los medios litros o salón.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = usageText,
                    onValueChange = { usageText = it },
                    label = { Text("Cantidad Utilizada (${item.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(10.dp))
                    Button(onClick = {
                        val used = usageText.toDoubleOrNull() ?: 1.0
                        onSave(used)
                    }) { Text("Descontar de Almacén") }
                }
            }
        }
    }
}
