package com.grupo8.fullsound.ui.auth.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.grupo8.fullsound.data.models.User
import com.grupo8.fullsound.data.repositories.UserRepository
import com.grupo8.fullsound.utils.Resource
import androidx.lifecycle.MutableLiveData
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Mock del registerResult
        val mockRegisterResult = MutableLiveData<Resource<User>>()
        `when`(userRepository.registerResult).thenReturn(mockRegisterResult)

        viewModel = RegisterViewModel(userRepository)
    }

    @Test
    fun `validateForm with valid data should set isDataValid to true`() {
        // Given
        val email = "test@gmail.com"
        val username = "testuser"
        val password = "12345"

        // When
        viewModel.validateForm(email, username, password)

        // Then
        val formState = viewModel.registerFormState.value
        assertNotNull(formState)
        assertTrue(formState!!.isDataValid)
        assertNull(formState.emailError)
        assertNull(formState.usernameError)
        assertNull(formState.passwordError)
    }

    @Test
    fun `validateForm with invalid email should show error`() {
        // Given
        val email = "invalid-email" // No domain
        val username = "testuser"
        val password = "12345"

        // When
        viewModel.validateForm(email, username, password)

        // Then
        val formState = viewModel.registerFormState.value
        assertNotNull(formState)
        assertFalse(formState!!.isDataValid)
        assertEquals("Email inválido. Debe tener un dominio (ej: @gmail.com)", formState.emailError)
    }

    @Test
    fun `validateForm with email without domain should show error`() {
        // Given
        val email = "test@" // Missing domain
        val username = "testuser"
        val password = "12345"

        // When
        viewModel.validateForm(email, username, password)

        // Then
        val formState = viewModel.registerFormState.value
        assertNotNull(formState)
        assertFalse(formState!!.isDataValid)
        assertEquals("Email inválido. Debe tener un dominio (ej: @gmail.com)", formState.emailError)
    }

    @Test
    fun `validateForm with empty username should show error`() {
        // Given
        val email = "test@gmail.com"
        val username = "" // Empty
        val password = "12345"

        // When
        viewModel.validateForm(email, username, password)

        // Then
        val formState = viewModel.registerFormState.value
        assertNotNull(formState)
        assertFalse(formState!!.isDataValid)
        assertEquals("El usuario no puede estar vacío", formState.usernameError)
    }

    @Test
    fun `validateForm with short password should show error`() {
        // Given
        val email = "test@gmail.com"
        val username = "testuser"
        val password = "1234" // Less than 5 characters

        // When
        viewModel.validateForm(email, username, password)

        // Then
        val formState = viewModel.registerFormState.value
        assertNotNull(formState)
        assertFalse(formState!!.isDataValid)
        assertEquals("La contraseña debe tener al menos 5 caracteres", formState.passwordError)
    }

    @Test
    fun `validateForm with single character username should be valid`() {
        // Given
        val email = "test@gmail.com"
        val username = "a" // Single character
        val password = "12345"

        // When
        viewModel.validateForm(email, username, password)

        // Then
        val formState = viewModel.registerFormState.value
        assertNotNull(formState)
        assertTrue(formState!!.isDataValid)
    }

    @Test
    fun `validateForm with exactly 5 character password should be valid`() {
        // Given
        val email = "test@gmail.com"
        val username = "testuser"
        val password = "12345" // Exactly 5 characters

        // When
        viewModel.validateForm(email, username, password)

        // Then
        val formState = viewModel.registerFormState.value
        assertNotNull(formState)
        assertTrue(formState!!.isDataValid)
    }

    @Test
    fun `register should call repository register method`() {
        // Given
        val email = "test@gmail.com"
        val username = "testuser"
        val password = "12345"

        // When
        viewModel.register(email, username, password)

        // Then
        verify(userRepository).register(email, username, password, username)
    }
}

