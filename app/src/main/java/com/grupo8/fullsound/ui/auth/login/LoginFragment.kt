package com.grupo8.fullsound.ui.auth.login

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.grupo8.fullsound.R
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.repository.UserRepository
import com.grupo8.fullsound.viewmodel.LoginViewModel
import com.grupo8.fullsound.databinding.FragmentLoginBinding
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.UserSession
import com.grupo8.fullsound.utils.AnimationHelper

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val userRepository = UserRepository(database.userDao(), requireContext())
        LoginViewModelFactory(userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startRgbTitleAnimation()
        setupObservers()
        setupListeners()
        animateEntrance()
    }

    private fun animateEntrance() {
        // Animar elementos en secuencia
        val elementsToAnimate = listOf(
            binding.txtTitulo,
            binding.emailInput,
            binding.passwordInput,
            binding.loginButton,
            binding.registerText
        )
        AnimationHelper.animateListSequentially(elementsToAnimate, 80, AnimationHelper.AnimationType.FADE)
    }

    private fun startRgbTitleAnimation() {
        val textView: TextView = binding.txtTitulo
        // Animación RGB completa con todos los colores del arcoíris
        val colors = intArrayOf(
            "#FF0000".toColorInt(), // Rojo
            "#FF7F00".toColorInt(), // Naranja
            "#FFFF00".toColorInt(), // Amarillo
            "#00FF00".toColorInt(), // Verde
            "#0000FF".toColorInt(), // Azul
            "#4B0082".toColorInt(), // Índigo
            "#9400D3".toColorInt(), // Violeta
            "#FF0000".toColorInt()  // Rojo (volver al inicio)
        )
        val animator = ValueAnimator.ofFloat(0f, (colors.size - 1).toFloat())
        animator.duration = 7000L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { animation ->
            val position = animation.animatedValue as Float
            val index = position.toInt()
            val fraction = position - index
            val color = ArgbEvaluator().evaluate(
                fraction,
                colors[index],
                colors[(index + 1) % colors.size]
            ) as Int
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

                    // Guardar sesión del usuario
                    val user = result.data
                    if (user != null) {
                        val userSession = UserSession(requireContext())
                        userSession.saveUserSession(
                            userId = user.id.hashCode(),
                            email = user.email,
                            username = user.username
                        )
                    }

                    showMessage("Inicio de sesión exitoso")
                    // Navegar a la pantalla principal
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
        // Validación en tiempo real mientras el usuario escribe
        binding.emailEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                validateForm()
            }
        })

        binding.passwordEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                validateForm()
            }
        })

        binding.loginButton.setOnClickListener {
            AnimationHelper.animateClick(it)
            validateForm()
            if (viewModel.loginFormState.value?.isDataValid == true) {
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                viewModel.login(email, password)
            }
        }

        binding.registerText.setOnClickListener {
            AnimationHelper.animateClick(it)
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.txtOlvideContrasena.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            if (email.isBlank()) {
                showMessage("Ingrese email en campo")
            } else {
                showMessage("Correo enviado a tu usuario")
            }
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

class LoginViewModelFactory(private val userRepository: com.grupo8.fullsound.repository.UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(com.grupo8.fullsound.viewmodel.LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return com.grupo8.fullsound.viewmodel.LoginViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}