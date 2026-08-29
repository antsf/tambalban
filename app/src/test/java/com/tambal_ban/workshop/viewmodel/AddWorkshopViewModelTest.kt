package com.tambal_ban.workshop.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tambal_ban.TambalBanApp
import com.tambal_ban.auth.data.AuthRepository
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddWorkshopViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val workshopRepo = mockk<WorkshopRepository>()
    private val authRepo = mockk<AuthRepository>()
    private val app = mockk<TambalBanApp>(relaxed = true)
    private lateinit var viewModel: AddWorkshopViewModel

    private val sampleWorkshop = Workshop(
        id = "ws-1",
        name = "Tambal Ban Jaya",
        lat = -6.2,
        lon = 106.8,
        city = "Jakarta"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { app.workshopRepository } returns workshopRepo
        every { app.authRepository } returns authRepo
        every { authRepo.isLoggedIn() } returns true
        every { authRepo.getUserId() } returns "user-123"
        viewModel = AddWorkshopViewModel(app)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addWorkshop success updates submissionResult`() = runTest {
        coEvery { workshopRepo.addWorkshop(any(), any(), any()) } returns Result.success(sampleWorkshop)

        viewModel.addWorkshop(
            name = "Tambal Ban Jaya",
            address = "Jl. Sudirman No. 1",
            city = "Jakarta",
            lat = -6.2,
            lon = 106.8,
            phone = "08123456789"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.submissionResult.value!!.isSuccess)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `addWorkshop failure updates submissionResult with error`() = runTest {
        coEvery { workshopRepo.addWorkshop(any(), any(), any()) } returns Result.failure(Exception("Submit failed"))

        viewModel.addWorkshop(
            name = "Tambal Ban Jaya",
            address = "Jl. Sudirman No. 1",
            city = "Jakarta",
            lat = -6.2,
            lon = 106.8,
            phone = "08123456789"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.submissionResult.value!!.isFailure)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `addWorkshop sets isLoading false after completion`() = runTest {
        coEvery { workshopRepo.addWorkshop(any(), any(), any()) } returns Result.success(sampleWorkshop)

        viewModel.addWorkshop(
            name = "Tambal Ban Jaya",
            address = "Jl. Sudirman",
            city = "Jakarta",
            lat = -6.2,
            lon = 106.8,
            phone = "08123456789"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `validateForm with all valid fields returns true`() {
        val form = AddWorkshopFormState(
            name = "Tambal Ban Jaya",
            address = "Jl. Sudirman No. 1",
            city = "Jakarta",
            phone = "08123456789",
            lat = -6.2,
            lon = 106.8
        )

        val result = viewModel.validateForm(form)

        assertTrue(result)
        assertTrue(viewModel.formErrors.value!!.isEmpty())
    }

    @Test
    fun `validateForm with empty name sets name error`() {
        val form = AddWorkshopFormState(
            name = "",
            address = "Jl. Sudirman No. 1",
            city = "Jakarta",
            phone = "08123456789",
            lat = -6.2,
            lon = 106.8
        )

        val result = viewModel.validateForm(form)

        assertFalse(result)
        assertNotNull(viewModel.formErrors.value!!["name"])
    }

    @Test
    fun `validateForm with empty city sets city error`() {
        val form = AddWorkshopFormState(
            name = "Tambal Ban",
            address = "Jl. Sudirman",
            city = "",
            phone = "08123456789",
            lat = -6.2,
            lon = 106.8
        )

        val result = viewModel.validateForm(form)

        assertFalse(result)
        assertNotNull(viewModel.formErrors.value!!["city"])
    }

    @Test
    fun `validateForm with invalid phone format sets phone error`() {
        val form = AddWorkshopFormState(
            name = "Tambal Ban",
            address = "Jl. Sudirman",
            city = "Jakarta",
            phone = "abc",
            lat = -6.2,
            lon = 106.8
        )

        val result = viewModel.validateForm(form)

        assertFalse(result)
        assertNotNull(viewModel.formErrors.value!!["phone"])
    }

    @Test
    fun `validateForm with invalid latitude sets lat error`() {
        val form = AddWorkshopFormState(
            name = "Tambal Ban",
            address = "Jl. Sudirman",
            city = "Jakarta",
            phone = "08123456789",
            lat = 200.0,
            lon = 106.8
        )

        val result = viewModel.validateForm(form)

        assertFalse(result)
        assertNotNull(viewModel.formErrors.value!!["lat"])
    }

    @Test
    fun `checkAuth updates isLoggedIn LiveData`() {
        every { authRepo.isLoggedIn() } returns true

        viewModel.checkAuth()

        assertTrue(viewModel.isLoggedIn.value!!)
    }

    @Test
    fun `updateFormField name updates formState`() {
        viewModel.updateFormField("name", "New Workshop")

        assertEquals("New Workshop", viewModel.formState.value!!.name)
    }

    @Test
    fun `updateFormField city updates formState`() {
        viewModel.updateFormField("city", "Bandung")

        assertEquals("Bandung", viewModel.formState.value!!.city)
    }

    @Test
    fun `submitWorkshop with invalid form sets submissionResult failure`() {
        val form = AddWorkshopFormState(
            name = "",
            address = "",
            city = "",
            phone = "",
            lat = -6.2,
            lon = 106.8
        )

        viewModel.submitWorkshop(form)

        assertTrue(viewModel.submissionResult.value!!.isFailure)
    }
}
