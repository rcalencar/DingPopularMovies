package com.rcalencar.dingpopularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rcalencar.dingpopularmovies.repository.remote.MovieService;
import com.rcalencar.dingpopularmovies.repository.remote.Service;
import com.rcalencar.dingpopularmovies.repository.remote.model.Trailers;
import com.trello.rxlifecycle.components.support.RxFragment;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TrailersFragment extends RxFragment {
    private static final String LOG_TAG = "TrailersFragment";
    private String movieId;

    private Observable<Trailers> trailersCache;
    private TrailersAdapter trailersAdapter;
    private Observer<Trailers> trailersObserver;

    public TrailersFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // should use getArguments() instead of getActivity()
        movieId = getActivity().getIntent().getStringExtra(MovieDetailActivity.MOVIE_ID);
        if (movieId == null) {
            throw new RuntimeException("movieId is null");
        }

        trailersObserver = new Observer<Trailers>() {
            @Override
            public void onCompleted() {
                // nothing to see here
            }

            @Override
            public void onError(Throwable e) {
                Log.d(LOG_TAG, e.getLocalizedMessage(), e);
            }

            @Override
            public void onNext(Trailers result) {
                trailersAdapter.clear();
                trailersAdapter.add(result.getResults());
            }
        };

        loadTrailers();
        trailersAdapter = new TrailersAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trailers, container, false);

        RecyclerView trailersRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_movie_trailers);
        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(getContext());
        trailersRecyclerView.setLayoutManager(trailersLayoutManager);
        trailersRecyclerView.setAdapter(trailersAdapter);

        if (savedInstanceState != null) {
            if (trailersCache != null) {
                trailersCache.subscribe(trailersObserver);
            }
        }

        return rootView;
    }

    private void loadTrailers() {
        MovieService service = Service.createService(MovieService.API_URL, MovieService.class);
        trailersCache = service
                .trailers(movieId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindToLifecycle());
        trailersCache.subscribe(trailersObserver);
    }
}
