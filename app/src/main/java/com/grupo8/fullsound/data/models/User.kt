package com.grupo8.fullsound.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val username: String,
    val password: String,
    val name: String,
    val role: String = "user",
    val profileImage: String? = null,
    val createdAt: Long
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)