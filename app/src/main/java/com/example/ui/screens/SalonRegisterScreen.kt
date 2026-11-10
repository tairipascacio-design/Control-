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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
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
import com.example.data.model.PaymentMethod
import com.example.data.model.Sale
import com.example.data.model.SalesChannel
import com.example.ui.theme.CardBlue
import com.example.ui.theme.CashAmber
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SalonOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SalonRegisterScreen(
    sales: List<Sale>,
    onAddSalonSale: (paymentMethod: PaymentMethod, dish: String, quantity: Double, price: Double, notes: String) -> Unit,
    onDeleteSale: (Sale) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showCutDialog by remember { mutableStateOf(false) }

    val salonSales = remember(sales) { sales.filter { it.channel == SalesChannel.RESTAURANTE } }
    val cashTotal = remember(salonSales) { salonSales.filter { it.paymentMethod == PaymentMethod.EFECTIVO }.sumOf { it.totalAmount } }
    val cardTotal = remember(salonSales) { salonSales.filter { it.paymentMethod == PaymentMethod.TARJETA }.sumOf { it.totalAmount } }
    val depositTotal = remember(salonSales) { salonSales.filter { it.paymentMethod == PaymentMethod.DEPOSITO }.sumOf { it.totalAmount } }
    val totalSalon = cashTotal + cardTotal + depositTotal

    Box(modifier = modifier.fillMaxSize().testTag("salon_register_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = SalonOrange)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TabletAndroid, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "CAJA REGISTRADORA • TABLET SALÓN",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Button(
                            onClick = { showCutDialog = true },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Corte Caja", color = SalonOrange, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "$${formatMoney(totalSalon)} en Salón",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(10.dp))

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
                                Text("💵 Efectivo Gaveta", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                Text("$${formatMoney(cashTotal)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("💳 Tarjeta TPV", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                Text("$${formatMoney(cardTotal)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("📱 Transferencia", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                Text("$${formatMoney(depositTotal)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (salonSales.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay cobros de salón registrados hoy.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(salonSales, key = { it.id }) { sale ->
                        SalonSaleCard(sale = sale, onDelete = { onDeleteSale(sale) })
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = SalonOrange,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_add_salon_sale")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Cobrar consumo en mesa")
        }
    }

    if (showAddDialog) {
        AddSalonSaleDialog(
            onDismiss = { showAddDialog = false },
            onSave = { method, dish, qty, price, notes ->
                onAddSalonSale(method, dish, qty, price, notes)
                showAddDialog = false
            }
        )
    }

    if (showCutDialog) {
        CashCutDialog(
            cashTotal = cashTotal,
            cardTotal = cardTotal,
            depositTotal = depositTotal,
            total = totalSalon,
            onDismiss = { showCutDialog = false }
        )
    }
}

@Composable
fun SalonSaleCard(
    sale: Sale,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("salon_sale_card_${sale.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sale.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                when (sale.paymentMethod) {
                                    PaymentMethod.EFECTIVO -> CashAmber.copy(alpha = 0.15f)
                                    PaymentMethod.TARJETA -> CardBlue.copy(alpha = 0.15f)
                                    PaymentMethod.DEPOSITO -> ProfitGreen.copy(alpha = 0.15f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = sale.paymentMethod.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (sale.paymentMethod) {
                                PaymentMethod.EFECTIVO -> CashAmber
                                PaymentMethod.TARJETA -> CardBlue
                                PaymentMethod.DEPOSITO -> ProfitGreen
                            }
                        )
                    }
                }

                Text(
                    text = "${sale.quantity.toInt()} pzas • ${dateFormat.format(Date(sale.dateMillis))} ${if (sale.notes.isNotBlank()) "• ${sale.notes}" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$${formatMoney(sale.totalAmount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun AddSalonSaleDialog(
    onDismiss: () -> Unit,
    onSave: (PaymentMethod, String, Double, Double, String) -> Unit
) {
    var dishName by remember { mutableStateOf("Comida Corrida / Plato Fuerte") }
    var quantityText by remember { mutableStateOf("1") }
    var unitPriceText by remember { mutableStateOf("95.0") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.EFECTIVO) }
    var notes by remember { mutableStateOf("Mesa 1") }
    var cashGivenText by remember { mutableStateOf("200.0") }

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
                        text = "Cobro en Caja Registradora",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = dishName,
                    onValueChange = { dishName = it },
                    label = { Text("Platillo / Consumo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unitPriceText,
                        onValueChange = { unitPriceText = it },
                        label = { Text("Precio c/u") },
                        prefix = { Text("$") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Mesa/Detalle") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text("Método de Pago en Salón:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PaymentMethod.entries.forEach { method ->
                        val selected = paymentMethod == method
                        OutlinedButton(
                            onClick = { paymentMethod = method },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = if (selected) androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(
                                text = when (method) {
                                    PaymentMethod.EFECTIVO -> "💵 Efectivo"
                                    PaymentMethod.TARJETA -> "💳 Tarjeta"
                                    PaymentMethod.DEPOSITO -> "📱 Transf."
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                if (paymentMethod == PaymentMethod.EFECTIVO) {
                    Spacer(Modifier.height(10.dp))
                    val total = (quantityText.toDoubleOrNull() ?: 1.0) * (unitPriceText.toDoubleOrNull() ?: 0.0)
                    val given = cashGivenText.toDoubleOrNull() ?: total
                    val change = (given - total).coerceAtLeast(0.0)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = cashGivenText,
                            onValueChange = { cashGivenText = it },
                            label = { Text("Paga con") },
                            prefix = { Text("$") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(ProfitGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Cambio a regresar:", style = MaterialTheme.typography.labelSmall)
                                Text("$${formatMoney(change)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = ProfitGreen)
                            }
                        }
                    }
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
                            val qty = quantityText.toDoubleOrNull() ?: 1.0
                            val price = unitPriceText.toDoubleOrNull() ?: 0.0
                            onSave(paymentMethod, dishName, qty, price, notes)
                        },
                        modifier = Modifier.testTag("btn_confirm_salon_sale")
                    ) {
                        Text("Cobrar Consumo")
                    }
                }
            }
        }
    }
}

@Composable
fun CashCutDialog(
    cashTotal: Double,
    cardTotal: Double,
    depositTotal: Double,
    total: Double,
    onDismiss: () -> Unit
) {
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
                    Text("Corte de Caja de Salón", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Cerrar") }
                }

                Spacer(Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("💵 Total Efectivo en Gaveta: $${formatMoney(cashTotal)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("💳 Total Terminal Tarjeta TPV: $${formatMoney(cardTotal)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("📱 Total Depósitos/Transferencias: $${formatMoney(depositTotal)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("TOTAL VENDIDO EN SALÓN: $${formatMoney(total)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = SalonOrange)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Aceptar")
                }
            }
        }
    }
}
