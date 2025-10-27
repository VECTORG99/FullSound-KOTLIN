package com.grupo8.fullsound.data.local

import com.grupo8.fullsound.data.models.Beat

 //Proveedor de beats locales para la app
 // Contiene 8 beats de ejemplo con imágenes y archivos mp3
object LocalBeatsProvider {

    fun getBeats(): List<Beat> {
        return listOf(
            Beat(
                id = 1,
                titulo = "Classical Dreams",
                artista = "Ludwig van Beethoven",
                bpm = 95,
                imagenPath = "1",
                mp3Path = "1",
                precio = 9.99
            ),
            Beat(
                id = 2,
                titulo = "Symphony Vibes",
                artista = "Wolfgang Amadeus Mozart",
                bpm = 108,
                imagenPath = "2",
                mp3Path = "2",
                precio = 12.99
            ),
            Beat(
                id = 3,
                titulo = "Baroque Rhythm",
                artista = "Johann Sebastian Bach",
                bpm = 88,
                imagenPath = "3",
                mp3Path = "3",
                precio = 11.99
            ),
            Beat(
                id = 4,
                titulo = "Romantic Flow",
                artista = "Frédéric Chopin",
                bpm = 72,
                imagenPath = "4",
                mp3Path = "4",
                precio = 14.99
            ),
            Beat(
                id = 5,
                titulo = "Orchestral Beat",
                artista = "Pyotr Ilyich Tchaikovsky",
                bpm = 116,
                imagenPath = "6",
                mp3Path = "5",
                precio = 15.99
            ),
            Beat(
                id = 6,
                titulo = "Concerto Groove",
                artista = "Antonio Vivaldi",
                bpm = 104,
                imagenPath = "7",
                mp3Path = "6",
                precio = 10.99
            ),
            Beat(
                id = 7,
                titulo = "Piano Essence",
                artista = "Franz Liszt",
                bpm = 80,
                imagenPath = "8",
                mp3Path = "7",
                precio = 13.99
            ),
            Beat(
                id = 8,
                titulo = "Classical Fusion",
                artista = "Claude Debussy",
                bpm = 92,
                imagenPath = "10",
                mp3Path = "8",
                precio = 11.99
            )
        )
    }


    // Obtiene un beat por su ID

    fun getBeatById(id: Int): Beat? {
        return getBeats().find { it.id == id }
    }


     // Obtener imagenes

    fun getImageFileName(imagePath: String): String {
        return "images/$imagePath.jpg"
    }

        // Obtener recursos de audio
    fun getAudioResourceId(context: android.content.Context, mp3Path: String): Int {
        val resourceName = mp3Path
        return context.resources.getIdentifier(resourceName, "raw", context.packageName)
    }
}

