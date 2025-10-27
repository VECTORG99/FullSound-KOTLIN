package com.grupo8.fullsound.ui.auth.register

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.grupo8.fullsound.R
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.data.repositories.UserRepository
import com.grupo8.fullsound.databinding.FragmentRegisterBinding
import com.grupo8.fullsound.utils.Resource

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val userRepository = UserRepository(database.userDao())
        RegisterViewModelFactory(userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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
        viewModel.registerFormState.observe(viewLifecycleOwner) { formState ->
            binding.registerButton.isEnabled = formState.isDataValid

            binding.emailInput.error = formState.emailError
            binding.usernameInput.error = formState.usernameError
            binding.passwordInput.error = formState.passwordError
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.registerButton.isEnabled = false
                }
                is Resource.Success -> {
                    showMessage("Registro exitoso. Por favor, inicia sesión")
                    // Navegar a Login después de registrarse
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is Resource.Error -> {
                    binding.registerButton.isEnabled = true
                    showMessage(result.message ?: "Error en el registro")
                }
                else -> {}
            }
        }
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

        binding.usernameEditText.addTextChangedListener(object : android.text.TextWatcher {
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

        binding.registerButton.setOnClickListener {
            validateForm()
            if (viewModel.registerFormState.value?.isDataValid == true) {
                val email = binding.emailEditText.text.toString()
                val username = binding.usernameEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                viewModel.register(email, username, password)
            }
        }

        binding.loginText.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun validateForm() {
        val email = binding.emailEditText.text.toString()
        val username = binding.usernameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        viewModel.validateForm(email, username, password)
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class RegisterViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
