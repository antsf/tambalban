package com.tambal_ban.workshop.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tambal_ban.TambalBanApp
import com.tambal_ban.auth.data.AuthRepository
import com.tambal_ban.workshop.data.Review
import com.tambal_ban.workshop.data.ReviewRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val workshopRepo = mockk<WorkshopRepository>()
    private val reviewRepo = mockk<ReviewRepository>()
    private val authRepo = mockk<AuthRepository>()
    private val app = mockk<TambalBanApp>()
    private lateinit var viewModel: WorkshopDetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { app.workshopRepository } returns workshopRepo
        every { app.reviewRepository } returns reviewRepo
        every { app.authRepository } returns authRepo
        every { authRepo.isLoggedIn() } returns false
        viewModel = WorkshopDetailViewModel(app)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadWorkshop reviews success emits review list`() = runTest {
        val reviews = listOf(
            Review(workshopId = "ws-1", rating = 5, comment = "Bagus"),
            Review(workshopId = "ws-1", rating = 4, comment = "Mantap")
        )
        coEvery { workshopRepo.getWorkshopById(any()) } returns null
        coEvery { reviewRepo.getReviews(any()) } returns Result.success(reviews)

        viewModel.loadWorkshop("ws-1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(reviews, viewModel.reviews.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `loadWorkshop reviews success with empty list emits empty`() = runTest {
        coEvery { workshopRepo.getWorkshopById(any()) } returns null
        coEvery { reviewRepo.getReviews(any()) } returns Result.success(emptyList())

        viewModel.loadWorkshop("ws-1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.reviews.value!!.isEmpty())
    }

    @Test
    fun `loadWorkshop reviews failure emits empty list not null`() = runTest {
        coEvery { workshopRepo.getWorkshopById(any()) } returns null
        coEvery { reviewRepo.getReviews(any()) } returns Result.failure(Exception("API error"))

        viewModel.loadWorkshop("ws-1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.reviews.value!!.isEmpty())
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `loadWorkshop sets loading false after completion`() = runTest {
        coEvery { workshopRepo.getWorkshopById(any()) } returns null
        coEvery { reviewRepo.getReviews(any()) } returns Result.success(emptyList())

        viewModel.loadWorkshop("ws-1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoading.value!!)
    }
}
