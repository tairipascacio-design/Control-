package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.DeliveryRound
import com.example.data.model.DestinationArea
import com.example.data.model.ExpenseCategory
import com.example.data.model.PaymentMethod
import com.example.data.model.ProductCategory
import com.example.data.model.SalesChannel
import com.example.data.model.WasteReason

class Converters {
    @TypeConverter
    fun fromProductCategory(value: ProductCategory?): String = value?.name ?: ProductCategory.OTROS.name

    @TypeConverter
    fun toProductCategory(value: String?): ProductCategory =
        try { ProductCategory.valueOf(value ?: ProductCategory.OTROS.name) } catch (e: Exception) { ProductCategory.OTROS }

    @TypeConverter
    fun fromDestinationArea(value: DestinationArea?): String = value?.name ?: DestinationArea.GENERAL.name

    @TypeConverter
    fun toDestinationArea(value: String?): DestinationArea =
        try { DestinationArea.valueOf(value ?: DestinationArea.GENERAL.name) } catch (e: Exception) { DestinationArea.GENERAL }

    @TypeConverter
    fun fromSalesChannel(value: SalesChannel?): String = value?.name ?: SalesChannel.MEDIOS_LITROS.name

    @TypeConverter
    fun toSalesChannel(value: String?): SalesChannel =
        try { SalesChannel.valueOf(value ?: SalesChannel.MEDIOS_LITROS.name) } catch (e: Exception) { SalesChannel.MEDIOS_LITROS }

    @TypeConverter
    fun fromDeliveryRound(value: DeliveryRound?): String = value?.name ?: DeliveryRound.VUELTA_1.name

    @TypeConverter
    fun toDeliveryRound(value: String?): DeliveryRound =
        try { DeliveryRound.valueOf(value ?: DeliveryRound.VUELTA_1.name) } catch (e: Exception) { DeliveryRound.VUELTA_1 }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?): String = value?.name ?: PaymentMethod.EFECTIVO.name

    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod =
        try { PaymentMethod.valueOf(value ?: PaymentMethod.EFECTIVO.name) } catch (e: Exception) { PaymentMethod.EFECTIVO }

    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory?): String = value?.name ?: ExpenseCategory.OTRO.name

    @TypeConverter
    fun toExpenseCategory(value: String?): ExpenseCategory =
        try { ExpenseCategory.valueOf(value ?: ExpenseCategory.OTRO.name) } catch (e: Exception) { ExpenseCategory.OTRO }

    @TypeConverter
    fun fromWasteReason(value: WasteReason?): String = value?.name ?: WasteReason.CADUCIDAD.name

    @TypeConverter
    fun toWasteReason(value: String?): WasteReason =
        try { WasteReason.valueOf(value ?: WasteReason.CADUCIDAD.name) } catch (e: Exception) { WasteReason.CADUCIDAD }
}
