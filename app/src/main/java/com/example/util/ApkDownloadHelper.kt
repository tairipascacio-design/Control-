package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File

object ApkDownloadHelper {

    fun exportAndShareApk(context: Context): Pair<Boolean, String> {
        return try {
            val sourceDir = context.applicationInfo.sourceDir
            val sourceFile = File(sourceDir)

            if (!sourceFile.exists()) {
                return Pair(false, "No se encontró el archivo APK base instalado.")
            }

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            var targetFile = File(downloadsDir, "ControlTotalRestaurante.apk")

            val copied = try {
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                sourceFile.copyTo(targetFile, overwrite = true)
                true
            } catch (e: Exception) {
                targetFile = File(context.cacheDir, "ControlTotalRestaurante.apk")
                sourceFile.copyTo(targetFile, overwrite = true)
                true
            }

            if (copied && targetFile.exists()) {
                shareApkIntent(context, targetFile)
                Pair(true, "APK preparado: ${targetFile.name}. Abriendo menú para compartir/instalar.")
            } else {
                Pair(false, "No se pudo copiar el archivo APK.")
            }
        } catch (e: Exception) {
            Pair(false, "Error al preparar el APK: ${e.localizedMessage}")
        }
    }

    private fun shareApkIntent(context: Context, apkFile: File) {
        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, apkUri)
            putExtra(Intent.EXTRA_SUBJECT, "Control Total - Instalador APK")
            putExtra(
                Intent.EXTRA_TEXT,
                "Instalador APK de Control Total Restaurante para tablets y celulares."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intent, "Descargar o Compartir APK").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
