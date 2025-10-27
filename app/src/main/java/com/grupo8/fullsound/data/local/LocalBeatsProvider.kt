package com.grupo8.fullsound.data.local

import com.grupo8.fullsound.model.Beat

 //Proveedor de beats locales para la app
 // Contiene 8 beats de ejemplo con imágenes y archivos mp3
object LocalBeatsProvider {

    fun getBeats(): List<Beat> {
        return listOf(
            Beat(
                id = 0,
                titulo = "Samuel canchalla",
                artista = "Ludwig van Beethoven",
                bpm = 95,
                imagenPath = "placeholder",
                mp3Path = "beat1"
            ),
            Beat(
                id = 0,
                titulo = "Luis salazar",
                artista = "Wolfgang Amadeus Mozart",
                bpm = 108,
                imagenPath = "placeholder",
                mp3Path = "beat2"
            ),
            Beat(
                id = 0,
                titulo = "Ismael Rivas",
                artista = "Johann Sebastian Bach",
                bpm = 88,
                imagenPath = "placeholder",
                mp3Path = "beat3"
            ),
            Beat(
                id = 0,
                titulo = "Diego Hernandez",
                artista = "Frédéric Chopin",
                bpm = 72,
                imagenPath = "placeholder",
                mp3Path = "beat4"
            ),
            Beat(
                id = 0,
                titulo = "Axel Moraga",
                artista = "Pyotr Ilyich Tchaikovsky",
                bpm = 116,
                imagenPath = "placeholder",
                mp3Path = "beat5"
            ),
            Beat(
                id = 0,
                titulo = "Diego hernandez remix",
                artista = "Antonio Vivaldi",
                bpm = 104,
                imagenPath = "placeholder",
                mp3Path = "beat6"
            ),
            Beat(
                id = 0,
                titulo = "Maxi",
                artista = "Franz Liszt",
                bpm = 80,
                imagenPath = "placeholder",
                mp3Path = "beat7"
            ),
            Beat(
                id = 0,
                titulo = "Diego Patricio Cares Gonzales",
                artista = "Claude Debussy",
                bpm = 92,
                imagenPath = "placeholder",
                mp3Path = "beat8"
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
        // El mp3Path ya viene sin la extensión (beat1, beat2, etc.)
        return context.resources.getIdentifier(mp3Path, "raw", context.packageName)
    }
}

