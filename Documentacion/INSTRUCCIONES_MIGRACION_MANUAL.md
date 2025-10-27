documentacion generada por IA

# INSTRUCCIONES PARA COMPLETAR LA MIGRACIÓN MVVM

## ⚠️ ESTADO ACTUAL

Los archivos nuevos de la estructura MVVM se han creado, pero pueden no estar en las ubicaciones correctas debido a limitaciones del sistema de archivos.

## 📋 PASOS MANUALES REQUERIDOS

### Paso 1: Crear la Estructura de Carpetas

Crear manualmente estas carpetas en:
`app/src/main/java/com/grupo8/fullsound/ui/`

```
ui/
├── auth/
│   ├── login/
│   └── register/
├── beats/
├── ajustes/
└── carrito/
```

### Paso 2: Mover Archivos a Nueva Estructura

#### A. Módulo Login (ui/auth/login/)
Crear o mover estos archivos a `ui/auth/login/`:

**LoginFragment.kt:**
```kotlin
package com.grupo8.fullsound.ui.auth.login

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.grupo8.fullsound.R
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.data.repositories.UserRepository
import com.grupo8.fullsound.databinding.FragmentLoginBinding
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.UserSession

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val userRepository = UserRepository(database.userDao())
        LoginViewModelFactory(userRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startRgbTitleAnimation()
        setupObservers()
        setupListeners()
    }

    private fun startRgbTitleAnimation() {
        val textView: TextView = binding.txtTitulo
        val colors = intArrayOf(Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED)
        val animator = ValueAnimator.ofFloat(0f, (colors.size - 1).toFloat())
        animator.duration = 4000L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { animation ->
            val position = animation.animatedValue as Float
            val index = position.toInt()
            val fraction = position - index
            val color = ArgbEvaluator().evaluate(fraction, colors[index], colors[(index + 1) % colors.size]) as Int
            textView.setTextColor(color)
        }
        animator.start()
    }

    private fun setupObservers() {
        viewModel.loginFormState.observe(viewLifecycleOwner, Observer { formState ->
            binding.loginButton.isEnabled = formState.isDataValid
            binding.emailInput.error = formState.emailError
            binding.passwordInput.error = formState.passwordError
        })

        viewModel.loginResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loginButton.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val user = result.data
                    if (user != null) {
                        val userSession = UserSession(requireContext())
                        userSession.saveUserSession(userId = user.id.hashCode(), email = user.email, username = user.username)
                    }
                    showMessage("Inicio de sesión exitoso")
                    findNavController().navigate(R.id.action_loginFragment_to_beatsFragment)
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    showMessage(result.message ?: "Error desconocido")
                }
                else -> {}
            }
        })
    }

    private fun setupListeners() {
        binding.emailEditText.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateForm() }
        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateForm() }
        binding.loginButton.setOnClickListener {
            validateForm()
            if (viewModel.loginFormState.value?.isDataValid == true) {
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                viewModel.login(email, password)
            }
        }
        binding.registerText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun validateForm() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        viewModel.validateForm(email, password)
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class LoginViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

**LoginViewModel.kt:**
```kotlin
package com.grupo8.fullsound.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo8.fullsound.data.repositories.UserRepository
import com.grupo8.fullsound.data.models.User
import com.grupo8.fullsound.utils.Resource

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginFormState
    val loginResult: LiveData<Resource<User>> = userRepository.loginResult

    fun login(emailOrUsername: String, password: String) {
        userRepository.login(emailOrUsername, password)
    }

    fun validateForm(emailOrUsername: String, password: String) {
        val emailOrUsernameValid = isValidEmailOrUsername(emailOrUsername)
        val passwordValid = isPasswordValid(password)
        _loginFormState.value = LoginFormState(
            emailError = if (!emailOrUsernameValid) "Email o usuario inválido" else null,
            passwordError = if (!passwordValid) "La contraseña debe tener al menos 6 caracteres" else null,
            isDataValid = emailOrUsernameValid && passwordValid
        )
    }

    private fun isValidEmailOrUsername(emailOrUsername: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches() || emailOrUsername.length >= 3
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
}

data class LoginFormState(
    val emailError: String? = null,
    val passwordError: String? = null,
    val isDataValid: Boolean = false
)
```

#### B. Módulo Register (ui/auth/register/)
Copiar el contenido similar para RegisterFragment.kt y RegisterViewModel.kt

#### C. Módulo Ajustes (ui/ajustes/)
Copiar el contenido de AjustesFragment.kt

### Paso 3: Eliminar Carpetas Antiguas

Una vez que los archivos estén en las nuevas ubicaciones, eliminar:
- `ui/Login/`
- `ui/Beats/` (si existe separado de `ui/beats/`)
- `ui/Ajustes/`

### Paso 4: Rebuild del Proyecto

En Android Studio:
1. File > Invalidate Caches / Restart
2. Build > Rebuild Project

O desde terminal:
```bash
./gradlew clean build
```

### Paso 5: Verificar Errores

Después del rebuild, no deberían existir errores de:
- "Unresolved class"
- "Package does not exist"
- Problemas de navegación

## ✅ CHECKLIST FINAL

- [ ] Carpetas `ui/auth/login/` y `ui/auth/register/` creadas
- [ ] LoginFragment.kt y LoginViewModel.kt en `ui/auth/login/`
- [ ] RegisterFragment.kt y RegisterViewModel.kt en `ui/auth/register/`
- [ ] AjustesFragment.kt en `ui/ajustes/`
- [ ] Carpetas antiguas (Login/, Beats/, Ajustes/) eliminadas
- [ ] nav_graph.xml apunta a paquetes correctos (ya actualizado)
- [ ] Layouts tienen tools:context correctos (ya actualizado)
- [ ] Proyecto reconstruido sin errores

## 📝 NOTAS

- Los archivos de código ya están preparados con los paquetes correctos
- Los layouts XML ya están actualizados
- nav_graph.xml ya tiene las referencias correctas
- Solo falta la reorganización física de archivos
