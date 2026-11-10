package com.example

import com.example.data.model.DeliveryRound
import com.example.data.model.DeliveryShift
import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.data.model.InventoryItem
import com.example.data.model.PaymentMethod
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.SalesChannel
import com.example.data.model.WasteRecord
import com.example.data.repository.RestaurantRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RestaurantFinancialTest {

    @Test
    fun testNetProfitCalculationWithInventoryValue() {
        val sales = listOf(
            Sale(totalAmount = 1000.0, channel = SalesChannel.MEDIOS_LITROS, paymentMethod = PaymentMethod.EFECTIVO),
            Sale(totalAmount = 500.0, channel = SalesChannel.RESTAURANTE, paymentMethod = PaymentMethod.TARJETA)
        )
        val purchases = listOf(
            Purchase(totalAmount = 400.0, isDailyPurchase = false)
        )
        val expenses = listOf(
            Expense(amount = 200.0, category = ExpenseCategory.GAS)
        )
        val inventory = listOf(
            InventoryItem(currentStock = 10.0, unitCost = 30.0) // 300.0 value
        )
        val waste = listOf(
            WasteRecord(estimatedCost = 50.0)
        )
        val shifts = emptyList<DeliveryShift>()

        val mockRepo = RestaurantRepository(androidx.room.Room.inMemoryDatabaseBuilder(
            androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            com.example.data.db.AppDatabase::class.java
        ).build())

        val summary = mockRepo.calculateSummary(sales, purchases, expenses, waste, inventory, shifts)

        // Net = 1500 (sales) - 400 (purchases) - 200 (expenses) + 300 (inventory) - 50 (waste) = 1150
        assertEquals(1500.0, summary.totalSales, 0.01)
        assertEquals(1150.0, summary.netProfit, 0.01)
        assertTrue(summary.isProfitable)
    }
}
