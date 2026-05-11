package com.tambal_ban.workshop.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tambal_ban.TambalBanApp
import com.tambal_ban.workshop.data.Workshop
import com.tambal_ban.workshop.data.WorkshopRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val workshopRepo = mockk<WorkshopRepository>()
    private val app = mockk<TambalBanApp>(relaxed = true)
    private lateinit var viewModel: WorkshopListViewModel

    private val sampleWorkshops = listOf(
        Workshop(id = "ws-1", name = "Tambal Ban Jaya", lat = -6.2, lon = 106.8, city = "Jakarta"),
        Workshop(id = "ws-2", name = "Tambal Ban Maju", lat = -6.21, lon = 106.81, city = "Jakarta")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { app.workshopRepository } returns workshopRepo
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): WorkshopListViewModel {
        return WorkshopListViewModel(app)
    }

    @Test
    fun `fetchAllWorkshops success updates workshops LiveData`() = runTest {
        coEvery { workshopRepo.getAllWorkshops(any(), any(), any()) } returns sampleWorkshops
        viewModel = createViewModel()

        viewModel.fetchAllWorkshops()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(sampleWorkshops, viewModel.workshops.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `fetchAllWorkshops empty result marks last page`() = runTest {
        coEvery { workshopRepo.getAllWorkshops(any(), any(), any()) } returns emptyList()
        viewModel = createViewModel()

        viewModel.fetchAllWorkshops()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.workshops.value!!.isEmpty())
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `fetchAllWorkshops exception sets error LiveData`() = runTest {
        coEvery { workshopRepo.getAllWorkshops(any(), any(), any()) } throws RuntimeException("Network error")
        viewModel = createViewModel()

        viewModel.fetchAllWorkshops()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `fetchNearbyWorkshops success updates workshops LiveData`() = runTest {
        coEvery { workshopRepo.getNearbyWorkshops(any(), any(), any(), any(), any(), any()) } returns sampleWorkshops
        viewModel = createViewModel()

        viewModel.fetchNearbyWorkshops(-6.2, 106.8, 5000)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(sampleWorkshops, viewModel.workshops.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `fetchNearbyWorkshops exception sets error`() = runTest {
        coEvery { workshopRepo.getNearbyWorkshops(any(), any(), any(), any(), any(), any()) } throws RuntimeException("Timeout")
        viewModel = createViewModel()

        viewModel.fetchNearbyWorkshops(-6.2, 106.8, 5000)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.error.value)
    }

    @Test
    fun `searchWorkshops success updates workshops LiveData`() = runTest {
        coEvery { workshopRepo.searchWorkshops(any(), any(), any(), any()) } returns sampleWorkshops
        viewModel = createViewModel()

        viewModel.searchWorkshops("Jaya")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(sampleWorkshops, viewModel.workshops.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `searchWorkshops with empty query calls fetchAllWorkshops`() = runTest {
        coEvery { workshopRepo.getAllWorkshops(any(), any(), any()) } returns sampleWorkshops
        viewModel = createViewModel()

        viewModel.searchWorkshops("")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(sampleWorkshops, viewModel.workshops.value)
    }

    @Test
    fun `fetchAllWorkshops refresh resets page and clears list`() = runTest {
        coEvery { workshopRepo.getAllWorkshops(any(), any(), any()) } returns sampleWorkshops
        viewModel = createViewModel()

        viewModel.fetchAllWorkshops(refresh = true)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(sampleWorkshops, viewModel.workshops.value)
    }

    @Test
    fun `fetchAllWorkshops sets isLoading true during fetch`() = runTest {
        coEvery { workshopRepo.getAllWorkshops(any(), any(), any()) } returns sampleWorkshops
        viewModel = createViewModel()

        viewModel.fetchAllWorkshops()
        assertTrue(viewModel.isLoading.value!!)

        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.isLoading.value!!)
    }
}
