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
import com.rcalencar.dingpopularmovies.repository.remote.model.Posters;
import com.trello.rxlifecycle.components.support.RxFragment;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostersFragment extends RxFragment {
    private static final String LOG_TAG = "PostersFragment";
    private String movieId;
    private Observable<Posters> postersCache;
    private PostersAdapter postersAdapter;
    private Observer<Posters> postersObserver;

    public PostersFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // should use getArguments() instead of getActivity()
        movieId = getActivity().getIntent().getStringExtra(MovieDetailActivity.MOVIE_ID);
        if (movieId == null) {
            throw new RuntimeException("movieId is null");
        }

        postersObserver = new Observer<Posters>() {
            @Override
            public void onCompleted() {
                // nothing to see here
            }

            @Override
            public void onError(Throwable e) {
                Log.d(LOG_TAG, e.getLocalizedMessage(), e);
            }

            @Override
            public void onNext(Posters result) {
                postersAdapter.clear();
                postersAdapter.add(result.getPosters());
            }
        };

        loadPosters();

        postersAdapter = new PostersAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posters, container, false);

        RecyclerView postrsRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_movie_posters);
        LinearLayoutManager postersLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        postrsRecyclerView.setLayoutManager(postersLayoutManager);
        postrsRecyclerView.setAdapter(postersAdapter);

        if (savedInstanceState != null) {
            if (postersCache != null) {
                postersCache.subscribe(postersObserver);
            }
        }

        return rootView;
    }

    private void loadPosters() {
        MovieService service = Service.createService(MovieService.API_URL, MovieService.class);
        postersCache = service
                .images(movieId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindToLifecycle());
        postersCache.subscribe(postersObserver);
    }
}
