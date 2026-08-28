package com.tambal_ban.auth.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tambal_ban.TambalBanApp
import com.tambal_ban.auth.data.AuthRepository
import com.tambal_ban.auth.data.Profile
import com.tambal_ban.auth.data.ProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
        coEvery { profileRepo.getProfile() } returns Result.success(profile)

        viewModel.getProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(profile, viewModel.profile.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `getProfile failure sets error`() = runTest {
        coEvery { profileRepo.getProfile() } returns Result.failure(Exception("Profile not found"))

        viewModel.getProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.profile.value)
        assertNotNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `getProfile when not logged in does nothing`() {
        every { authRepo.isLoggedIn() } returns false

        viewModel.getProfile()

        assertNull(viewModel.profile.value)
    }

    @Test
    fun `updateProfile success sets isUpdateSuccess`() = runTest {
        val updated = Profile(id = "u1", fullName = "New Name")
        coEvery { profileRepo.updateProfile(any()) } returns Result.success(updated)
        coEvery { profileRepo.getProfile() } returns Result.success(updated)

        viewModel.updateProfile("New Name", "08123456789")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.isUpdateSuccess.value!!)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `logout clears session`() = runTest {
        coEvery { authRepo.logout() } returns Unit

        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { authRepo.logout() }
        assertTrue(viewModel.isLoggedOut.value!!)
    }

    @Test
    fun `uploadAvatar success triggers profile update`() = runTest {
        val profile = Profile(id = "u1")
        coEvery { profileRepo.uploadAvatar(any(), any()) } returns Result.success("https://example.com/avatar.png")
        coEvery { profileRepo.updateProfile(any()) } returns Result.success(profile)
        coEvery { profileRepo.getProfile() } returns Result.success(profile)

        viewModel.uploadAvatar(byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte()), "image/png")
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isUploading.value!!)
        coVerify { profileRepo.uploadAvatar(any(), any()) }
    }
}
