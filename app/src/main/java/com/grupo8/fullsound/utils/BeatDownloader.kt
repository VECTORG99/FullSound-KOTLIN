package com.grupo8.fullsound.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manejador de descarga de archivos MP3 de beats
 */
object BeatDownloader {

    /**
     * Descarga un beat en formato MP3
     * @param context Contexto de la aplicación
     * @param mp3Url URL del archivo MP3
     * @param beatTitle Título del beat (para el nombre del archivo)
     * @param artistName Nombre del artista (opcional)
     * @return true si la descarga inició exitosamente
     */
    suspend fun downloadBeat(
        context: Context,
        mp3Url: String?,
        beatTitle: String,
        artistName: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        if (mp3Url.isNullOrBlank()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Error: No hay URL de descarga para el beat '$beatTitle'",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@withContext false
        }

        try {
            val fileName = generateBeatFileName(beatTitle, artistName)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - usar DownloadManager
                downloadUsingDownloadManager(context, mp3Url, fileName)
            } else {
                // Android 9 y anterior - usar DownloadManager también
                downloadUsingDownloadManager(context, mp3Url, fileName)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Error al descargar '$beatTitle': ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            false
        }
    }

    /**
     * Descarga usando DownloadManager (método estándar y confiable)
     */
    private fun downloadUsingDownloadManager(
        context: Context,
        url: String,
        fileName: String
    ) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Descargando beat de FullSound")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "FullSound/$fileName")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    /**
     * Descarga múltiples beats
     * @param context Contexto de la aplicación
     * @param beats Lista de tuplas (mp3Url, beatTitle, artistName)
     * @return número de beats descargados exitosamente
     */
    suspend fun downloadMultipleBeats(
        context: Context,
        beats: List<Triple<String?, String, String?>>
    ): Int {
        var successCount = 0

        beats.forEach { (mp3Url, beatTitle, artistName) ->
            if (downloadBeat(context, mp3Url, beatTitle, artistName)) {
                successCount++
            }
            // Pequeño delay para no saturar el sistema
            kotlinx.coroutines.delay(200)
        }

        return successCount
    }

    /**
     * Genera un nombre de archivo válido para el beat
     */
    private fun generateBeatFileName(beatTitle: String, artistName: String?): String {
        // Limpiar caracteres no válidos para nombres de archivo
        val cleanTitle = beatTitle
            .replace(Regex("[^a-zA-Z0-9\\s-_]"), "")
            .replace(Regex("\\s+"), "_")
            .take(50) // Limitar longitud

        val cleanArtist = artistName?.let {
            it.replace(Regex("[^a-zA-Z0-9\\s-_]"), "")
                .replace(Regex("\\s+"), "_")
                .take(30)
        }

        return if (cleanArtist != null && cleanArtist.isNotBlank()) {
            "${cleanArtist}_-_${cleanTitle}.mp3"
        } else {
            "${cleanTitle}.mp3"
        }
    }

    /**
     * Obtiene la ruta del directorio de música donde se guardan los beats
     */
    fun getDownloadPath(): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + "/FullSound"
    }
}

