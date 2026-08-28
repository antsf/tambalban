package com.tambal_ban.auth.data

import com.tambal_ban.core.network.TambalBanApiService
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

    private val service = mockk<TambalBanApiService>()
    private val authPrefs = mockk<AuthPrefs>()
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        repository = AuthRepository(service, authPrefs)
        every { authPrefs.saveAccessToken(any()) } just runs
        every { authPrefs.saveUserId(any()) } just runs
        every { authPrefs.saveEmail(any()) } just runs
        every { authPrefs.clear() } just runs
    }

    @Test
    fun `login success saves token and returns AuthResponse`() = runTest {
        val user = User("u1", "test@example.com")
        val authResponse = AuthResponse("tok123", "2026-09-01T00:00:00.000Z", user)
        coEvery { service.login(any()) } returns Response.success(authResponse)

        val result = repository.login("test@example.com", "password")

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        verify { authPrefs.saveAccessToken("tok123") }
        verify { authPrefs.saveUserId("u1") }
        verify { authPrefs.saveEmail("test@example.com") }
    }

    @Test
    fun `login error response returns failure`() = runTest {
        coEvery { service.login(any()) } returns Response.error(
            400, "".toResponseBody("application/json".toMediaType())
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
    fun `register success saves token and returns AuthResponse`() = runTest {
        val user = User("u1", "test@example.com")
        val authResponse = AuthResponse("tok456", "2026-09-01T00:00:00.000Z", user)
        coEvery { service.register(any()) } returns Response.success(authResponse)

        val result = repository.register("Test User", "test@example.com", "password")

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        verify { authPrefs.saveAccessToken("tok456") }
        verify { authPrefs.saveEmail("test@example.com") }
    }

    @Test
    fun `logout calls the server then clears local prefs`() = runTest {
        coEvery { service.logout() } returns Response.success(Unit)

        repository.logout()

        coEvery { service.logout() }
        verify { authPrefs.clear() }
    }

    @Test
    fun `logout still clears local prefs even if the server call fails`() = runTest {
        coEvery { service.logout() } throws java.io.IOException("Network error")

        repository.logout()

        verify { authPrefs.clear() }
    }
}
