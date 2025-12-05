package com.grupo8.fullsound.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests para el modelo CarritoItem
 */
class CarritoItemTest : StringSpec({

    "CarritoItem debe crearse con todos los campos requeridos" {
        val item = CarritoItem(
            id = 1,
            beatId = 100,
            titulo = "Beat Test",
            artista = "Artist Test",
            precio = 10000.0,
            imagenPath = "1.jpg",
            cantidad = 1
        )

        item.id shouldBe 1
        item.beatId shouldBe 100
        item.titulo shouldBe "Beat Test"
        item.artista shouldBe "Artist Test"
        item.precio shouldBe 10000.0
        item.imagenPath shouldBe "1.jpg"
        item.cantidad shouldBe 1
    }

    "CarritoItem debe tener cantidad por defecto 1" {
        val item = CarritoItem(
            id = 0,
            beatId = 100,
            titulo = "Beat Test",
            precio = 5000.0
        )

        item.cantidad shouldBe 1
    }

    "CarritoItem debe calcular subtotal correctamente" {
        val item = CarritoItem(
            id = 1,
            beatId = 100,
            titulo = "Beat Test",
            precio = 5000.0,
            cantidad = 3
        )

        val subtotal = item.precio * item.cantidad
        subtotal shouldBe 15000.0
    }

    "CarritoItem debe permitir artista e imagen nulos" {
        val item = CarritoItem(
            id = 1,
            beatId = 100,
            titulo = "Beat Test",
            artista = null,
            precio = 5000.0,
            imagenPath = null,
            cantidad = 1
        )

        item.artista shouldBe null
        item.imagenPath shouldBe null
    }

    "CarritoItem debe crearse desde Beat" {
        val beat = Beat(
            id = 100,
            titulo = "Beat Test",
            artista = "Artist Test",
            precio = 10000.0,
            imagenPath = "1.jpg",
            mp3Path = "1.mp3"
        )

        val item = CarritoItem(
            id = 0,
            beatId = beat.id,
            titulo = beat.titulo,
            artista = beat.artista,
            precio = beat.precio,
            imagenPath = beat.imagenPath,
            cantidad = 1
        )

        item.beatId shouldBe beat.id
        item.titulo shouldBe beat.titulo
        item.artista shouldBe beat.artista
        item.precio shouldBe beat.precio
        item.imagenPath shouldBe beat.imagenPath
    }

    "CarritoItem debe poder aumentar cantidad" {
        val item = CarritoItem(
            id = 1,
            beatId = 100,
            titulo = "Beat Test",
            precio = 5000.0,
            cantidad = 1
        )

        val itemActualizado = item.copy(cantidad = item.cantidad + 1)

        itemActualizado.cantidad shouldBe 2
        itemActualizado.beatId shouldBe item.beatId
    }

    "CarritoItem debe compararse correctamente por igualdad" {
        val item1 = CarritoItem(
            id = 1,
            beatId = 100,
            titulo = "Beat Test",
            precio = 5000.0,
            cantidad = 1
        )

        val item2 = CarritoItem(
            id = 1,
            beatId = 100,
            titulo = "Beat Test",
            precio = 5000.0,
            cantidad = 1
        )

        val item3 = CarritoItem(
            id = 2,
            beatId = 200,
            titulo = "Beat Test 2",
            precio = 5000.0,
            cantidad = 1
        )

        item1 shouldBe item2
        item1 shouldNotBe item3
    }

    "CarritoItem con diferentes cantidades debe ser diferente" {
        val item1 = CarritoItem(
            id = 1,
            beatId = 100,
            titulo = "Beat Test",
            precio = 5000.0,
            cantidad = 1
        )

        val item2 = CarritoItem(
            id = 1,
            beatId = 100,
            titulo = "Beat Test",
            precio = 5000.0,
            cantidad = 2
        )

        item1 shouldNotBe item2
    }
})

