package com.tambal_ban.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.model.AuthResponse
import com.tambal_ban.data.model.User
import com.tambal_ban.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val authRepository = mockk<AuthRepository>()
    private val application = mockk<TambalBanApp>()
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        coEvery { application.authRepository } returns authRepository
        viewModel = RegisterViewModel(application)
    }

    @Test
    fun `register success updates registerResult`() {
        val user = User("1", "test@example.com")
        val response = AuthResponse("access", "refresh", user)
        coEvery { authRepository.register(any(), any(), any()) } returns Result.success(response)

        viewModel.register("Test User", "test@example.com", "password")

        assertEquals(Result.success(response), viewModel.registerResult.value)
    }

    @Test
    fun `register failure updates registerResult with error`() {
        val exception = Exception("Registration failed")
        coEvery { authRepository.register(any(), any(), any()) } returns Result.failure(exception)

        viewModel.register("Test User", "test@example.com", "password")

        assertEquals(Result.failure<AuthResponse>(exception), viewModel.registerResult.value)
    }
}
