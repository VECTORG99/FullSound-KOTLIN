package com.grupo8.fullsound.data.remote.supabase.repository

import android.util.Log
import com.grupo8.fullsound.data.remote.supabase.SupabaseClient
import com.grupo8.fullsound.data.remote.supabase.dto.BeatSupabaseDto
import com.grupo8.fullsound.model.Beat
import io.github.jan.supabase.postgrest.from

/**
 * Repositorio para operaciones CRUD de beats en Supabase
 */
class SupabaseBeatRepository {

    private val TAG = "SupabaseBeatRepository"

    // URLs base de Supabase Storage
    private val SUPABASE_URL = "https://kivpcepyhfpqjfoycwel.supabase.co"
    private val STORAGE_PATH = "/storage/v1/object/public"
    private val IMAGES_BUCKET = "Imagenes"
    private val AUDIO_BUCKET = "audios"

    /**
     * Construye la URL completa de Supabase Storage para una imagen
     * Si el valor ya es una URL completa, la retorna tal cual
     * Si es solo un nombre de archivo (ej: "3.jpg"), construye la URL completa
     */
    private fun buildImageUrl(imagenUrl: String?): String? {
        if (imagenUrl.isNullOrBlank()) return null

        // Si ya es una URL completa, retornarla tal cual
        if (imagenUrl.startsWith("http://") || imagenUrl.startsWith("https://")) {
            return imagenUrl
        }

        // Si es solo un nombre de archivo, construir la URL completa
        return "$SUPABASE_URL$STORAGE_PATH/$IMAGES_BUCKET/$imagenUrl"
    }

    /**
     * Construye la URL completa de Supabase Storage para un audio
     * Si el valor ya es una URL completa, la retorna tal cual
     * Si es solo un nombre de archivo (ej: "3.mp3"), construye la URL completa
     */
    private fun buildAudioUrl(audioUrl: String?): String? {
        if (audioUrl.isNullOrBlank()) return null

        // Si ya es una URL completa, retornarla tal cual
        if (audioUrl.startsWith("http://") || audioUrl.startsWith("https://")) {
            return audioUrl
        }

        // Si es solo un nombre de archivo, construir la URL completa
        return "$SUPABASE_URL$STORAGE_PATH/$AUDIO_BUCKET/$audioUrl"
    }

