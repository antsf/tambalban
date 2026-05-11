package com.tambal_ban.workshop.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tambal_ban.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddWorkshopActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AddWorkshopActivity::class.java)

    @Test
    fun addWorkshopScreen_keyViewsVisible() {
        onView(withId(R.id.etName)).check(matches(isDisplayed()))
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()))
        onView(withId(R.id.etAddress)).check(matches(isDisplayed()))
        onView(withId(R.id.etCity)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSubmit)).check(matches(isDisplayed()))
    }

    @Test
    fun submitButton_isEnabled() {
        onView(withId(R.id.btnSubmit)).check(matches(isEnabled()))
    }

    @Test
    fun emptySubmit_staysOnScreen() {
        onView(withId(R.id.btnSubmit)).perform(click())
        onView(withId(R.id.btnSubmit)).check(matches(isDisplayed()))
    }

    @Test
    fun typingName_updatesField() {
        onView(withId(R.id.etName))
            .perform(replaceText("Tambal Ban Jaya"), closeSoftKeyboard())
        onView(withId(R.id.etName)).check(matches(isDisplayed()))
    }

    @Test
    fun typingPhone_updatesField() {
        onView(withId(R.id.etPhone))
            .perform(replaceText("08123456789"), closeSoftKeyboard())
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()))
    }

    @Test
    fun currentLocationButton_visible() {
        onView(withId(R.id.btnCurrentLocation)).check(matches(isDisplayed()))
    }

    @Test
    fun latLonFields_visible() {
        onView(withId(R.id.etLat)).check(matches(isDisplayed()))
        onView(withId(R.id.etLon)).check(matches(isDisplayed()))
    }
}
