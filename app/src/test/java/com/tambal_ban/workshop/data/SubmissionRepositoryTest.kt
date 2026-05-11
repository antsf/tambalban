package com.tambal_ban.workshop.data

import android.content.Context
import com.tambal_ban.core.network.SupabaseService
import com.tambal_ban.workshop.data.database.WorkshopDbHelper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SubmissionRepositoryTest {

    private val service = mockk<SupabaseService>()
    private val dbHelper = mockk<WorkshopDbHelper>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private lateinit var repository: WorkshopRepository

    private val submission = WorkshopSubmission(
        name = "Tambal Ban Maju",
        address = "Jl. Raya No.10",
        city = "Jakarta",
        lat = -6.2,
        lon = 106.8,
        phone = "08111222333"
    )

    private fun mockWorkshop() = Workshop(
        id = "ws-1",
        name = "Tambal Ban Maju",
        lat = -6.2,
        lon = 106.8
    )

    @Before
    fun setUp() {
        repository = WorkshopRepository(dbHelper, service)
    }

    @Test
    fun `addWorkshop success returns Result success with workshop`() = runTest {
        coEvery { service.addWorkshop(any()) } returns Response.success(listOf(mockWorkshop()))

        val result = repository.addWorkshop(submission, null, null, context)

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `addWorkshop error response returns Result failure`() = runTest {
        coEvery { service.addWorkshop(any()) } returns
            Response.error(400, "".toResponseBody("application/json".toMediaType()))

        val result = repository.addWorkshop(submission, null, null, context)

        assertTrue(result.isFailure)
    }

    @Test
    fun `addWorkshop network exception returns Result failure`() = runTest {
        coEvery { service.addWorkshop(any()) } throws java.io.IOException("No connection")

        val result = repository.addWorkshop(submission, null, null, context)

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `addWorkshop empty response body returns Result failure`() = runTest {
        coEvery { service.addWorkshop(any()) } returns Response.success(emptyList())

        val result = repository.addWorkshop(submission, null, null, context)

        assertTrue(result.isFailure)
    }

    @Test
    fun `addWorkshop with province and openingHours succeeds`() = runTest {
        val fullSubmission = submission.copy(
            province = "DKI Jakarta",
            openingHours = "08:00 - 20:00"
        )
        coEvery { service.addWorkshop(any()) } returns Response.success(listOf(mockWorkshop()))

        val result = repository.addWorkshop(fullSubmission, null, null, context)

        assertTrue(result.isSuccess)
    }
}
