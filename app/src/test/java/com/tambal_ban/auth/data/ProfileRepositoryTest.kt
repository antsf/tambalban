package com.tambal_ban.auth.data

import com.tambal_ban.core.network.TambalBanApiService
import com.tambal_ban.core.network.UploadResponse
import io.mockk.coEvery
import io.mockk.mockk
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

    private val service = mockk<TambalBanApiService>()
    private lateinit var repository: ProfileRepository

    @Before
    fun setUp() {
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
        coEvery { service.getProfile() } returns Response.success(profile)

        val result = repository.getProfile()

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getProfile error response returns failure`() = runBlocking {
        coEvery { service.getProfile() } returns Response.error(
            401, "".toResponseBody("application/json".toMediaType())
        )

        val result = repository.getProfile()

        assertTrue(result.isFailure)
    }

    @Test
    fun `updateProfile success returns the updated profile`() = runBlocking {
        val updated = Profile(id = "u1", fullName = "Updated", email = "test@example.com")
        coEvery { service.updateProfile(any()) } returns Response.success(updated)

        val result = repository.updateProfile(mapOf("full_name" to "Updated"))

        assertTrue(result.isSuccess)
        assertEquals("Updated", result.getOrNull()?.fullName)
    }

    @Test
    fun `uploadAvatar success returns the R2 URL`() = runBlocking {
        coEvery { service.uploadAvatarImage(any()) } returns
            Response.success(UploadResponse("https://tambalban-web.antsf.workers.dev/images/avatars/x.webp"))

        val result = repository.uploadAvatar(ByteArray(8), "image/png")

        assertTrue(result.isSuccess)
        assertEquals("https://tambalban-web.antsf.workers.dev/images/avatars/x.webp", result.getOrNull())
    }
}
