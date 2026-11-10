package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.repository.FinancialSummary
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.CardBlue
import com.example.ui.theme.CashAmber
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.MediosLitrosPurple
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SalonOrange

@Composable
fun DashboardScreen(
    summary: FinancialSummary,
    isSpeaking: Boolean,
    onSpeakSummary: () -> Unit,
    onStopSpeaking: () -> Unit,
    onNavigateTo: (AppScreen) -> Unit,
    onOpenAddPurchase: () -> Unit,
    onOpenAddSale: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            NetProfitHeroCard(
                netProfit = summary.netProfit,
                operatingMarginPct = summary.operatingMarginPct,
                profitabilityStatus = summary.profitabilityStatus,
                statusRecommendation = summary.statusRecommendation
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedButton(
                    onClick = { if (isSpeaking) onStopSpeaking() else onSpeakSummary() },
                    modifier = Modifier.weight(1f).testTag("btn_dashboard_tts"),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = if (isSpeaking) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isSpeaking) "Detener Voz" else "Escuchar Resumen",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onOpenAddPurchase,
                    modifier = Modifier.weight(1f).testTag("btn_dashboard_add_purchase"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Nueva Compra", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        item {
            Text(
                text = "Ingresos por Canal de Venta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ChannelMetricBox(
                    title = "REPARTO (MEDIOS LITROS)",
                    amount = summary.salesMediosLitros,
                    subtitle = "3 vueltas/día (Dom: 1)",
                    containerColor = MediosLitrosPurple,
                    icon = Icons.Default.Moped,
                    modifier = Modifier.weight(1f).testTag("kpi_sales_medios_litros")
                )

                ChannelMetricBox(
                    title = "SALÓN (RESTAURANTE)",
                    amount = summary.salesRestaurante,
                    subtitle = "Caja registradora tablet",
                    containerColor = SalonOrange,
                    icon = Icons.Default.Restaurant,
                    modifier = Modifier.weight(1f).testTag("kpi_sales_salon")
                )
            }
        }

        item {
            Text(
                text = "Cobros por Método de Pago",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FinancialKpiCard(
                    title = "Efectivo",
                    amount = summary.totalCash,
                    subtitle = "En gaveta / reparto",
                    accentColor = CashAmber,
                    icon = Icons.Default.Payments,
                    modifier = Modifier.weight(1f)
                )

                FinancialKpiCard(
                    title = "Tarjeta TPV",
                    amount = summary.totalCards,
                    subtitle = "Terminal de salón",
                    accentColor = CardBlue,
                    icon = Icons.Default.CreditCard,
                    modifier = Modifier.weight(1f)
                )

                FinancialKpiCard(
                    title = "Depósitos",
                    amount = summary.totalDeposits,
                    subtitle = "Transferencias",
                    accentColor = ProfitGreen,
                    icon = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "Costos, Mercancía Invertida y Mermas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FinancialKpiCard(
                    title = "Mercancía Invertida (Almacén)",
                    amount = summary.totalInventoryValue,
                    subtitle = "Suma al neto como activo en bodega",
                    accentColor = ProfitGreen,
                    icon = Icons.Default.Inventory2,
                    modifier = Modifier.fillMaxWidth().testTag("kpi_inventory_value")
                )

                FinancialKpiCard(
                    title = "Compras Totales Insumos",
                    amount = summary.totalPurchases,
                    subtitle = "Carnes, secos, lácteos, abarrotes",
                    accentColor = ExpenseRed,
                    icon = Icons.Default.ReceiptLong,
                    modifier = Modifier.fillMaxWidth().testTag("kpi_purchases_total")
                )

                FinancialKpiCard(
                    title = "Egresos Fijos Operativos",
                    amount = summary.totalExpenses,
                    subtitle = "Renta, luz, gas LP y sueldos",
                    accentColor = ExpenseRed,
                    icon = Icons.Default.ShoppingBag,
                    modifier = Modifier.fillMaxWidth()
                )

                FinancialKpiCard(
                    title = "Mermas y Sobrantes",
                    amount = summary.totalWasteCost,
                    subtitle = "Descontadas de ganancia",
                    accentColor = MaterialTheme.colorScheme.error,
                    icon = Icons.Default.DeleteSweep,
                    modifier = Modifier.fillMaxWidth().testTag("kpi_waste_cost")
                )
            }
        }
    }
}
