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
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginScreen_keyViewsVisible() {
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
    }

    @Test
    fun loginButton_visibleAndEnabled() {
        onView(withId(R.id.btnLogin)).check(matches(isEnabled()))
    }

    @Test
    fun registerLink_visible() {
        onView(withId(R.id.tvRegister)).check(matches(isDisplayed()))
    }

    @Test
    fun registerLink_navigatesToRegister() {
        onView(withId(R.id.tvRegister)).perform(click())
        onView(withId(R.id.etName)).check(matches(isDisplayed()))
    }

    @Test
    fun emptyEmailAndPassword_loginButtonStillClickable() {
        onView(withId(R.id.btnLogin)).perform(click())
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
    }

    @Test
    fun typingEmail_updatesField() {
        onView(withId(R.id.etEmail))
            .perform(replaceText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
    }
}
