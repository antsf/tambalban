package com.tambal_ban.core.update

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.tambal_ban.core.utils.AuthPrefs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class InAppUpdateManagerTest {

    private val authPrefs = mockk<AuthPrefs>()
    private val activity = mockk<Activity>()
    private lateinit var manager: InAppUpdateManager

    @Before
    fun setUp() {
        manager = InAppUpdateManager(authPrefs)
    }

    @After
    fun tearDown() {
        // no-op
    }

    private fun <T> mockSuccessTask(result: T): Task<T> {
        val task = mockk<Task<T>>(relaxed = true)
        every { task.addOnSuccessListener(any<OnSuccessListener<T>>()) } answers {
            firstArg<OnSuccessListener<T>>().onSuccess(result)
            task
        }
        every { task.addOnFailureListener(any<OnFailureListener>()) } returns task
        every { task.isSuccessful() } returns true
        return task
    }

    private fun <T> mockFailureTask(error: Exception): Task<T> {
        val task = mockk<Task<T>>(relaxed = true)
        every { task.addOnFailureListener(any<OnFailureListener>()) } answers {
            firstArg<OnFailureListener>().onFailure(error)
            task
        }
        every { task.addOnSuccessListener(any<OnSuccessListener<T>>()) } returns task
        every { task.isSuccessful() } returns false
        return task
    }

    @Test
    fun `checkForUpdate skips when no update available`() {
        val appUpdateManager = mockk<AppUpdateManager>(relaxed = true)
        val appUpdateInfo = mockk<AppUpdateInfo>()
        mockkStatic(AppUpdateManagerFactory::class)
        every { AppUpdateManagerFactory.create(activity) } returns appUpdateManager
        every { appUpdateManager.appUpdateInfo } returns mockSuccessTask(appUpdateInfo)
        every { appUpdateInfo.updateAvailability() } returns UpdateAvailability.UPDATE_NOT_AVAILABLE

        manager.checkForUpdate(activity)

        verify(exactly = 0) { authPrefs.setLastUpdatePromptTime(any()) }
        verify(exactly = 0) { appUpdateManager.startUpdateFlow(any(), any(), any<AppUpdateOptions>()) }
    }

    @Test
    fun `checkForUpdate skips when flexible update not allowed`() {
        val appUpdateManager = mockk<AppUpdateManager>(relaxed = true)
        val appUpdateInfo = mockk<AppUpdateInfo>()
        mockkStatic(AppUpdateManagerFactory::class)
        every { AppUpdateManagerFactory.create(activity) } returns appUpdateManager
        every { appUpdateManager.appUpdateInfo } returns mockSuccessTask(appUpdateInfo)
        every { appUpdateInfo.updateAvailability() } returns UpdateAvailability.UPDATE_AVAILABLE
        every { appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) } returns false

        manager.checkForUpdate(activity)

        verify(exactly = 0) { authPrefs.setLastUpdatePromptTime(any()) }
        verify(exactly = 0) { appUpdateManager.startUpdateFlow(any(), any(), any<AppUpdateOptions>()) }
    }

    @Test
    fun `checkForUpdate skips when prompted within 3 days`() {
        val appUpdateManager = mockk<AppUpdateManager>(relaxed = true)
        val appUpdateInfo = mockk<AppUpdateInfo>()
        mockkStatic(AppUpdateManagerFactory::class)
        every { AppUpdateManagerFactory.create(activity) } returns appUpdateManager
        every { appUpdateManager.appUpdateInfo } returns mockSuccessTask(appUpdateInfo)
        every { appUpdateInfo.updateAvailability() } returns UpdateAvailability.UPDATE_AVAILABLE
        every { appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) } returns true
        every { authPrefs.getLastUpdatePromptTime() } returns System.currentTimeMillis() - 24 * 60 * 60 * 1000

        manager.checkForUpdate(activity)

        verify(exactly = 0) { authPrefs.setLastUpdatePromptTime(any()) }
        verify(exactly = 0) { appUpdateManager.startUpdateFlow(any(), any(), any<AppUpdateOptions>()) }
    }

    @Test
    fun `checkForUpdate starts flow when update available and cooldown passed`() {
        val appUpdateManager = mockk<AppUpdateManager>(relaxed = true)
        val appUpdateInfo = mockk<AppUpdateInfo>()
        mockkStatic(AppUpdateManagerFactory::class)
        every { AppUpdateManagerFactory.create(activity) } returns appUpdateManager
        every { appUpdateManager.appUpdateInfo } returns mockSuccessTask(appUpdateInfo)
        every { appUpdateInfo.updateAvailability() } returns UpdateAvailability.UPDATE_AVAILABLE
        every { appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) } returns true
        every { authPrefs.getLastUpdatePromptTime() } returns System.currentTimeMillis() - 10L * 24 * 60 * 60 * 1000
        every { authPrefs.setLastUpdatePromptTime(any()) } just runs
        every { appUpdateManager.startUpdateFlow(appUpdateInfo, activity, any<AppUpdateOptions>()) } returns mockSuccessTask(9001)

        manager.checkForUpdate(activity)

        verify { authPrefs.setLastUpdatePromptTime(any()) }
        verify { appUpdateManager.startUpdateFlow(appUpdateInfo, activity, any<AppUpdateOptions>()) }
    }

    @Test
    fun `checkForUpdate first time ever shows prompt`() {
        val appUpdateManager = mockk<AppUpdateManager>(relaxed = true)
        val appUpdateInfo = mockk<AppUpdateInfo>()
        mockkStatic(AppUpdateManagerFactory::class)
        every { AppUpdateManagerFactory.create(activity) } returns appUpdateManager
        every { appUpdateManager.appUpdateInfo } returns mockSuccessTask(appUpdateInfo)
        every { appUpdateInfo.updateAvailability() } returns UpdateAvailability.UPDATE_AVAILABLE
        every { appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) } returns true
        every { authPrefs.getLastUpdatePromptTime() } returns 0L
        every { authPrefs.setLastUpdatePromptTime(any()) } just runs
        every { appUpdateManager.startUpdateFlow(appUpdateInfo, activity, any<AppUpdateOptions>()) } returns mockSuccessTask(9001)

        manager.checkForUpdate(activity)

        verify { authPrefs.setLastUpdatePromptTime(any()) }
        verify { appUpdateManager.startUpdateFlow(appUpdateInfo, activity, any<AppUpdateOptions>()) }
    }

    @Test
    fun `checkForUpdate handles failure silently`() {
        val appUpdateManager = mockk<AppUpdateManager>(relaxed = true)
        mockkStatic(AppUpdateManagerFactory::class)
        every { AppUpdateManagerFactory.create(activity) } returns appUpdateManager
        every { appUpdateManager.appUpdateInfo } returns mockFailureTask<AppUpdateInfo>(RuntimeException("Play Store unavailable"))

        manager.checkForUpdate(activity)
    }

    @Test
    fun `checkForUpdate registers listener for download completion`() {
        val appUpdateManager = mockk<AppUpdateManager>(relaxed = true)
        val appUpdateInfo = mockk<AppUpdateInfo>()
        mockkStatic(AppUpdateManagerFactory::class)
        every { AppUpdateManagerFactory.create(activity) } returns appUpdateManager
        every { appUpdateManager.appUpdateInfo } returns mockSuccessTask(appUpdateInfo)
        every { appUpdateInfo.updateAvailability() } returns UpdateAvailability.UPDATE_AVAILABLE
        every { appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) } returns true
        every { authPrefs.getLastUpdatePromptTime() } returns System.currentTimeMillis() - 10L * 24 * 60 * 60 * 1000
        every { authPrefs.setLastUpdatePromptTime(any()) } just runs
        every { appUpdateManager.startUpdateFlow(appUpdateInfo, activity, any<AppUpdateOptions>()) } returns mockSuccessTask(9001)

        manager.checkForUpdate(activity)

        verify { appUpdateManager.registerListener(any()) }
    }
}
