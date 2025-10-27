package com.grupo8.fullsound.ui.auth.register

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.grupo8.fullsound.R
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.data.repositories.UserRepository
import com.grupo8.fullsound.databinding.FragmentRegisterBinding
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.UserSession

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
        val colors = intArrayOf(
            Color.RED,
            Color.MAGENTA,
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.YELLOW,
            Color.RED
        )
        val animator = ValueAnimator.ofFloat(0f, (colors.size - 1).toFloat())
        animator.duration = 4000L
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
                    // Guardar sesión del usuario recién registrado
                    val user = result.data
                    if (user != null) {
                        val userSession = UserSession(requireContext())
                        userSession.saveUserSession(
                            userId = user.id.hashCode(),
                            email = user.email,
                            username = user.username
                        )
                    }

                    showMessage("Registro exitoso")
                    // Navegar a Beats después de registrarse
                    findNavController().navigate(R.id.action_registerFragment_to_beatsFragment)
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
        binding.emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateForm()
            }
        }

        binding.usernameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateForm()
            }
        }

        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateForm()
            }
        }

        binding.registerButton.setOnClickListener {
            validateForm()
            if (viewModel.registerFormState.value?.isDataValid == true) {
                val email = binding.emailEditText.text.toString()
                val username = binding.usernameEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                viewModel.register(email, username, password)
            }
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
