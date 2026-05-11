package com.tambal_ban.auth.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tambal_ban.TambalBanApp
import com.tambal_ban.auth.data.AuthRepository
import com.tambal_ban.auth.data.Profile
import com.tambal_ban.auth.data.ProfileRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
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
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val profileRepo = mockk<ProfileRepository>()
    private val authRepo = mockk<AuthRepository>()
    private val app = mockk<TambalBanApp>()
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { app.profileRepository } returns profileRepo
        every { app.authRepository } returns authRepo
        every { authRepo.isLoggedIn() } returns true
        viewModel = ProfileViewModel(app)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getProfile success updates profile`() = runTest {
        val profile = Profile(id = "u1", fullName = "Test User", email = "test@example.com", phone = "08123456789")
        every { authRepo.getUserId() } returns "u1"
        coEvery { profileRepo.getProfile(any()) } returns Result.success(profile)

        viewModel.getProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(profile, viewModel.profile.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `getProfile failure sets error`() = runTest {
        every { authRepo.getUserId() } returns "u1"
        coEvery { profileRepo.getProfile(any()) } returns Result.failure(Exception("Profile not found"))

        viewModel.getProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.profile.value)
        assertNotNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `getProfile when userId null does nothing`() {
        every { authRepo.getUserId() } returns null

        viewModel.getProfile()

        assertNull(viewModel.profile.value)
    }

    @Test
    fun `updateProfile success sets isUpdateSuccess`() = runTest {
        every { authRepo.getUserId() } returns "u1"
        coEvery { profileRepo.updateProfile(any(), any()) } returns Result.success(Unit)
        coEvery { profileRepo.getProfile(any()) } returns Result.success(Profile(id = "u1"))

        viewModel.updateProfile("New Name", "08123456789")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.isUpdateSuccess.value!!)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `logout clears session`() {
        every { authRepo.logout() } just runs

        viewModel.logout()

        verify { authRepo.logout() }
        assertTrue(viewModel.isLoggedOut.value!!)
    }

    @Test
    fun `uploadAvatar success triggers profile update`() = runTest {
        every { authRepo.getUserId() } returns "u1"
        coEvery { profileRepo.uploadAvatar(any(), any(), any()) } returns Result.success("https://example.com/avatar.png")
        coEvery { profileRepo.updateProfile(any(), any()) } returns Result.success(Unit)
        coEvery { profileRepo.getProfile(any()) } returns Result.success(Profile(id = "u1"))

        viewModel.uploadAvatar(byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte()), "image/png")
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isUploading.value!!)
        coVerify { profileRepo.uploadAvatar(any(), any(), any()) }
    }
}
