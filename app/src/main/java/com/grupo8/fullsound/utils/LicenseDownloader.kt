package com.grupo8.fullsound.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

/**
 * Manejador de descarga de archivos de licencia
 */
object LicenseDownloader {

    /**
     * Descarga el archivo de licencia en el dispositivo
     * @param context Contexto de la aplicación
     * @param licenseContent Contenido de la licencia
     * @param fileName Nombre del archivo
     * @return true si la descarga fue exitosa, false en caso contrario
     */
    fun downloadLicense(
        context: Context,
        licenseContent: String,
        fileName: String = LicenseGenerator.generateLicenseFileName()
    ): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 y superior - usar MediaStore
                downloadUsingMediaStore(context, licenseContent, fileName)
            } else {
                // Android 9 y anterior - usar almacenamiento externo
                downloadToExternalStorage(context, licenseContent, fileName)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "Error al descargar la licencia: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }

    /**
     * Descarga usando MediaStore (Android 10+)
     */
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
    private fun downloadUsingMediaStore(
        context: Context,
        content: String,
        fileName: String
    ) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/FullSound")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(content.toByteArray())
                outputStream.flush()
            }

            Toast.makeText(
                context,
                "Licencia descargada en Descargas/FullSound/$fileName",
                Toast.LENGTH_LONG
            ).show()
        } ?: throw Exception("No se pudo crear el archivo")
    }

    /**
     * Descarga a almacenamiento externo (Android 9 y anterior)
     */
    private fun downloadToExternalStorage(
        context: Context,
        content: String,
        fileName: String
    ) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fullSoundDir = File(downloadsDir, "FullSound")

        // Crear directorio si no existe
        if (!fullSoundDir.exists()) {
            fullSoundDir.mkdirs()
        }

        val file = File(fullSoundDir, fileName)

        FileOutputStream(file).use { outputStream ->
            outputStream.write(content.toByteArray())
            outputStream.flush()
        }

        Toast.makeText(
            context,
            "Licencia descargada en Descargas/FullSound/$fileName",
            Toast.LENGTH_LONG
        ).show()
    }
}

