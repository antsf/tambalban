package com.tambal_ban.auth.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
class ProfileActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ProfileActivity::class.java)

    @Test
    fun profileScreen_keyViewsVisible() {
        onView(withId(R.id.tvProfileName)).check(matches(isDisplayed()))
        onView(withId(R.id.tvProfileEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.btnEditProfile)).check(matches(isDisplayed()))
    }

    @Test
    fun avatarView_visible() {
        onView(withId(R.id.avatarView)).check(matches(isDisplayed()))
    }

    @Test
    fun editProfileButton_isEnabled() {
        onView(withId(R.id.btnEditProfile)).check(matches(isEnabled()))
    }

    @Test
    fun logoutButton_visible() {
        onView(withId(R.id.btnLogout)).check(matches(isDisplayed()))
    }

    @Test
    fun addWorkshopButton_visible() {
        onView(withId(R.id.btnAddWorkshop)).check(matches(isDisplayed()))
    }

    @Test
    fun themeToggle_visible() {
        onView(withId(R.id.switchTheme)).check(matches(isDisplayed()))
    }

    @Test
    fun editProfileButton_click_navigatesToEditProfile() {
        onView(withId(R.id.btnEditProfile)).perform(click())
        onView(withId(R.id.etFullName)).check(matches(isDisplayed()))
    }
}
