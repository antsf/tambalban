package com.tambal_ban.workshop.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.tambal_ban.core.network.TambalBanApiService
import com.tambal_ban.workshop.data.database.WorkshopDbHelper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WorkshopRepositoryTest {

    private val service = mockk<TambalBanApiService>()
    private val dbHelper = mockk<WorkshopDbHelper>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val connectivityManager = mockk<ConnectivityManager>()
    private val network = mockk<Network>()
    private val capabilities = mockk<NetworkCapabilities>()
    private lateinit var repository: WorkshopRepository

    private fun mockWorkshop(id: String = "ws-1") = Workshop(
        id = id,
        name = "Tambal Ban Jaya",
        lat = -6.2,
        lon = 106.8,
        phone = "08123456789",
        address = "Jl. Test No.1"
    )

    @Before
    fun setUp() {
        repository = WorkshopRepository(dbHelper, service)
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
    }

    @Test
    fun `getWorkshopsInBounds success returns workshop list`() = runTest {
        val workshops = listOf(mockWorkshop())
        coEvery { service.getWorkshops(any(), any(), any(), any(), any()) } returns Response.success(workshops)

        val result = repository.getWorkshopsInBounds(-6.3, -6.1, 106.7, 106.9, context)

        assertEquals(1, result.size)
        assertEquals("ws-1", result.first().id)
    }

    @Test
    fun `getWorkshopsInBounds API failure falls back to local DB empty`() = runTest {
        coEvery { service.getWorkshops(any(), any(), any(), any(), any()) } returns
            Response.error(500, "".toResponseBody("application/json".toMediaType()))
        every { dbHelper.getWorkshopsInBounds(any(), any(), any(), any()) } returns null

        val result = repository.getWorkshopsInBounds(-6.3, -6.1, 106.7, 106.9, context)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getWorkshopsInBounds network exception falls back to local DB`() = runTest {
        coEvery { service.getWorkshops(any(), any(), any(), any(), any()) } throws
            java.io.IOException("No network")
        every { dbHelper.getWorkshopsInBounds(any(), any(), any(), any()) } returns null

        val result = repository.getWorkshopsInBounds(-6.3, -6.1, 106.7, 106.9, context)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getWorkshopsInBounds offline uses local DB`() = runTest {
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false
        every { dbHelper.getWorkshopsInBounds(any(), any(), any(), any()) } returns null

        val result = repository.getWorkshopsInBounds(-6.3, -6.1, 106.7, 106.9, context)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getWorkshopById success returns workshop`() = runTest {
        val workshop = mockWorkshop()
        coEvery { service.getWorkshopById(any()) } returns Response.success(workshop)

        val result = repository.getWorkshopById("ws-1")

        assertNotNull(result)
        assertEquals("ws-1", result?.id)
    }

    @Test
    fun `getWorkshopById error response returns null`() = runTest {
        coEvery { service.getWorkshopById(any()) } returns
            Response.error(404, "".toResponseBody("application/json".toMediaType()))

        val result = repository.getWorkshopById("ws-1")

        assertNull(result)
    }

    @Test
    fun `getWorkshopById network exception returns null`() = runTest {
        coEvery { service.getWorkshopById(any()) } throws java.io.IOException("Timeout")

        val result = repository.getWorkshopById("ws-1")

        assertNull(result)
    }

    @Test
    fun `searchWorkshops success returns list`() = runTest {
        val workshops = listOf(mockWorkshop())
        coEvery { service.getWorkshops(search = any()) } returns Response.success(workshops)

        val result = repository.searchWorkshops("jaya", context)

        assertEquals(1, result.size)
    }

    @Test
    fun `searchWorkshops offline uses local DB`() = runTest {
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false
        every { dbHelper.readableDatabase } returns mockk(relaxed = true) {
            every { query(any(), any(), any(), any(), any(), any(), any()) } returns mockk(relaxed = true) {
                every { moveToNext() } returns false
                every { close() } returns Unit
            }
        }

        val result = repository.searchWorkshops("jaya", context)

        assertTrue(result.isEmpty())
    }
}
