package com.grupo8.fullsound.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Helper para gestionar permisos de almacenamiento
 */
object PermissionHelper {

    /**
     * Verifica si se tienen los permisos de almacenamiento
     */
    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ no requiere permisos para escribir en Downloads usando MediaStore
            true
        } else {
            // Android 9 y anteriores requieren WRITE_EXTERNAL_STORAGE
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Obtiene la lista de permisos necesarios según la versión de Android
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ no necesita permisos para MediaStore
            arrayOf()
        } else {
            // Android 9 y anteriores
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    /**
     * Verifica si se deben solicitar permisos
     */
    fun shouldRequestPermissions(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
    }
}

