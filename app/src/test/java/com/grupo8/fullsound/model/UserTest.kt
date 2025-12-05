package com.grupo8.fullsound.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests para el modelo User adaptado a Supabase
 */
class UserTest : StringSpec({

    "User debe crearse con todos los campos requeridos" {
        val user = User(
            id = "123e4567-e89b-12d3-a456-426614174000",
            email = "test@test.com",
            username = "testuser",
            password = "password123",
            name = "Test User",
            rut = "12345678-9",
            role = "user",
            profileImage = null,
            createdAt = System.currentTimeMillis()
        )

        user.id shouldBe "123e4567-e89b-12d3-a456-426614174000"
        user.email shouldBe "test@test.com"
        user.username shouldBe "testuser"
        user.password shouldBe "password123"
        user.name shouldBe "Test User"
        user.rut shouldBe "12345678-9"
        user.role shouldBe "user"
    }

    "User debe tener rol por defecto 'user'" {
        val user = User(
            id = "123",
            email = "test@test.com",
            username = "testuser",
            password = "password123",
            name = "Test User",
            rut = "12345678-9",
            createdAt = System.currentTimeMillis()
        )

        user.role shouldBe "user"
    }

    "User admin debe tener email con dominio @admin.cl" {
        val adminUser = User(
            id = "456",
            email = "admin@admin.cl",
            username = "admin",
            password = "admin123",
            name = "Admin User",
            rut = "87654321-0",
            role = "admin",
            createdAt = System.currentTimeMillis()
        )

        adminUser.role shouldBe "admin"
        adminUser.email.endsWith("@admin.cl") shouldBe true
    }

    "User debe compararse correctamente por ID" {
        val timestamp = System.currentTimeMillis()
        val user1 = User(
            id = "123",
            email = "test@test.com",
            username = "testuser",
            password = "password123",
            name = "Test User",
            rut = "12345678-9",
            createdAt = timestamp
        )

        val user2 = User(
            id = "123",
            email = "test@test.com",
            username = "testuser",
            password = "password123",
            name = "Test User",
            rut = "12345678-9",
            createdAt = timestamp
        )

        val user3 = User(
            id = "456",
            email = "other@test.com",
            username = "otheruser",
            password = "password123",
            name = "Other User",
            rut = "87654321-0",
            createdAt = timestamp
        )

        user1 shouldBe user2
        user1 shouldNotBe user3
    }

    "User debe poder copiar con nuevos valores" {
        val original = User(
            id = "123",
            email = "test@test.com",
            username = "testuser",
            password = "password123",
            name = "Test User",
            rut = "12345678-9",
            createdAt = System.currentTimeMillis()
        )

        val actualizado = original.copy(
            name = "Updated Name",
            role = "admin"
        )

        actualizado.id shouldBe "123"
        actualizado.name shouldBe "Updated Name"
        actualizado.role shouldBe "admin"
        actualizado.email shouldBe "test@test.com"
    }

    "LoginRequest debe crearse correctamente" {
        val loginRequest = LoginRequest(
            email = "test@test.com",
            password = "password123"
        )

        loginRequest.email shouldBe "test@test.com"
        loginRequest.password shouldBe "password123"
    }

    "RegisterRequest debe crearse correctamente" {
        val registerRequest = RegisterRequest(
            email = "test@test.com",
            password = "password123",
            name = "Test User",
            rut = "12345678-9"
        )

        registerRequest.email shouldBe "test@test.com"
        registerRequest.password shouldBe "password123"
        registerRequest.name shouldBe "Test User"
        registerRequest.rut shouldBe "12345678-9"
    }
})

