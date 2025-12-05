package com.grupo8.fullsound.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests para el modelo Beat adaptado a Supabase
 */
class BeatTest : StringSpec({

    "Beat debe crearse con todos los campos de Supabase" {
        val beat = Beat(
            id = 1,
            titulo = "Beat Test",
            slug = "beat-test",
            artista = "Artist Test",
            precio = 10000.0,
            bpm = 120,
            tonalidad = "Am",
            duracion = 180,
            genero = "Hip-Hop",
            etiquetas = "trap,boom bap",
            descripcion = "Un beat de prueba",
            imagenPath = "1.jpg",
            mp3Path = "1.mp3",
            audioDemoPath = "1_demo.mp3",
            reproducciones = 100,
            estado = "DISPONIBLE",
            createdAt = "2025-12-01T00:00:00Z",
            updatedAt = "2025-12-01T00:00:00Z"
        )

        beat.id shouldBe 1
        beat.titulo shouldBe "Beat Test"
        beat.artista shouldBe "Artist Test"
        beat.precio shouldBe 10000.0
        beat.genero shouldBe "Hip-Hop"
        beat.estado shouldBe "DISPONIBLE"
    }

    "Beat debe tener valores por defecto correctos" {
        val beat = Beat(
            id = 0,
            titulo = "Beat Mínimo"
        )

        beat.id shouldBe 0
        beat.titulo shouldBe "Beat Mínimo"
        beat.slug shouldBe null
        beat.artista shouldBe null
        beat.precio shouldBe 0.0
        beat.reproducciones shouldBe 0
        beat.estado shouldBe "DISPONIBLE"
    }

    "Beat debe poder copiar con nuevos valores" {
        val original = Beat(
            id = 1,
            titulo = "Beat Original",
            precio = 5000.0
        )

        val copia = original.copy(
            titulo = "Beat Modificado",
            precio = 8000.0
        )

        copia.id shouldBe 1
        copia.titulo shouldBe "Beat Modificado"
        copia.precio shouldBe 8000.0
    }

    "Beat debe compararse correctamente por igualdad" {
        val beat1 = Beat(
            id = 1,
            titulo = "Beat 1",
            precio = 5000.0
        )

        val beat2 = Beat(
            id = 1,
            titulo = "Beat 1",
            precio = 5000.0
        )

        val beat3 = Beat(
            id = 2,
            titulo = "Beat 2",
            precio = 5000.0
        )

        beat1 shouldBe beat2
        beat1 shouldNotBe beat3
    }

    "Beat debe manejar campos opcionales nulos" {
        val beat = Beat(
            id = 1,
            titulo = "Beat Sin Extras",
            artista = null,
            genero = null,
            imagenPath = null,
            mp3Path = null
        )

        beat.artista shouldBe null
        beat.genero shouldBe null
        beat.imagenPath shouldBe null
        beat.mp3Path shouldBe null
    }
})

