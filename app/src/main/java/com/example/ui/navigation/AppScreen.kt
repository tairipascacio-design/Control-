package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppScreen(val title: String, val icon: ImageVector, val testTag: String) {
    DASHBOARD("Resumen Neto Semanal", Icons.Default.Assessment, "nav_dashboard"),
    PURCHASES("Compras Insumos", Icons.Default.ShoppingCart, "nav_purchases"),
    DELIVERY("Reparto Medios Litros", Icons.Default.Moped, "nav_delivery"),
    SALON_REGISTER("Caja Salón / Tablet", Icons.Default.PointOfSale, "nav_salon_register"),
    INVENTORY("Inventario Almacén", Icons.Default.Inventory2, "nav_inventory"),
    EXPENSES("Egresos & Mermas", Icons.Default.AttachMoney, "nav_expenses")
}
