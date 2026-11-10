package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.DeliveryOrder
import com.example.data.model.DeliveryRound
import com.example.data.model.DeliveryShift
import com.example.data.model.PaymentMethod
import com.example.ui.theme.MediosLitrosPurple
import com.example.ui.theme.ProfitGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DeliveryScreen(
    shifts: List<DeliveryShift>,
    deliveryOrders: List<DeliveryOrder>,
    onAddShiftWithOrders: (
        round: DeliveryRound,
        driverName: String,
        totalOrdersCount: Int,
        totalGuisadosCount: Int,
        ordersSummary: String,
        itemsLoaded: Int,
        itemsDelivered: Int,
        itemsReturned: Int,
        cashCollected: Double,
        depositsCollected: Double,
        notes: String,
        isClosed: Boolean,
        orders: List<DeliveryOrder>
    ) -> Unit,
    onToggleOrderStatus: (DeliveryOrder, Boolean, Boolean) -> Unit,
    onDeleteShift: (DeliveryShift) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val totalCash = shifts.sumOf { it.cashCollected }
    val totalDeposits = shifts.sumOf { it.depositsCollected }
    val totalOrdersAll = shifts.sumOf { it.totalOrdersCount }
    val totalGuisadosAll = shifts.sumOf { it.totalGuisadosCount }

    Box(modifier = modifier.fillMaxSize().testTag("delivery_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MediosLitrosPurple)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Moped,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "REPARTO • CONTROL POR PEDIDOS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "3 Vueltas (Dom: 1)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "$${formatMoney(totalCash + totalDeposits)} recaudados",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("📦 Pedidos", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                Text("$totalOrdersAll pedidos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("🍲 Guisados", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                Text("$totalGuisadosAll medios lt", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("💵 Efectivo", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                Text("$${formatMoney(totalCash)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("📱 Depósitos", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                Text("$${formatMoney(totalDeposits)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (shifts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay vueltas de reparto registradas hoy. Toca el botón + para registrar pedidos de la vuelta.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(shifts, key = { it.id }) { shift ->
                        val shiftOrders = deliveryOrders.filter { it.shiftId == shift.id }
                        DeliveryShiftCard(
                            shift = shift,
                            orders = shiftOrders,
                            onToggleOrderStatus = onToggleOrderStatus,
                            onDelete = { onDeleteShift(shift) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MediosLitrosPurple,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_add_delivery_shift")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Registrar nueva vuelta con pedidos")
        }
    }

    if (showAddDialog) {
        AddDeliveryShiftWithOrdersDialog(
            onDismiss = { showAddDialog = false },
            onSave = { round, driver, ordersCount, guisadosCount, summaryStr, loaded, delivered, returned, cash, deposits, notes, isClosed, orders ->
                onAddShiftWithOrders(
                    round, driver, ordersCount, guisadosCount, summaryStr, loaded, delivered, returned, cash, deposits, notes, isClosed, orders
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DeliveryShiftCard(
    shift: DeliveryShift,
    orders: List<DeliveryOrder>,
    onToggleOrderStatus: (DeliveryOrder, Boolean, Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM - hh:mm a", Locale.getDefault()) }
    var expandedOrders by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("delivery_card_${shift.id}"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(MediosLitrosPurple.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = shift.round.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MediosLitrosPurple
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    if (shift.isClosed) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ProfitGreen, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(2.dp))
                            Text("Liquidada", style = MaterialTheme.typography.labelSmall, color = ProfitGreen, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("En ruta...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }

                Text(
                    text = "$${formatMoney(shift.totalMoneyCollected)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ProfitGreen
                )
            }

            Spacer(Modifier.height(8.dp))

            // Orders and Guisados Hero Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${shift.totalOrdersCount} Pedidos cargados • ${shift.totalGuisadosCount} medios litros",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (shift.ordersSummary.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Desglose: ${shift.ordersSummary}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Payment Split (Cash vs Deposit)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "💵 Efectivo: $${formatMoney(shift.cashCollected)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "📱 Depósitos: $${formatMoney(shift.depositsCollected)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${shift.driverName} • ${dateFormat.format(Date(shift.dateMillis))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (orders.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clickable { expandedOrders = !expandedOrders }
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (expandedOrders) "Ocultar Pedidos" else "Ver ${orders.size} Pedidos",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                if (expandedOrders) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Expanded Order List
            AnimatedVisibility(visible = expandedOrders && orders.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider()
                    Text(
                        text = "Detalle de los pedidos individuales:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    orders.forEach { order ->
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${order.customerOrOrderTag}: ${order.guisadosCount} guisados",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (order.guisadosDetail.isNotBlank()) {
                                        Text(
                                            text = order.guisadosDetail,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "${order.paymentMethod.displayName} • $${formatMoney(order.totalAmount)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (order.paymentMethod == PaymentMethod.EFECTIVO) MaterialTheme.colorScheme.primary else ProfitGreen
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = order.isDelivered,
                                        onCheckedChange = { delivered ->
                                            onToggleOrderStatus(order, delivered, !delivered)
                                        }
                                    )
                                    Text(
                                        text = if (order.isDelivered) "Entregado" else "Pendiente",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class OrderGroupEntry(
    var orderCount: String,
    var guisadosPerOrder: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeliveryShiftWithOrdersDialog(
    onDismiss: () -> Unit,
    onSave: (
        round: DeliveryRound,
        driverName: String,
        ordersCount: Int,
        guisadosCount: Int,
        summaryStr: String,
        loaded: Int,
        delivered: Int,
        returned: Int,
        cash: Double,
        deposits: Double,
        notes: String,
        isClosed: Boolean,
        orders: List<DeliveryOrder>
    ) -> Unit
) {
    var round by remember { mutableStateOf(DeliveryRound.VUELTA_1) }
    var driverName by remember { mutableStateOf("Mario (Repartidor)") }

    // Order Builder Rows: e.g. 5 pedidos de 2 guisados, 3 pedidos de 1 guisado, 2 pedidos de 5 guisados
    val orderGroups = remember {
        mutableStateListOf(
            OrderGroupEntry("5", "2"),
            OrderGroupEntry("3", "1"),
            OrderGroupEntry("2", "5")
        )
    }

    val pricePerGuisado = 75.0

    // Computed totals
    val totalOrdersComputed = orderGroups.sumOf { it.orderCount.toIntOrNull() ?: 0 }
    val totalGuisadosComputed = orderGroups.sumOf {
        (it.orderCount.toIntOrNull() ?: 0) * (it.guisadosPerOrder.toIntOrNull() ?: 0)
    }
    val totalAmountComputed = totalGuisadosComputed * pricePerGuisado

    var cashText by remember { mutableStateOf((totalAmountComputed * 0.6).toInt().toString()) }
    var depositsText by remember { mutableStateOf((totalAmountComputed * 0.4).toInt().toString()) }
    var notes by remember { mutableStateOf("") }
    var isClosed by remember { mutableStateOf(true) }
    var roundExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Registrar Vuelta de Reparto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Text(
                    text = "Registra los pedidos que se lleva el repartidor (ej. 5 pedidos de 2 guisados, 3 de 1 guisado, etc.).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = roundExpanded,
                    onExpandedChange = { roundExpanded = !roundExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = round.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Turno / Vuelta de Reparto") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roundExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = roundExpanded,
                        onDismissRequest = { roundExpanded = false }
                    ) {
                        DeliveryRound.entries.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r.displayName) },
                                onClick = {
                                    round = r
                                    roundExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Order Groups Builder Card
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📦 Desglose por Pedido",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MediosLitrosPurple
                            )

                            IconButton(
                                onClick = { orderGroups.add(OrderGroupEntry("1", "1")) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.PlaylistAdd, contentDescription = "Agregar fila", tint = MediosLitrosPurple)
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        orderGroups.forEachIndexed { index, group ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = group.orderCount,
                                    onValueChange = { group.orderCount = it },
                                    label = { Text("Pedidos") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                Text("de", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

                                OutlinedTextField(
                                    value = group.guisadosPerOrder,
                                    onValueChange = { group.guisadosPerOrder = it },
                                    label = { Text("Guisados") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                Text("c/u", style = MaterialTheme.typography.bodySmall)

                                if (orderGroups.size > 1) {
                                    IconButton(
                                        onClick = { orderGroups.removeAt(index) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Quitar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MediosLitrosPurple.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Total a cargar: $totalOrdersComputed pedidos ($totalGuisadosComputed medios litros)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MediosLitrosPurple
                                )
                                Text(
                                    text = "Cobro estimado a $75 c/u: $${formatMoney(totalAmountComputed)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MediosLitrosPurple
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Payment collected inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = cashText,
                        onValueChange = { cashText = it },
                        label = { Text("💵 Efectivo cobrado") },
                        prefix = { Text("$") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = depositsText,
                        onValueChange = { depositsText = it },
                        label = { Text("📱 Depósito / Transf.") },
                        prefix = { Text("$") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = isClosed, onCheckedChange = { isClosed = it })
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Liquidar vuelta ahora (sumar cobros a ventas)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val cash = cashText.toDoubleOrNull() ?: 0.0
                            val deposits = depositsText.toDoubleOrNull() ?: 0.0

                            // Build summary string like "5 pedidos de 2 guisados • 3 pedidos de 1 guisado"
                            val summaryStr = orderGroups
                                .filter { (it.orderCount.toIntOrNull() ?: 0) > 0 }
                                .joinToString(" • ") { "${it.orderCount} pedidos de ${it.guisadosPerOrder} guisados" }

                            // Generate individual order items
                            val generatedOrders = mutableListOf<DeliveryOrder>()
                            var orderIndex = 1
                            orderGroups.forEach { group ->
                                val count = group.orderCount.toIntOrNull() ?: 0
                                val guisados = group.guisadosPerOrder.toIntOrNull() ?: 1
                                for (i in 1..count) {
                                    val isDeposit = orderIndex % 3 == 0 // simulate deposit split
                                    val amt = guisados * pricePerGuisado
                                    generatedOrders.add(
                                        DeliveryOrder(
                                            customerOrOrderTag = "Pedido #$orderIndex",
                                            guisadosCount = guisados,
                                            guisadosDetail = "$guisados medios litros",
                                            pricePerGuisado = pricePerGuisado,
                                            totalAmount = amt,
                                            paymentMethod = if (isDeposit) PaymentMethod.DEPOSITO else PaymentMethod.EFECTIVO,
                                            isDelivered = true
                                        )
                                    )
                                    orderIndex++
                                }
                            }

                            onSave(
                                round,
                                driverName,
                                totalOrdersComputed,
                                totalGuisadosComputed,
                                summaryStr,
                                totalGuisadosComputed,
                                totalGuisadosComputed,
                                0,
                                cash,
                                deposits,
                                notes,
                                isClosed,
                                generatedOrders
                            )
                        },
                        modifier = Modifier.testTag("btn_save_delivery_shift")
                    ) {
                        Text("Guardar Vuelta")
                    }
                }
            }
        }
    }
}
