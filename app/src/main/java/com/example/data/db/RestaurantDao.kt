package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DeliveryOrder
import com.example.data.model.DeliveryShift
import com.example.data.model.Expense
import com.example.data.model.InventoryItem
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.WasteRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases ORDER BY dateMillis DESC")
    fun getAllPurchases(): Flow<List<Purchase>>

    @Query("SELECT * FROM purchases WHERE isDailyPurchase = 1 ORDER BY dateMillis DESC")
    fun getDailyPurchases(): Flow<List<Purchase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: Purchase): Long

    @Update
    suspend fun updatePurchase(purchase: Purchase)

    @Delete
    suspend fun deletePurchase(purchase: Purchase)
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY productName ASC")
    fun getAllInventory(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory WHERE productName = :name LIMIT 1")
    suspend fun findByName(name: String): InventoryItem?

    @Query("SELECT * FROM inventory WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): InventoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: InventoryItem): Long

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)
}

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY dateMillis DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long

    @Update
    suspend fun updateSale(sale: Sale)

    @Delete
    suspend fun deleteSale(sale: Sale)
}

@Dao
interface DeliveryShiftDao {
    @Query("SELECT * FROM delivery_shifts ORDER BY dateMillis DESC")
    fun getAllDeliveryShifts(): Flow<List<DeliveryShift>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: DeliveryShift): Long

    @Update
    suspend fun updateShift(shift: DeliveryShift)

    @Delete
    suspend fun deleteShift(shift: DeliveryShift)
}

@Dao
interface DeliveryOrderDao {
    @Query("SELECT * FROM delivery_orders ORDER BY id ASC")
    fun getAllDeliveryOrders(): Flow<List<DeliveryOrder>>

    @Query("SELECT * FROM delivery_orders WHERE shiftId = :shiftId ORDER BY id ASC")
    fun getOrdersForShift(shiftId: Long): Flow<List<DeliveryOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: DeliveryOrder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<DeliveryOrder>)

    @Update
    suspend fun updateOrder(order: DeliveryOrder)

    @Delete
    suspend fun deleteOrder(order: DeliveryOrder)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY dateMillis DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}

@Dao
interface WasteDao {
    @Query("SELECT * FROM waste_records ORDER BY dateMillis DESC")
    fun getAllWaste(): Flow<List<WasteRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaste(waste: WasteRecord): Long

    @Update
    suspend fun updateWaste(waste: WasteRecord)

    @Delete
    suspend fun deleteWaste(waste: WasteRecord)
}
