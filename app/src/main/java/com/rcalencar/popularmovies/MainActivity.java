package com.rcalencar.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.rcalencar.popularmovies.repository.remote.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnFragmentInteractionListener {
    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Movie movie, View view, String path) {
        Intent intent = MovieDetailActivity.newIntentTo(this, movie.getId(), path);

        startActivity(intent, transitionOptions(this, view, R.string.transition_thumbnail));
    }

    public static Bundle transitionOptions(Activity activity, View transitionView, int transitionNameResId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return null;
        }
        View decorView = activity.getWindow().getDecorView();
        View statusBar = decorView.findViewById(android.R.id.statusBarBackground);
        View navigationBar = decorView.findViewById(android.R.id.navigationBarBackground);
        View appBarLayout = decorView.findViewById(R.id.app_bar_layout);
        String transitionName = activity.getString(transitionNameResId);

        List<Pair<View, String>> pairs = new ArrayList<>();
        if (statusBar != null) {
            pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
        }
        if (navigationBar != null) {
            pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
        }
        if (appBarLayout != null) {
            pairs.add(Pair.create(appBarLayout, activity.getString(R.string.transition_app_bar_layout)));
        }
        pairs.add(Pair.create(transitionView, transitionName));
        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs.toArray(new Pair[pairs.size()]))
                .toBundle();
    }
}
