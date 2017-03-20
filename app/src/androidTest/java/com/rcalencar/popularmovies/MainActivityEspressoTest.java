package com.rcalencar.popularmovies;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {
    @Rule
    public IntentsTestRule<MainActivity> mActivity = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void changeText_newActivity() {
        onView(withId(R.id.recycler_view_popular_movies_result)).perform(actionOnItemAtPosition(0, click()));

        intended(hasComponent(MovieDetailActivity.class.getName()));
    }
}
