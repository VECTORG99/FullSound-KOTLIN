package com.grupo8.fullsound.data.remote.supabase.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.grupo8.fullsound.data.remote.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Repositorio para manejar operaciones con Supabase Storage (buckets de archivos)
 */
class SupabaseStorageRepository(private val context: Context) {

    private val TAG = "SupabaseStorage"

    // Nombres de los buckets en Supabase
    private val IMAGES_BUCKET = "Imagenes"
    private val AUDIO_BUCKET = "audios"

    // URL base de Supabase
    private val SUPABASE_URL = "https://kivpcepyhfpqjfoycwel.supabase.co"
    private val STORAGE_PATH = "/storage/v1/object/public"

    /**
     * Sube una imagen al bucket de Supabase Storage
     * @param uri URI del archivo de imagen seleccionado
     * @param fileName Nombre que tendrá el archivo en Supabase (opcional, se genera automáticamente)
     * @return URL pública de la imagen subida o null si falla
     */
    suspend fun uploadImage(uri: Uri, fileName: String? = null): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== INICIO SUBIDA DE IMAGEN ===")
            Log.d(TAG, "📤 Subiendo imagen a Supabase Storage...")
            Log.d(TAG, "URI: $uri")
            Log.d(TAG, "Bucket: $IMAGES_BUCKET")

            // Generar nombre único si no se proporciona
            val finalFileName = fileName ?: "beat_image_${System.currentTimeMillis()}.jpg"
            Log.d(TAG, "Nombre de archivo: $finalFileName")

