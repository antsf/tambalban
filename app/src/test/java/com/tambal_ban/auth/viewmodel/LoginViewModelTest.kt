package com.tambal_ban.auth.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tambal_ban.TambalBanApp
import com.tambal_ban.auth.data.AuthRepository
import com.tambal_ban.auth.data.AuthResponse
import com.tambal_ban.auth.data.User
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val authRepo = mockk<AuthRepository>()
    private val app = mockk<TambalBanApp>()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { app.authRepository } returns authRepo
        viewModel = LoginViewModel(app)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success updates loginResult`() = runTest {
        val user = User("u1", "test@example.com")
        val response = AuthResponse("access-token", "refresh-token", user)
        coEvery { authRepo.login(any(), any()) } returns Result.success(response)

        viewModel.login("test@example.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(Result.success(response), viewModel.loginResult.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `login failure updates loginResult with error`() = runTest {
        coEvery { authRepo.login(any(), any()) } returns Result.failure(Exception("Invalid credentials"))

        viewModel.login("wrong@email.com", "wrong")
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.loginResult.value?.isSuccess ?: true)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `login sets loading false after completion`() = runTest {
        val user = User("u1", "test@example.com")
        val response = AuthResponse("access-token", "refresh-token", user)
        coEvery { authRepo.login(any(), any()) } returns Result.success(response)

        viewModel.login("test@example.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoading.value!!)
    }
}
