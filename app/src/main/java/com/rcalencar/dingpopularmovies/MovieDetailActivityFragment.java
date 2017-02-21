package com.rcalencar.dingpopularmovies;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rcalencar.dingpopularmovies.repository.remote.MovieService;
import com.rcalencar.dingpopularmovies.repository.remote.Service;
import com.rcalencar.dingpopularmovies.repository.remote.model.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.Locale;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetailActivityFragment extends RxFragment {
    private static final String LOG_TAG = "MovieDetailActivityFrag";
    private Observable<Movie> cache;
    private String movieId;
    private Movie movie;
    private ImageView movie_thumbnail;
    private TextView movie_title;
    private TextView movie_release_year;
    private TextView movie_popularity;
    private TextView movie_genres;
    private TextView movie_overview;
    private String thumbNailPath;
    private Observer<Movie> movieObserver;
    private View movie_item_container;

    public MovieDetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // should use getArguments() instead of getActivity()
        movieId = getActivity().getIntent().getStringExtra(MovieDetailActivity.MOVIE_ID);
        thumbNailPath = getActivity().getIntent().getStringExtra(MovieDetailActivity.MOVIE_THUMBNAIL_PATH);
        if (movieId == null || thumbNailPath == null) {
            throw new RuntimeException("movieId or thumbNail is null");
        }

        movieObserver = new Observer<Movie>() {
            @Override
            public void onCompleted() {
                // nothing to see here
            }

            @Override
            public void onError(Throwable e) {
                Log.d(LOG_TAG, e.getLocalizedMessage(), e);
            }

            @Override
            public void onNext(Movie result) {
                movie = result;
                loadIntoView();
            }
        };

        loadMovie();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        movie_item_container = rootView.findViewById(R.id.movie_item_container);
        movie_title = (TextView) rootView.findViewById(R.id.movie_title);
        movie_release_year = (TextView) rootView.findViewById(R.id.movie_release_year);
        movie_popularity = (TextView) rootView.findViewById(R.id.movie_popularity);
        movie_genres = (TextView) rootView.findViewById(R.id.movie_genres);
        movie_overview = (TextView) rootView.findViewById(R.id.movie_overview);
        movie_thumbnail = (ImageView) rootView.findViewById(R.id.movie_thumbnail);

        Picasso.with(getContext())
                .load(thumbNailPath)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        movie_thumbnail.setImageBitmap(bitmap);
                        Palette.from(bitmap)
                                .generate(palette -> {
                                    Palette.Swatch swatch = palette.getDominantSwatch();
                                    if (swatch != null) {
                                        movie_item_container.setBackgroundColor(swatch.getRgb());
                                        movie_title.setTextColor(swatch.getTitleTextColor());
                                        movie_release_year.setTextColor(swatch.getBodyTextColor());
                                        movie_popularity.setTextColor(swatch.getBodyTextColor());
                                        movie_overview.setBackgroundColor(swatch.getBodyTextColor());
                                        movie_overview.setTextColor(swatch.getRgb());
                                        movie_genres.setTextColor(swatch.getBodyTextColor());
                                    }
                                });
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        if (savedInstanceState != null) {
            if (cache != null) {
                cache.subscribe(movieObserver);
            }
        }

        return rootView;
    }

    private void loadMovie() {
        MovieService service = Service.createService(MovieService.API_URL, MovieService.class);
        cache = service
                .movie(movieId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindToLifecycle());
        cache.subscribe(movieObserver);
    }

    private void loadIntoView() {
        movie_title.setText(movie.getOriginal_title());
        movie_release_year.setText(movie.getYear());
        movie_popularity.setText(String.format(Locale.getDefault(), "%s %.2f", getContext().getString(R.string.popularity), movie.getPopularity()));
        movie_overview.setText(movie.getOverview());
        movie_genres.setText(movie.genresAsString());
    }
}