            // Leer el archivo desde la URI
            Log.d(TAG, "Leyendo bytes del archivo...")
            val bytes = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            } ?: run {
                Log.e(TAG, "❌ No se pudo leer la imagen desde URI")
                return@withContext null
            }

            Log.d(TAG, "📦 Tamaño de la imagen: ${bytes.size / 1024} KB (${bytes.size} bytes)")

            // Verificar que el cliente de Supabase esté inicializado
            Log.d(TAG, "Obteniendo cliente de Supabase...")
            val supabaseClient = SupabaseClient.client
            Log.d(TAG, "✅ Cliente Supabase obtenido")

            // Obtener el bucket de Storage
            Log.d(TAG, "Obteniendo bucket '$IMAGES_BUCKET'...")
            val bucket = supabaseClient.storage.from(IMAGES_BUCKET)
            Log.d(TAG, "✅ Bucket obtenido")

            // Subir a Supabase Storage
            Log.d(TAG, "Iniciando subida...")
            bucket.upload(
                path = finalFileName,
                data = bytes,
                upsert = true
            )
            Log.d(TAG, "✅ Archivo subido al bucket")

            // Construir URL pública
            val publicUrl = "$SUPABASE_URL$STORAGE_PATH/$IMAGES_BUCKET/$finalFileName"
            Log.d(TAG, "✅ URL pública construida: $publicUrl")
            Log.d(TAG, "=== FIN SUBIDA DE IMAGEN (EXITOSA) ===")

            return@withContext publicUrl

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al subir imagen: ${e.message}", e)
            Log.e(TAG, "Tipo de excepción: ${e.javaClass.name}")
            Log.e(TAG, "Stack trace completo:")
            e.printStackTrace()
            Log.e(TAG, "=== FIN SUBIDA DE IMAGEN (FALLIDA) ===")
            return@withContext null
        }
    }

    /**
     * Sube un archivo de audio al bucket de Supabase Storage
     * @param uri URI del archivo de audio seleccionado
     * @param fileName Nombre que tendrá el archivo en Supabase (opcional, se genera automáticamente)
     * @return URL pública del audio subido o null si falla
     */
    suspend fun uploadAudio(uri: Uri, fileName: String? = null): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== INICIO SUBIDA DE AUDIO ===")
            Log.d(TAG, "📤 Subiendo audio a Supabase Storage...")
            Log.d(TAG, "URI: $uri")
            Log.d(TAG, "Bucket: $AUDIO_BUCKET")

            // Generar nombre único si no se proporciona
            val finalFileName = fileName ?: "beat_audio_${System.currentTimeMillis()}.mp3"
            Log.d(TAG, "Nombre de archivo: $finalFileName")

            // Leer el archivo desde la URI
            Log.d(TAG, "Leyendo bytes del archivo...")
            val bytes = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            } ?: run {
                Log.e(TAG, "❌ No se pudo leer el audio desde URI")
                return@withContext null
            }

            Log.d(TAG, "📦 Tamaño del audio: ${bytes.size / 1024} KB (${bytes.size} bytes)")

            // Verificar que el cliente de Supabase esté inicializado
            Log.d(TAG, "Obteniendo cliente de Supabase...")
            val supabaseClient = SupabaseClient.client
            Log.d(TAG, "✅ Cliente Supabase obtenido")

            // Obtener el bucket de Storage
            Log.d(TAG, "Obteniendo bucket '$AUDIO_BUCKET'...")
            val bucket = supabaseClient.storage.from(AUDIO_BUCKET)
            Log.d(TAG, "✅ Bucket obtenido")

            // Subir a Supabase Storage
            Log.d(TAG, "Iniciando subida...")
            bucket.upload(
                path = finalFileName,
                data = bytes,
                upsert = true
            )
            Log.d(TAG, "✅ Archivo subido al bucket")

            // Construir URL pública
            val publicUrl = "$SUPABASE_URL$STORAGE_PATH/$AUDIO_BUCKET/$finalFileName"
            Log.d(TAG, "✅ URL pública construida: $publicUrl")
            Log.d(TAG, "=== FIN SUBIDA DE AUDIO (EXITOSA) ===")

            return@withContext publicUrl

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al subir audio: ${e.message}", e)
            Log.e(TAG, "Tipo de excepción: ${e.javaClass.name}")
            Log.e(TAG, "Stack trace completo:")
            e.printStackTrace()
            Log.e(TAG, "=== FIN SUBIDA DE AUDIO (FALLIDA) ===")
            return@withContext null
        }
    }

    /**
     * Elimina una imagen del bucket de Supabase Storage
     * @param fileName Nombre del archivo a eliminar o URL completa
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    suspend fun deleteImage(fileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🗑️ Eliminando imagen de Supabase Storage...")

            // Extraer nombre del archivo si es una URL completa
            val actualFileName = extractFileName(fileName)

            if (actualFileName.isNullOrBlank()) {
                Log.w(TAG, "⚠️ Nombre de archivo inválido para eliminar")
                return@withContext false
            }

            val bucket = SupabaseClient.client.storage.from(IMAGES_BUCKET)
            bucket.delete(actualFileName)

            Log.d(TAG, "✅ Imagen eliminada: $actualFileName")
            return@withContext true

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al eliminar imagen: ${e.message}", e)
            return@withContext false
        }
    }

    /**
     * Elimina un archivo de audio del bucket de Supabase Storage
     * @param fileName Nombre del archivo a eliminar o URL completa
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    suspend fun deleteAudio(fileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🗑️ Eliminando audio de Supabase Storage...")

            // Extraer nombre del archivo si es una URL completa
            val actualFileName = extractFileName(fileName)

            if (actualFileName.isNullOrBlank()) {
                Log.w(TAG, "⚠️ Nombre de archivo inválido para eliminar")
                return@withContext false
            }

            val bucket = SupabaseClient.client.storage.from(AUDIO_BUCKET)
            bucket.delete(actualFileName)

            Log.d(TAG, "✅ Audio eliminado: $actualFileName")
            return@withContext true

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al eliminar audio: ${e.message}", e)
            return@withContext false
        }
    }

    /**
     * Extrae el nombre del archivo de una URL completa
     * Ejemplo: https://.../Imagenes/beat_123.jpg -> beat_123.jpg
     */
    private fun extractFileName(urlOrFileName: String): String? {
        if (urlOrFileName.isBlank()) return null

        // Si es una URL, extraer el último segmento
        if (urlOrFileName.startsWith("http://") || urlOrFileName.startsWith("https://")) {
            return urlOrFileName.split("/").lastOrNull()
        }

        // Si ya es un nombre de archivo, retornarlo
        return urlOrFileName
    }

    /**
     * Copia un archivo temporal a almacenamiento interno antes de subirlo
     * (útil para procesar archivos antes de subirlos)
     */
    suspend fun copyToTempFile(uri: Uri, fileName: String): File? = withContext(Dispatchers.IO) {
        try {
            val tempFile = File(context.cacheDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            return@withContext tempFile

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al copiar archivo temporal: ${e.message}", e)
            return@withContext null
        }
    }

    /**
     * Verifica si un archivo existe en el bucket
     */
    suspend fun fileExists(bucketName: String, fileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val bucket = SupabaseClient.client.storage.from(bucketName)
            val files = bucket.list()
            return@withContext files.any { it.name == fileName }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al verificar existencia de archivo: ${e.message}", e)
            return@withContext false
        }
    }

    /**
     * Método de diagnóstico para verificar la conexión con Storage
     */
    suspend fun testStorageConnection(): String = withContext(Dispatchers.IO) {
        val result = StringBuilder()
        result.appendLine("=== TEST DE CONEXIÓN SUPABASE STORAGE ===")

        try {
            // 1. Verificar cliente
            result.appendLine("1. Cliente Supabase:")
            val client = SupabaseClient.client
            result.appendLine("   ✅ Cliente inicializado")

            // 2. Verificar Storage plugin
            result.appendLine("2. Plugin Storage:")
            val storage = client.storage
            result.appendLine("   ✅ Storage disponible")

            // 3. Verificar bucket de imágenes
            result.appendLine("3. Bucket '$IMAGES_BUCKET':")
            try {
                val imagesBucket = storage.from(IMAGES_BUCKET)
                result.appendLine("   ✅ Bucket accesible")

                // Intentar listar archivos
                try {
                    val files = imagesBucket.list()
                    result.appendLine("   ✅ Puede listar archivos (${files.size} archivos)")
                } catch (e: Exception) {
                    result.appendLine("   ⚠️ No puede listar archivos: ${e.message}")
                }
            } catch (e: Exception) {
                result.appendLine("   ❌ Error al acceder bucket: ${e.message}")
            }

            // 4. Verificar bucket de audio
            result.appendLine("4. Bucket '$AUDIO_BUCKET':")
            try {
                val audioBucket = storage.from(AUDIO_BUCKET)
                result.appendLine("   ✅ Bucket accesible")

                // Intentar listar archivos
                try {
                    val files = audioBucket.list()
                    result.appendLine("   ✅ Puede listar archivos (${files.size} archivos)")
                } catch (e: Exception) {
                    result.appendLine("   ⚠️ No puede listar archivos: ${e.message}")
                }
            } catch (e: Exception) {
                result.appendLine("   ❌ Error al acceder bucket: ${e.message}")
            }

            result.appendLine("=== FIN DEL TEST ===")

        } catch (e: Exception) {
            result.appendLine("❌ ERROR GENERAL: ${e.message}")
            result.appendLine("Tipo: ${e.javaClass.name}")
            e.printStackTrace()
        }

        val testResult = result.toString()
        Log.d(TAG, testResult)
        return@withContext testResult
    }
}

