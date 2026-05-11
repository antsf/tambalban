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
class RegisterActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Test
    fun registerScreen_keyViewsVisible() {
        onView(withId(R.id.etName)).check(matches(isDisplayed()))
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()))
    }

    @Test
    fun registerButton_isEnabled() {
        onView(withId(R.id.btnRegister)).check(matches(isEnabled()))
    }

    @Test
    fun loginLink_visible() {
        onView(withId(R.id.tvLogin)).check(matches(isDisplayed()))
    }

    @Test
    fun typingAllFields_buttonRemainEnabled() {
        onView(withId(R.id.etName)).perform(replaceText("Budi"), closeSoftKeyboard())
        onView(withId(R.id.etEmail)).perform(replaceText("budi@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(replaceText("password123"), closeSoftKeyboard())
        onView(withId(R.id.btnRegister)).check(matches(isEnabled()))
    }

    @Test
    fun emptySubmit_staysOnRegisterScreen() {
        onView(withId(R.id.btnRegister)).perform(click())
        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()))
    }
}
