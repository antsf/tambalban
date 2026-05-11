package com.tambal_ban.auth.data

import com.tambal_ban.core.network.SupabaseService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ProfileRepositoryTest {

    private val service = mockk<SupabaseService>()
    private lateinit var repository: ProfileRepository

    @Before
    fun setUp() {
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        repository = ProfileRepository(service)
    }

    @Test
    fun `getProfile success returns profile`() = runBlocking {
        val profile = Profile(
            id = "u1",
            fullName = "Test User",
            email = "test@example.com",
            phone = "08123456789",
            avatarUrl = null,
        )
        coEvery { service.getProfile(any<String>(), any<String>()) } returns Response.success(listOf(profile))

        val result = repository.getProfile("u1")

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getProfile empty response returns failure`() = runBlocking {
        coEvery { service.getProfile("eq.u1", "*") } returns Response.success(emptyList())

        val result = repository.getProfile("u1")

        assertTrue(result.isFailure)
    }

    @Test
    fun `getProfile error response returns failure`() = runBlocking {
        coEvery { service.getProfile("eq.u1", "*") } returns Response.error(
            500, "".toResponseBody("application/json".toMediaType())
        )

        val result = repository.getProfile("u1")

        assertTrue(result.isFailure)
    }

    @Test
    fun `updateProfile success returns unit`() = runBlocking {
        coEvery { service.updateProfile(any(), any()) } returns Response.success<Void>(null)

        val result = repository.updateProfile("u1", mapOf("full_name" to "Updated"))

        assertTrue(result.isSuccess)
    }
}
