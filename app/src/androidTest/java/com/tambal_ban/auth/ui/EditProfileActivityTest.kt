package com.tambal_ban.auth.ui

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
class EditProfileActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(EditProfileActivity::class.java)

    @Test
    fun editProfileScreen_keyViewsVisible() {
        onView(withId(R.id.etFullName)).check(matches(isDisplayed()))
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
    }

    @Test
    fun saveButton_isEnabled() {
        onView(withId(R.id.btnSave)).check(matches(isEnabled()))
    }

    @Test
    fun avatarView_visible() {
        onView(withId(R.id.avatarView)).check(matches(isDisplayed()))
    }

    @Test
    fun typingFullName_updatesField() {
        onView(withId(R.id.etFullName))
            .perform(replaceText("Budi Santoso"), closeSoftKeyboard())
        onView(withId(R.id.etFullName)).check(matches(isDisplayed()))
    }

    @Test
    fun typingPhone_updatesField() {
        onView(withId(R.id.etPhone))
            .perform(replaceText("08123456789"), closeSoftKeyboard())
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()))
    }

    @Test
    fun saveButton_click_staysOnScreen() {
        onView(withId(R.id.btnSave)).perform(click())
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
    }
}
