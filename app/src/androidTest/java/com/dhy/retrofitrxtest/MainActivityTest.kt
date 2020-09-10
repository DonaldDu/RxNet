package com.dhy.retrofitrxtest

import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dhy.retrofitrxutil.MultListenerDialog
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule
    @JvmField
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    private val idlingResource = SimpleIdlingResource()
    private lateinit var rootView: ViewGroup

    @Before
    fun registerIdlingResource() {
        activityScenarioRule.scenario.onActivity {
            rootView = it.findViewById(R.id.root)
            val dialog = MultListenerDialog.getInstance(it)
            dialog.setOnShowListener {
                idlingResource.setIdleState(false)
            }
            dialog.addOnDismissListener {
                idlingResource.setIdleState(true)
            }
            IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun okTest() {
        rootView.childCount.downTo(0).forEach {
            val id = rootView.getChildAt(it)?.id
            if (id != null && id != R.id.buttonFinish) onView(withId(id)).perform(click())
        }
    }

    @Test
    fun finishTest() {
        onView(withId(R.id.buttonFinish)).perform(click())
    }
}