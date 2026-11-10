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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.data.model.InventoryItem
import com.example.data.model.PaymentMethod
import com.example.data.model.ProductCategory
import com.example.data.model.WasteReason
import com.example.data.model.WasteRecord
import com.example.ui.theme.ExpenseRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    expenses: List<Expense>,
    wasteRecords: List<WasteRecord>,
    inventory: List<InventoryItem>,
    onAddExpense: (ExpenseCategory, String, Double, PaymentMethod, String?) -> Unit,
    onDeleteExpense: (Expense) -> Unit,
    onAddWaste: (String, ProductCategory, Double, String, Double, WasteReason, String) -> Unit,
    onDeleteWaste: (WasteRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showAddWasteDialog by remember { mutableStateOf(false) }

    val totalExpenses = remember(expenses) { expenses.sumOf { it.amount } }
    val totalWasteCost = remember(wasteRecords) { wasteRecords.sumOf { it.estimatedCost } }

    Box(modifier = modifier.fillMaxSize().testTag("expenses_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = ExpenseRed)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("EGRESOS FIJOS & CONTROL DE MERMAS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
                    Spacer(Modifier.height(8.dp))
                    Text("$${formatMoney(totalExpenses + totalWasteCost)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f).background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Column {
                                Text("Salidas Fijas (Renta/Luz/Gas)", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                Text("$${formatMoney(totalExpenses)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Column {
                                Text("Mermas / Sobrantes", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                Text("$${formatMoney(totalWasteCost)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            PrimaryTabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Egresos Fijos (${expenses.size})") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Mermas Registradas (${wasteRecords.size})") })
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedTab == 0) {
                    if (expenses.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("No hay egresos fijos registrados.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(expenses, key = { it.id }) { exp ->
                            ExpenseItemCard(expense = exp, onDelete = { onDeleteExpense(exp) })
                        }
                    }
                } else {
                    if (wasteRecords.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("No hay mermas registradas. ¡Excelente control de cocina!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(wasteRecords, key = { it.id }) { waste ->
                            WasteItemCard(waste = waste, onDelete = { onDeleteWaste(waste) })
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (selectedTab == 0) showAddExpenseDialog = true else showAddWasteDialog = true
            },
            containerColor = ExpenseRed,
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).testTag("fab_add_expense_or_waste")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Registrar egreso o merma")
        }
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onSave = { cat, desc, amt, pay ->
                onAddExpense(cat, desc, amt, pay, null)
                showAddExpenseDialog = false
            }
        )
    }

    if (showAddWasteDialog) {
        AddWasteDialog(
            inventory = inventory,
            onDismiss = { showAddWasteDialog = false },
            onSave = { prod, cat, qty, unit, cost, reason, notes ->
                onAddWaste(prod, cat, qty, unit, cost, reason, notes)
                showAddWasteDialog = false
            }
        )
    }
}

@Composable
fun ExpenseItemCard(expense: Expense, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("expense_card_${expense.id}"),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.category.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (expense.description.isNotBlank()) {
                    Text(expense.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("-$${formatMoney(expense.amount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ExpenseRed)
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun WasteItemCard(waste: WasteRecord, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("waste_card_${waste.id}"),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(waste.productName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${waste.quantity} ${waste.unit} • Motivo: ${waste.reason.displayName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("-$${formatMoney(waste.estimatedCost)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (ExpenseCategory, String, Double, PaymentMethod) -> Unit
) {
    var category by remember { mutableStateOf(ExpenseCategory.GAS) }
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("800.0") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.EFECTIVO) }
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth(0.95f).clip(RoundedCornerShape(20.dp)), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Registrar Egreso Fijo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = category.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rubro de Salida") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        ExpenseCategory.entries.forEach { c ->
                            DropdownMenuItem(text = { Text(c.displayName) }, onClick = { category = c; expanded = false })
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Detalle / Observaciones") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto Pagado") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(10.dp))
                    Button(onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        onSave(category, description, amt, paymentMethod)
                    }) { Text("Guardar Egreso") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWasteDialog(
    inventory: List<InventoryItem>,
    onDismiss: () -> Unit,
    onSave: (String, ProductCategory, Double, String, Double, WasteReason, String) -> Unit
) {
    var productName by remember { mutableStateOf(if (inventory.isNotEmpty()) inventory[0].productName else "Tomate") }
    var quantityText by remember { mutableStateOf("1.0") }
    var unit by remember { mutableStateOf(if (inventory.isNotEmpty()) inventory[0].unit else "kg") }
    var costText by remember { mutableStateOf("25.0") }
    var reason by remember { mutableStateOf(WasteReason.SOBRANTE_NO_VENDIDO) }
    var notes by remember { mutableStateOf("") }
    var expandedReason by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth(0.95f).clip(RoundedCornerShape(20.dp)), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Registrar Merma o Sobrante", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Se descontará del inventario para mantener el cálculo del neto exacto.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Insumo / Producto") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = quantityText, onValueChange = { quantityText = it }, label = { Text("Cantidad") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unidad") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = costText, onValueChange = { costText = it }, label = { Text("Costo Est.") }, prefix = { Text("$") }, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(expanded = expandedReason, onExpandedChange = { expandedReason = !expandedReason }) {
                    OutlinedTextField(
                        value = reason.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Motivo de Merma") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReason) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expandedReason, onDismissRequest = { expandedReason = false }) {
                        WasteReason.entries.forEach { r ->
                            DropdownMenuItem(text = { Text(r.displayName) }, onClick = { reason = r; expandedReason = false })
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(10.dp))
                    Button(onClick = {
                        val qty = quantityText.toDoubleOrNull() ?: 1.0
                        val cost = costText.toDoubleOrNull() ?: 0.0
                        onSave(productName, ProductCategory.OTROS, qty, unit, cost, reason, notes)
                    }) { Text("Registrar Merma") }
                }
            }
        }
    }
}
