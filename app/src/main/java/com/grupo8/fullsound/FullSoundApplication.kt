package com.grupo8.fullsound.utils

import android.app.Application
import com.grupo8.fullsound.data.remote.RetrofitClient

/**
 * Application class para inicializar componentes globales
 * Agregar en AndroidManifest.xml: android:name=".FullSoundApplication"
 */
class FullSoundApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar RetrofitClient con contexto de aplicación
        RetrofitClient.initialize(this)
    }
}
