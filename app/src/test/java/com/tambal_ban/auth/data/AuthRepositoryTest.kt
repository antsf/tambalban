package com.tambal_ban.auth.data

import com.tambal_ban.core.network.SupabaseService
import com.tambal_ban.core.utils.AuthPrefs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {

    private val service = mockk<SupabaseService>()
    private val authPrefs = mockk<AuthPrefs>()
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        repository = AuthRepository(service, authPrefs)
        every { authPrefs.saveAccessToken(any()) } just runs
        every { authPrefs.saveRefreshToken(any()) } just runs
        every { authPrefs.saveUserId(any()) } just runs
        every { authPrefs.saveEmail(any()) } just runs
    }

    @Test
    fun `login success saves tokens and returns AuthResponse`() = runTest {
        val user = User("u1", "test@example.com")
        val authResponse = AuthResponse("access", "refresh", user)
        coEvery { service.login(any()) } returns Response.success(authResponse)

        val result = repository.login("test@example.com", "password")

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        verify { authPrefs.saveAccessToken("access") }
        verify { authPrefs.saveRefreshToken("refresh") }
        verify { authPrefs.saveUserId("u1") }
        verify { authPrefs.saveEmail("test@example.com") }
    }

    @Test
    fun `login error response returns failure`() = runTest {
        coEvery { service.login(any()) } returns Response.error(
            401, "".toResponseBody("application/json".toMediaType())
        )

        val result = repository.login("test@example.com", "wrong")

        assertTrue(result.isFailure)
    }

    @Test
    fun `login network exception returns failure`() = runTest {
        coEvery { service.login(any()) } throws java.io.IOException("Network error")

        val result = repository.login("test@example.com", "password")

        assertTrue(result.isFailure)
    }

    @Test
    fun `register success saves tokens and returns AuthResponse`() = runTest {
        val user = User("u1", "test@example.com")
        val authResponse = AuthResponse("access", "refresh", user)
        coEvery { service.register(any()) } returns Response.success(authResponse)

        val result = repository.register("Test User", "test@example.com", "password")

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        verify { authPrefs.saveAccessToken("access") }
        verify { authPrefs.saveEmail("test@example.com") }
    }
}
