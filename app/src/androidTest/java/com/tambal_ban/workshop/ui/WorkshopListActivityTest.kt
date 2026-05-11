package com.tambal_ban.workshop.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tambal_ban.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkshopListActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(WorkshopListActivity::class.java)

    @Test
    fun workshopListScreen_keyViewsVisible() {
        onView(withId(R.id.rvWorkshops)).check(matches(isDisplayed()))
        onView(withId(R.id.searchLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun searchField_visible() {
        onView(withId(R.id.etSearch)).check(matches(isDisplayed()))
    }

    @Test
    fun recyclerView_visible() {
        onView(withId(R.id.rvWorkshops)).check(matches(isDisplayed()))
    }

    @Test
    fun toolbar_visible() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }
}
