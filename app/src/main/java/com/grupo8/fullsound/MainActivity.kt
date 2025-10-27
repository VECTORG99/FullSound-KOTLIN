package com.grupo8.fullsound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.grupo8.fullsound.utils.UserSession

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Verificar si hay una sesión activa
        val userSession = UserSession(this)
        if (userSession.isLoggedIn()) {
            // Si hay sesión activa, navegar directamente a Beats
            navController.navigate(R.id.beatsFragment)
        }
    }
}