package com.tambal_ban.map.ui

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
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainScreen_keyViewsVisible() {
        onView(withId(R.id.mapView)).check(matches(isDisplayed()))
        onView(withId(R.id.bottomSheet)).check(matches(isDisplayed()))
    }

    @Test
    fun mapView_visible() {
        onView(withId(R.id.mapView)).check(matches(isDisplayed()))
    }

    @Test
    fun myLocationFab_visible() {
        onView(withId(R.id.fabMyLocation)).check(matches(isDisplayed()))
    }

    @Test
    fun bottomSheet_visible() {
        onView(withId(R.id.bottomSheet)).check(matches(isDisplayed()))
    }

    @Test
    fun sheetTitle_visible() {
        onView(withId(R.id.tvSheetTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun nearbyWorkshopsRecyclerView_visible() {
        onView(withId(R.id.rvWorkshopsNearby)).check(matches(isDisplayed()))
    }
}
