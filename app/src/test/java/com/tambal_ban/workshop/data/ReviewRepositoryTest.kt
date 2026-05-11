package com.tambal_ban.workshop.data

import com.tambal_ban.core.network.SupabaseService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ReviewRepositoryTest {

    private val service = mockk<SupabaseService>()
    private lateinit var repository: ReviewRepository

    @Before
    fun setUp() {
        repository = ReviewRepository(service)
    }

    @Test
    fun `getReviews success returns list`() = runTest {
        val reviews = listOf(
            Review(workshopId = "ws-1", rating = 5, comment = "Good")
        )
        coEvery { service.getReviews(any()) } returns Response.success(reviews)

        val result = repository.getReviews("ws-1")

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getReviews error response returns failure`() = runTest {
        coEvery { service.getReviews(any()) } returns Response.error(
            404, "".toResponseBody("application/json".toMediaType())
        )

        val result = repository.getReviews("ws-1")

        assertTrue(result.isFailure)
    }

    @Test
    fun `getReviews network exception returns failure`() = runTest {
        coEvery { service.getReviews(any()) } throws java.io.IOException("No network")

        val result = repository.getReviews("ws-1")

        assertTrue(result.isFailure)
    }

    @Test
    fun `submitReview success returns unit`() = runTest {
        val review = Review(workshopId = "ws-1", rating = 5, comment = "Good")
        coEvery { service.submitReview(any()) } returns Response.success<Void>(null)

        val result = repository.submitReview(review)

        assertTrue(result.isSuccess)
    }
}
