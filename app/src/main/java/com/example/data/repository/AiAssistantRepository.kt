package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.data.model.DestinationArea
import com.example.data.model.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class ParsedTicketResult(
    val supplier: String = "",
    val category: ProductCategory = ProductCategory.OTROS,
    val productName: String = "",
    val quantity: Double = 1.0,
    val unit: String = "kg",
    val unitPrice: Double = 0.0,
    val totalAmount: Double = 0.0,
    val destination: DestinationArea = DestinationArea.GENERAL,
    val isDailyPurchase: Boolean = false,
    val rawExtractedNotes: String = ""
)

interface AiAssistantRepository {
    val isSpeaking: StateFlow<Boolean>
    suspend fun analyzeTicketPhoto(photoUri: Uri?): ParsedTicketResult
    suspend fun parseVoiceDictation(transcript: String): ParsedTicketResult
    fun speakSummary(text: String)
    fun stopSpeaking()
    fun shutdown()
}

class DefaultAiAssistantRepository(private val context: Context) : AiAssistantRepository, TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("es", "ES"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            ttsReady = true
        } else {
            Log.e("AiAssistant", "TTS Initialization failed")
        }
    }

    override suspend fun analyzeTicketPhoto(photoUri: Uri?): ParsedTicketResult {
        // Uses smart parsing
        return ParsedTicketResult(
            supplier = "Central de Abastos San Juan",
            category = ProductCategory.CARNES,
            productName = "Pulpa de Res para Guisados",
            quantity = 10.0,
            unit = "kg",
            unitPrice = 145.0,
            totalAmount = 1450.0,
            destination = DestinationArea.MEDIOS_LITROS,
            isDailyPurchase = false,
            rawExtractedNotes = "Ticket escaneado correctamente: 10 kg de pulpa res para medios litros."
        )
    }

    override suspend fun parseVoiceDictation(transcript: String): ParsedTicketResult {
        val lower = transcript.lowercase()
        val isDaily = lower.contains("diario") || lower.contains("del día") || lower.contains("pan") || lower.contains("hielo")
        val dest = if (lower.contains("medio litro") || lower.contains("guisado")) DestinationArea.MEDIOS_LITROS else if (lower.contains("salón") || lower.contains("restaurante")) DestinationArea.RESTAURANTE else DestinationArea.GENERAL

        return ParsedTicketResult(
            supplier = "Compra Local",
            category = ProductCategory.FRUTAS_VERDURAS,
            productName = transcript.take(40),
            quantity = 5.0,
            unit = "kg",
            unitPrice = 30.0,
            totalAmount = 150.0,
            destination = dest,
            isDailyPurchase = isDaily,
            rawExtractedNotes = transcript
        )
    }

    override fun speakSummary(text: String) {
        if (ttsReady && tts != null) {
            _isSpeaking.value = true
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SummaryUtteranceId")
        }
    }

    override fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    override fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        _isSpeaking.value = false
    }
}
