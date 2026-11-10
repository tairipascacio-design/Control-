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
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TakeoutDining
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Sale
import com.example.data.model.SalesChannel
import com.example.ui.theme.MediosLitrosPurple
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SalongBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SalesScreen(
    sales: List<Sale>,
    onAddSale: (channel: SalesChannel, productName: String, quantity: Double, unitPrice: Double, notes: String) -> Unit,
    onDeleteSale: (Sale) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedChannelTab by remember { mutableIntStateOf(0) } // 0: Todas, 1: Medios Litros, 2: Restaurante
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredSales = sales.filter { sale ->
        when (selectedChannelTab) {
            1 -> sale.channel == SalesChannel.MEDIOS_LITROS
            2 -> sale.channel == SalesChannel.RESTAURANTE
            else -> true
        }
    }

    val totalAmount = filteredSales.sumOf { it.totalAmount }
    val totalQty = filteredSales.sumOf { it.quantity }

    Box(modifier = modifier.fillMaxSize().testTag("sales_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tabs: Todas vs Medios Litros vs Restaurante
            TabRow(selectedTabIndex = selectedChannelTab) {
                Tab(
                    selected = selectedChannelTab == 0,
                    onClick = { selectedChannelTab = 0 },
                    text = { Text("Todas (${sales.size})", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedChannelTab == 1,
                    onClick = { selectedChannelTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TakeoutDining, contentDescription = null, modifier = Modifier.size(16.dp), tint = MediosLitrosPurple)
                            Spacer(Modifier.width(4.dp))
                            Text("Medios Litros", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
                Tab(
                    selected = selectedChannelTab == 2,
                    onClick = { selectedChannelTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp), tint = SalongBlue)
                            Spacer(Modifier.width(4.dp))
                            Text("Restaurante", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }

            // Total Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredSales.size} ventas ($totalQty piezas)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total: $${formatMoney(totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ProfitGreen
                )
            }

            // Sales List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredSales.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay ventas registradas en este canal.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredSales, key = { it.id }) { sale ->
                        SaleItemCard(sale = sale, onDelete = { onDeleteSale(sale) })
                    }
                }
            }
        }

        // Add Sale FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = ProfitGreen,
            contentColor = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_add_sale")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Registrar nueva venta")
        }
    }

    if (showAddDialog) {
        AddSaleDialog(
            initialChannel = if (selectedChannelTab == 1) SalesChannel.MEDIOS_LITROS else SalesChannel.RESTAURANTE,
            onDismiss = { showAddDialog = false },
            onSave = { channel, prod, qty, price, notes ->
                onAddSale(channel, prod, qty, price, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SaleItemCard(
    sale: Sale,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM - hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sale_card_${sale.id}"),
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
                        text = sale.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${sale.quantity.toInt()} pzas a $${formatMoney(sale.unitPrice)} c/u",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "$${formatMoney(sale.totalAmount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ProfitGreen
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Channel Badge
                val isMedioLitro = sale.channel == SalesChannel.MEDIOS_LITROS
                val channelColor = if (isMedioLitro) MediosLitrosPurple else SalongBlue
                val channelIcon = if (isMedioLitro) Icons.Default.TakeoutDining else Icons.Default.Restaurant

                Row(
                    modifier = Modifier
                        .background(channelColor.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(channelIcon, contentDescription = null, tint = channelColor, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = sale.channel.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = channelColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateFormat.format(Date(sale.dateMillis)),
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
                            contentDescription = "Eliminar venta",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (sale.notes.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Nota: ${sale.notes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddSaleDialog(
    initialChannel: SalesChannel,
    onDismiss: () -> Unit,
    onSave: (SalesChannel, String, Double, Double, String) -> Unit
) {
    var channel by remember { mutableStateOf(initialChannel) }
    var productName by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1.0") }
    var unitPriceText by remember { mutableStateOf("75.0") }
    var notes by remember { mutableStateOf("") }

    val qty = quantityText.toDoubleOrNull() ?: 1.0
    val price = unitPriceText.toDoubleOrNull() ?: 0.0
    val total = qty * price

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
                        text = "Registrar Venta",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Channel Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isML = channel == SalesChannel.MEDIOS_LITROS
                    Button(
                        onClick = {
                            channel = SalesChannel.MEDIOS_LITROS
                            if (productName.isBlank()) productName = "Medio litro de "
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = if (isML) MediosLitrosPurple else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isML) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.TakeoutDining, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Medios Litros", maxLines = 1)
                    }

                    val isRest = channel == SalesChannel.RESTAURANTE
                    Button(
                        onClick = {
                            channel = SalesChannel.RESTAURANTE
                            if (productName == "Medio litro de ") productName = ""
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = if (isRest) SalongBlue else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isRest) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Restaurante Salón", maxLines = 1)
                    }
                }

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Platillo / Comida vendida") },
                    placeholder = { Text(if (channel == SalesChannel.MEDIOS_LITROS) "Ej: Medio litro de Pozole" else "Ej: Comida corrida salón") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        value = unitPriceText,
                        onValueChange = { unitPriceText = it },
                        label = { Text("Precio Venta") },
                        prefix = { Text("$") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Calculated Total
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ProfitGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Venta:", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "$${formatMoney(total)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = ProfitGreen
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas (opcional)") },
                    placeholder = { Text("Mesa, cliente o detalle de entrega") },
                    singleLine = true,
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
                            if (productName.isNotBlank() && qty > 0 && price > 0) {
                                onSave(channel, productName.trim(), qty, price, notes.trim())
                            }
                        },
                        enabled = productName.isNotBlank() && qty > 0 && price > 0
                    ) {
                        Text("Guardar Venta")
                    }
                }
            }
        }
    }
}
