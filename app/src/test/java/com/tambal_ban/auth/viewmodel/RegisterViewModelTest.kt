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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val authRepo = mockk<AuthRepository>()
    private val app = mockk<TambalBanApp>()
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { app.authRepository } returns authRepo
        viewModel = RegisterViewModel(app)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register success updates registerResult`() = runTest {
        val user = User("u1", "test@example.com")
        val response = AuthResponse("access-token", "2026-09-01T00:00:00.000Z", user)
        coEvery { authRepo.register(any(), any(), any()) } returns Result.success(response)

        viewModel.register("Test User", "test@example.com", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.registerResult.value!!.isSuccess)
        assertEquals(response, viewModel.registerResult.value!!.getOrNull())
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `register failure updates registerResult with error`() = runTest {
        coEvery { authRepo.register(any(), any(), any()) } returns Result.failure(Exception("Email already taken"))

        viewModel.register("Test User", "taken@example.com", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.registerResult.value!!.isFailure)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `validateAndRegister with empty name sets validation error`() {
        viewModel.validateAndRegister("", "test@example.com", "password123")

        val error = viewModel.validationError.value
        assertNotNull(error)
        assertEquals("name", error!!.first)
    }

    @Test
    fun `validateAndRegister with empty email sets validation error`() {
        viewModel.validateAndRegister("Name", "", "password123")

        val error = viewModel.validationError.value
        assertNotNull(error)
        assertEquals("email", error!!.first)
    }

    @Test
    fun `validateAndRegister with invalid email sets validation error`() {
        viewModel.validateAndRegister("Name", "not-an-email", "password123")

        val error = viewModel.validationError.value
        assertNotNull(error)
        assertEquals("email", error!!.first)
    }

    @Test
    fun `validateAndRegister with short password sets validation error`() {
        viewModel.validateAndRegister("Name", "test@example.com", "short")

        val error = viewModel.validationError.value
        assertNotNull(error)
        assertEquals("password", error!!.first)
    }

    @Test
    fun `validateAndRegister with valid inputs clears validation error`() = runTest {
        val user = User("u1", "test@example.com")
        val response = AuthResponse("access-token", "2026-09-01T00:00:00.000Z", user)
        coEvery { authRepo.register(any(), any(), any()) } returns Result.success(response)

        viewModel.validateAndRegister("Test User", "test@example.com", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.validationError.value)
    }

    @Test
    fun `register sets isLoading false after completion`() = runTest {
        val user = User("u1", "test@example.com")
        val response = AuthResponse("access-token", "2026-09-01T00:00:00.000Z", user)
        coEvery { authRepo.register(any(), any(), any()) } returns Result.success(response)

        viewModel.register("Test User", "test@example.com", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoading.value!!)
    }
}
