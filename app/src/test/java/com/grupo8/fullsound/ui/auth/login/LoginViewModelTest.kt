package com.grupo8.fullsound.ui.auth.login

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

class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Mock del loginResult
        val mockLoginResult = MutableLiveData<Resource<User>>()
        `when`(userRepository.loginResult).thenReturn(mockLoginResult)

        viewModel = LoginViewModel(userRepository)
    }

    @Test
    fun `validateForm with valid email and password should set isDataValid to true`() {
        // Given
        val email = "test@gmail.com"
        val password = "12345"

        // When
        viewModel.validateForm(email, password)

        // Then
        val formState = viewModel.loginFormState.value
        assertNotNull(formState)
        assertTrue(formState!!.isDataValid)
        assertNull(formState.emailError)
        assertNull(formState.passwordError)
    }

    @Test
    fun `validateForm with empty email should show error`() {
        // Given
        val email = ""
        val password = "12345"

        // When
        viewModel.validateForm(email, password)

        // Then
        val formState = viewModel.loginFormState.value
        assertNotNull(formState)
        assertFalse(formState!!.isDataValid)
        assertEquals("Email o usuario inválido", formState.emailError)
    }

    @Test
    fun `validateForm with short password should show error`() {
        // Given
        val email = "test@gmail.com"
        val password = "1234" // Less than 5 characters

        // When
        viewModel.validateForm(email, password)

        // Then
        val formState = viewModel.loginFormState.value
        assertNotNull(formState)
        assertFalse(formState!!.isDataValid)
        assertEquals("La contraseña debe tener al menos 5 caracteres", formState.passwordError)
    }

    @Test
    fun `validateForm with valid username should set isDataValid to true`() {
        // Given
        val username = "testuser"
        val password = "12345"

        // When
        viewModel.validateForm(username, password)

        // Then
        val formState = viewModel.loginFormState.value
        assertNotNull(formState)
        assertTrue(formState!!.isDataValid)
        assertNull(formState.emailError)
        assertNull(formState.passwordError)
    }

    @Test
    fun `login should call repository login method`() {
        // Given
        val email = "test@gmail.com"
        val password = "12345"

        // When
        viewModel.login(email, password)

        // Then
        verify(userRepository).login(email, password)
    }
}

