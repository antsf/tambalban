package com.tambal_ban.workshop.ui

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tambal_ban.R
import com.tambal_ban.core.utils.Constants
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkshopDetailActivityTest {

    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext,
        WorkshopDetailActivity::class.java
    ).apply {
        putExtra(Constants.EXTRA_WORKSHOP_ID, "test-workshop-id")
    }

    @get:Rule
    val activityRule = ActivityScenarioRule<WorkshopDetailActivity>(intent)

    @Test
    fun workshopDetailScreen_keyViewsVisible() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.scrollContent)).check(matches(isDisplayed()))
    }

    @Test
    fun workshopNameTextView_visible() {
        onView(withId(R.id.tvWorkshopName)).check(matches(isDisplayed()))
    }

    @Test
    fun callButton_visible() {
        onView(withId(R.id.btnCall)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateButton_visible() {
        onView(withId(R.id.btnNavigate)).check(matches(isDisplayed()))
    }

    @Test
    fun reviewsSection_visible() {
        onView(withId(R.id.rvReviews)).check(matches(isDisplayed()))
    }

    @Test
    fun ratingBar_visible() {
        onView(withId(R.id.ratingBar)).check(matches(isDisplayed()))
    }
}
