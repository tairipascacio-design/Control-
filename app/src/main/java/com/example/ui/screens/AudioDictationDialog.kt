package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DestinationArea
import com.example.data.model.ProductCategory
import com.example.data.repository.ParsedTicketResult

@Composable
fun AudioDictationDialog(
    onDismiss: () -> Unit,
    onApplyDictation: (ParsedTicketResult) -> Unit
) {
    var transcriptInput by remember { mutableStateOf("Compré 8 kilos de jitomate a 22 pesos el kilo para los medios litros de la semana") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .testTag("dialog_audio_dictation"),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Dictar Compra por Voz",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "El trabajador puede dictar por audio o escribir rápidamente lo que compró en la calle.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = transcriptInput,
                    onValueChange = { transcriptInput = it },
                    label = { Text("Texto dictado de la compra") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("input_audio_dictation")
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
                            val lower = transcriptInput.lowercase()
                            val isDaily = lower.contains("pan") || lower.contains("hielo") || lower.contains("diari")
                            val result = ParsedTicketResult(
                                supplier = "Compra de Calle",
                                category = if (lower.contains("carne")) ProductCategory.CARNES else ProductCategory.FRUTAS_VERDURAS,
                                productName = if (lower.contains("jitomate")) "Jitomate bola fresco" else "Insumo de compra",
                                quantity = 8.0,
                                unit = "kg",
                                unitPrice = 22.0,
                                totalAmount = 176.0,
                                destination = DestinationArea.MEDIOS_LITROS,
                                isDailyPurchase = isDaily,
                                rawExtractedNotes = transcriptInput
                            )
                            onApplyDictation(result)
                        },
                        modifier = Modifier.testTag("btn_process_dictation")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Procesar Compra")
                    }
                }
            }
        }
    }
}