    /**
     * Convierte BeatSupabaseDto a Beat (modelo local)
     * Construye automáticamente las URLs completas de Supabase Storage
     */
    private fun dtoToModel(dto: BeatSupabaseDto): Beat {
        return Beat(
            id = dto.idBeat ?: 0,
            titulo = dto.titulo,
            slug = dto.slug,
            artista = dto.artista,
            precio = dto.precio.toDouble(), // Convertir de Int a Double
            bpm = dto.bpm,
            tonalidad = dto.tonalidad,
            duracion = dto.duracion,
            genero = dto.genero,
            etiquetas = dto.etiquetas,
            descripcion = dto.descripcion,
            imagenPath = buildImageUrl(dto.imagenUrl), // Construir URL completa automáticamente
            mp3Path = buildAudioUrl(dto.audioUrl), // Construir URL completa automáticamente
            audioDemoPath = buildAudioUrl(dto.audioDemoUrl), // Construir URL completa automáticamente
            reproducciones = dto.reproducciones,
            estado = dto.estado,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    /**
     * Convierte Beat (modelo local) a BeatSupabaseDto
     */
    private fun modelToDto(beat: Beat): BeatSupabaseDto {
        return BeatSupabaseDto(
            idBeat = if (beat.id > 0) beat.id else null,
            titulo = beat.titulo,
            slug = beat.slug,
            artista = beat.artista,
            precio = beat.precio.toInt(), // Convertir de Double a Int
            bpm = beat.bpm,
            tonalidad = beat.tonalidad,
            duracion = beat.duracion,
            genero = beat.genero,
            etiquetas = beat.etiquetas,
            descripcion = beat.descripcion,
            imagenUrl = beat.imagenPath,
            audioUrl = beat.mp3Path,
            audioDemoUrl = beat.audioDemoPath,
            reproducciones = beat.reproducciones,
            estado = beat.estado,
            createdAt = beat.createdAt,
            updatedAt = beat.updatedAt
        )
    }

    /**
     * Obtiene todos los beats desde Supabase
     */
    suspend fun getAllBeats(): List<Beat> {
        return try {
            Log.d(TAG, "📡 Solicitando beats desde Supabase (tabla: beat)...")
            Log.d(TAG, "🔗 Cliente Supabase inicializado: ${SupabaseClient.client}")

            val result = SupabaseClient.client
                .from("beat")
                .select()
                .decodeList<BeatSupabaseDto>()

            Log.d(TAG, "✅ Supabase retornó ${result.size} beats")

            if (result.isEmpty()) {
                Log.w(TAG, "⚠️ La consulta no retornó beats. Verifica que la tabla 'beat' tenga datos.")
            }

            result.map { dto ->
                dtoToModel(dto).also { beat ->
                    Log.d(TAG, "  - Beat: ${beat.titulo} (ID: ${beat.id})")
                    Log.d(TAG, "    Imagen BD: ${dto.imagenUrl} → URL: ${beat.imagenPath}")
                    Log.d(TAG, "    Audio BD: ${dto.audioUrl} → URL: ${beat.mp3Path}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al obtener beats desde Supabase")
            Log.e(TAG, "   Tipo: ${e.javaClass.simpleName}")
            Log.e(TAG, "   Mensaje: ${e.message}")
            Log.e(TAG, "   Causa: ${e.cause?.message}")
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Obtiene un beat por su ID
     */
    suspend fun getBeatById(beatId: Int): Beat? {
        return try {
            Log.d(TAG, "📡 Obteniendo beat con ID: $beatId desde Supabase...")

            val result = SupabaseClient.client
                .from("beat")
                .select {
                    filter {
                        eq("id_beat", beatId)
                    }
                }
                .decodeSingleOrNull<BeatSupabaseDto>()

            result?.let { dto ->
                Log.d(TAG, "✅ Beat encontrado: ${dto.titulo}")
                dtoToModel(dto)
            } ?: run {
                Log.w(TAG, "⚠️ Beat con ID $beatId no encontrado")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al obtener beat por ID desde Supabase", e)
            throw e
        }
    }

    /**
     * Inserta un nuevo beat en Supabase
     */
    suspend fun insertBeat(beat: Beat): Beat {
        return try {
            Log.d(TAG, "📡 Insertando beat: ${beat.titulo} en Supabase...")

            val dto = modelToDto(beat)

            val result = SupabaseClient.client
                .from("beat")
                .insert(dto) {
                    select()
                }
                .decodeSingle<BeatSupabaseDto>()

            Log.d(TAG, "✅ Beat insertado exitosamente con ID: ${result.idBeat}")
            dtoToModel(result)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al insertar beat en Supabase", e)
            throw e
        }
    }

    /**
     * Actualiza un beat existente en Supabase
     */
    suspend fun updateBeat(beat: Beat): Beat {
        return try {
            Log.d(TAG, "📡 Actualizando beat con ID: ${beat.id} en Supabase...")

            val dto = modelToDto(beat)

            val result = SupabaseClient.client
                .from("beat")
                .update(dto) {
                    filter {
                        eq("id_beat", beat.id)
                    }
                    select()
                }
                .decodeSingle<BeatSupabaseDto>()

            Log.d(TAG, "✅ Beat actualizado exitosamente")
            dtoToModel(result)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al actualizar beat en Supabase", e)
            throw e
        }
    }

    /**
     * Elimina un beat por su ID
     */
    suspend fun deleteBeat(beatId: Int) {
        try {
            Log.d(TAG, "📡 Eliminando beat con ID: $beatId de Supabase...")

            SupabaseClient.client
                .from("beat")
                .delete {
                    filter {
                        eq("id_beat", beatId)
                    }
                }

            Log.d(TAG, "✅ Beat eliminado exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al eliminar beat de Supabase", e)
            throw e
        }
    }
}

