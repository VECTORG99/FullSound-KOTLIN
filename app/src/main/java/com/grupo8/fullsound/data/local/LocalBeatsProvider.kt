package com.grupo8.fullsound.data.local

import com.grupo8.fullsound.model.Beat

 //Proveedor de beats locales para la app
 // Contiene 8 beats de ejemplo con imágenes y archivos mp3
object LocalBeatsProvider {

    fun getBeats(): List<Beat> {
        val images = listOf("img1", "img2", "img3", "img4", "img5")
        return listOf(
            Beat(
                id = 0,
                titulo = "Samuel canchaya",
                artista = "Ludwig van Beethoven",
                bpm = 95,
                imagenPath = images[0 % images.size],
                mp3Path = "beat1"
            ),
            Beat(
                id = 0,
                titulo = "Luis salazar",
                artista = "Wolfgang Amadeus Mozart",
                bpm = 108,
                imagenPath = images[1 % images.size],
                mp3Path = "beat2"
            ),
            Beat(
                id = 0,
                titulo = "Ismael Rivas",
                artista = "Johann Sebastian Bach",
                bpm = 88,
                imagenPath = images[2 % images.size],
                mp3Path = "beat3"
            ),
            Beat(
                id = 0,
                titulo = "Diego Hernandez",
                artista = "Frédéric Chopin",
                bpm = 72,
                imagenPath = images[3 % images.size],
                mp3Path = "beat4"
            ),
            Beat(
                id = 0,
                titulo = "Axel Moraga",
                artista = "Pyotr Ilyich Tchaikovsky",
                bpm = 116,
                imagenPath = images[4 % images.size],
                mp3Path = "beat5"
            ),
            Beat(
                id = 0,
                titulo = "Diego hernandez remix",
                artista = "Antonio Vivaldi",
                bpm = 104,
                imagenPath = images[0 % images.size],
                mp3Path = "beat6"
            ),
            Beat(
                id = 0,
                titulo = "Maxi",
                artista = "Franz Liszt",
                bpm = 80,
                imagenPath = images[1 % images.size],
                mp3Path = "beat7"
            ),
            Beat(
                id = 0,
                titulo = "Diego Patricio Cares Gonzales",
                artista = "Claude Debussy",
                bpm = 92,
                imagenPath = images[2 % images.size],
                mp3Path = "beat8"
            )
        )
    }
}
