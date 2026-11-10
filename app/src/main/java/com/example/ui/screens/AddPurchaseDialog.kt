package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DestinationArea
import com.example.data.model.ProductCategory
import com.example.data.repository.ParsedTicketResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseDialog(
    initialParsedTicket: ParsedTicketResult? = null,
    onDismiss: () -> Unit,
    onSave: (
        supplier: String,
        category: ProductCategory,
        productName: String,
        quantity: Double,
        unit: String,
        unitPrice: Double,
        destination: DestinationArea,
        isDailyPurchase: Boolean,
        ticketPhotoUri: String?,
        ticketTextNotes: String?,
        workerName: String
    ) -> Unit
) {
    var supplier by remember { mutableStateOf(initialParsedTicket?.supplier ?: "") }
    var category by remember { mutableStateOf(initialParsedTicket?.category ?: ProductCategory.CARNES) }
    var productName by remember { mutableStateOf(initialParsedTicket?.productName ?: "") }
    var quantityText by remember { mutableStateOf(if (initialParsedTicket != null && initialParsedTicket.quantity > 0) initialParsedTicket.quantity.toString() else "1.0") }
    var unit by remember { mutableStateOf(initialParsedTicket?.unit ?: "kg") }
    var unitPriceText by remember { mutableStateOf(if (initialParsedTicket != null && initialParsedTicket.unitPrice > 0) initialParsedTicket.unitPrice.toString() else "0.0") }
    var destination by remember { mutableStateOf(initialParsedTicket?.destination ?: DestinationArea.GENERAL) }
    var isDailyPurchase by remember { mutableStateOf(initialParsedTicket?.isDailyPurchase ?: false) }
    var notes by remember { mutableStateOf(initialParsedTicket?.rawExtractedNotes ?: "") }
    var workerName by remember { mutableStateOf("Trabajador de Compras") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var destinationExpanded by remember { mutableStateOf(false) }

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
                        text = "Registrar Compra / Insumo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Producto o Insumo") },
                    placeholder = { Text("ej. Carne molida, Jitomate, Envases") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_purchase_product")
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    label = { Text("Proveedor / Comercio") },
                    placeholder = { Text("ej. Carnicería San Juan, Central de Abastos") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría del Insumo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        ProductCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

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
                        placeholder = { Text("kg, pza, lt") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unitPriceText,
                        onValueChange = { unitPriceText = it },
                        label = { Text("Precio Unit.") },
                        prefix = { Text("$") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = destinationExpanded,
                    onExpandedChange = { destinationExpanded = !destinationExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = destination.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Área de Destino del Insumo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = destinationExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = destinationExpanded,
                        onDismissRequest = { destinationExpanded = false }
                    ) {
                        DestinationArea.entries.forEach { dest ->
                            DropdownMenuItem(
                                text = { Text(dest.displayName) },
                                onClick = {
                                    destination = dest
                                    destinationExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isDailyPurchase,
                        onCheckedChange = { isDailyPurchase = it }
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Es Compra Diaria (pan fresco, hielo, verdura del día)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Verificación de Ticket / Notas") },
                    placeholder = { Text("Folio de ticket, peso comprobado, etc.") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

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
                            val qty = quantityText.toDoubleOrNull() ?: 1.0
                            val price = unitPriceText.toDoubleOrNull() ?: 0.0
                            onSave(
                                supplier,
                                category,
                                productName,
                                qty,
                                unit,
                                price,
                                destination,
                                isDailyPurchase,
                                null,
                                notes,
                                workerName
                            )
                        },
                        modifier = Modifier.testTag("btn_save_purchase")
                    ) {
                        Text("Guardar en Inventario")
                    }
                }
            }
        }
    }
}
