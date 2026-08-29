package com.tambal_ban.map.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tambal_ban.TambalBanApp
import com.tambal_ban.auth.data.AuthRepository
import com.tambal_ban.auth.data.Profile
import com.tambal_ban.auth.data.ProfileRepository
import com.tambal_ban.core.location.LocationService
import com.tambal_ban.workshop.data.Workshop
import com.tambal_ban.workshop.data.WorkshopRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val workshopRepo = mockk<WorkshopRepository>()
    private val profileRepo = mockk<ProfileRepository>()
    private val authRepo = mockk<AuthRepository>()
    private val locationService = mockk<LocationService>(relaxed = true)
    private val app = mockk<TambalBanApp>()
    private lateinit var viewModel: MainViewModel

    private fun mockWorkshop(id: String = "ws-1") = Workshop(
        id = id,
        name = "Tambal Ban Jaya",
        lat = -6.2,
        lon = 106.8,
        phone = null,
        address = "Jl. Test"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(LocationService)
        every { app.workshopRepository } returns workshopRepo
        every { app.profileRepository } returns profileRepo
        every { app.authRepository } returns authRepo
        every { authRepo.isLoggedIn() } returns false
        every { authRepo.getUserId() } returns null
        every { LocationService.getInstance(app) } returns locationService
        every { locationService.location } returns mockk(relaxed = true)
        viewModel = MainViewModel(app)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(LocationService)
    }

    @Test
    fun `fetchNearbyWorkshops success updates workshops LiveData`() = runTest {
        val workshops = listOf(mockWorkshop())
        coEvery { workshopRepo.getNearbyWorkshops(any(), any(), any(), any(), any(), any()) } returns workshops

        viewModel.fetchNearbyWorkshops(-6.2, 106.8)
        advanceUntilIdle()

        assertEquals(workshops, viewModel.workshops.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `fetchNearbyWorkshops exception sets error and loading false`() = runTest {
        coEvery { workshopRepo.getNearbyWorkshops(any(), any(), any(), any(), any(), any()) } throws
            Exception("Network error")

        viewModel.fetchNearbyWorkshops(-6.2, 106.8)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value!!)
        assertEquals("Network error", viewModel.error.value)
    }

    @Test
    fun `fetchNearbyWorkshops sets loading false after completion`() = runTest {
        coEvery { workshopRepo.getNearbyWorkshops(any(), any(), any(), any(), any(), any()) } returns emptyList()

        viewModel.fetchNearbyWorkshops(-6.2, 106.8)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `fetchNearbyWorkshops sets sheetTitle to default`() = runTest {
        coEvery { workshopRepo.getNearbyWorkshops(any(), any(), any(), any(), any(), any()) } returns emptyList()

        viewModel.fetchNearbyWorkshops(-6.2, 106.8)
        advanceUntilIdle()

        assertEquals("Tambal Ban Terdekat", viewModel.sheetTitle.value)
    }

    @Test
    fun `searchWorkshops success updates workshops and sheetTitle`() = runTest {
        val workshops = listOf(mockWorkshop())
        coEvery { workshopRepo.searchWorkshops(any(), any(), any(), any()) } returns workshops

        viewModel.searchWorkshops("jaya")
        advanceUntilIdle()

        assertEquals(workshops, viewModel.workshops.value)
        assertEquals("Hasil Pencarian", viewModel.sheetTitle.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `fetchUserProfile not logged in sets userProfile null`() = runTest {
        every { authRepo.isLoggedIn() } returns false

        viewModel.fetchUserProfile()
        advanceUntilIdle()

        assertNull(viewModel.userProfile.value)
    }

    @Test
    fun `fetchUserProfile logged in success updates userProfile`() = runTest {
        val profile = Profile(id = "u1", fullName = "Budi", email = "budi@example.com")
        every { authRepo.isLoggedIn() } returns true
        coEvery { profileRepo.getProfile() } returns Result.success(profile)

        viewModel.fetchUserProfile()
        advanceUntilIdle()

        assertEquals(profile, viewModel.userProfile.value)
    }

    @Test
    fun `fetchUserProfile no userId returns null profile`() = runTest {
        every { authRepo.isLoggedIn() } returns true
        every { authRepo.getUserId() } returns null

        viewModel.fetchUserProfile()
        advanceUntilIdle()

        assertNull(viewModel.userProfile.value)
    }
}
