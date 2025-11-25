package com.grupo8.fullsound.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Tests para FormValidator
 * Estos tests verifican que las validaciones funcionen correctamente sin dependencias de Android
 */
class FormValidatorTest : StringSpec({

    "isValidEmail debe validar emails correctos" {
        FormValidator.isValidEmail("test@gmail.com") shouldBe true
        FormValidator.isValidEmail("user.name@example.com") shouldBe true
        FormValidator.isValidEmail("user+tag@domain.co.uk") shouldBe true
    }

    "isValidEmail debe rechazar emails incorrectos" {
        FormValidator.isValidEmail("") shouldBe false
        FormValidator.isValidEmail("   ") shouldBe false
        FormValidator.isValidEmail("notanemail") shouldBe false
        FormValidator.isValidEmail("@gmail.com") shouldBe false
        FormValidator.isValidEmail("test@") shouldBe false
        FormValidator.isValidEmail("test@.com") shouldBe false
    }

    "isValidEmailOrUsername debe aceptar emails válidos" {
        FormValidator.isValidEmailOrUsername("test@gmail.com") shouldBe true
        FormValidator.isValidEmailOrUsername("user@example.com") shouldBe true
    }

    "isValidEmailOrUsername debe aceptar usernames (no vacíos)" {
        FormValidator.isValidEmailOrUsername("username123") shouldBe true
        FormValidator.isValidEmailOrUsername("user_name") shouldBe true
        FormValidator.isValidEmailOrUsername("abc") shouldBe true
    }

    "isValidEmailOrUsername debe rechazar strings vacíos" {
        FormValidator.isValidEmailOrUsername("") shouldBe false
        FormValidator.isValidEmailOrUsername("   ") shouldBe false
    }

    "isValidUsername debe aceptar usernames no vacíos" {
        FormValidator.isValidUsername("user123") shouldBe true
        FormValidator.isValidUsername("a") shouldBe true
        FormValidator.isValidUsername("user_name") shouldBe true
    }

    "isValidUsername debe rechazar strings vacíos" {
        FormValidator.isValidUsername("") shouldBe false
        FormValidator.isValidUsername("   ") shouldBe false
    }

    "isValidPassword debe aceptar passwords de 5+ caracteres" {
        FormValidator.isValidPassword("12345") shouldBe true
        FormValidator.isValidPassword("password123") shouldBe true
        FormValidator.isValidPassword("abc12") shouldBe true
    }

    "isValidPassword debe rechazar passwords de menos de 5 caracteres" {
        FormValidator.isValidPassword("") shouldBe false
        FormValidator.isValidPassword("1234") shouldBe false
        FormValidator.isValidPassword("abc") shouldBe false
    }

    "isValidEmailStrict debe validar emails con dominio completo" {
        FormValidator.isValidEmailStrict("test@gmail.com") shouldBe true
        FormValidator.isValidEmailStrict("user@example.co.uk") shouldBe true
    }

    "isValidEmailStrict debe rechazar emails sin dominio correcto" {
        FormValidator.isValidEmailStrict("") shouldBe false
        FormValidator.isValidEmailStrict("test") shouldBe false
        FormValidator.isValidEmailStrict("test@") shouldBe false
        FormValidator.isValidEmailStrict("test@gmail") shouldBe false
        FormValidator.isValidEmailStrict("@gmail.com") shouldBe false
        FormValidator.isValidEmailStrict("test@@gmail.com") shouldBe false
    }
})

