package com.rcalencar.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rcalencar.popularmovies.repository.remote.MovieService;
import com.rcalencar.popularmovies.repository.remote.Service;
import com.rcalencar.popularmovies.repository.remote.model.Movie;
import com.rcalencar.popularmovies.repository.remote.model.PopularMovies;
import com.trello.rxlifecycle.components.support.RxFragment;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivityFragment extends RxFragment implements Observer<PopularMovies>, MoviesAdapter.OnItemSelectedListener {
    private static final String LOG_TAG = "MainActivityFragment";
    private static final String SELECTED_KEY = "selected_position";
    private Observable<PopularMovies> cache;
    private MoviesAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int currentPage = 1;
    private OnFragmentInteractionListener fragmentInteractionListener;
    private LinearLayoutManager mLayoutManager;
    private int mPosition = RecyclerView.NO_POSITION;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Movie movie, View view, String path);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        adapter = new MoviesAdapter(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            adapter.clear();
            load(currentPage);
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_popular_movies_result);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager, currentPage) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                currentPage = page;
                load(currentPage);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        if (savedInstanceState == null || cache == null) {
            load(currentPage);
        } else {
            cache.subscribe(this);
        }

        return rootView;
    }

    private void localScroll() {
        if (mPosition != RecyclerView.NO_POSITION) {
            mLayoutManager.scrollToPosition(mPosition);
            mPosition = RecyclerView.NO_POSITION;
        }
    }

    @Override
    public void onDestroyView() {
        recyclerView.clearOnScrollListeners();
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            fragmentInteractionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mPosition = mLayoutManager.findFirstVisibleItemPosition();
        outState.putInt(SELECTED_KEY, mPosition);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    private void load(int page) {
        Log.d(LOG_TAG, "loading page " + page);
        swipeRefreshLayout.setRefreshing(true);
        MovieService service = Service.createService(MovieService.API_URL, MovieService.class);
        cache = service
                .popular(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindToLifecycle());
        cache.subscribe(this);
    }

    @Override
    public void onCompleted() {
        // nothing to see here
    }

    @Override
    public void onError(Throwable e) {
        Log.d(LOG_TAG, e.getLocalizedMessage(), e);
    }

    @Override
    public void onNext(PopularMovies popularMovies) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.add(popularMovies.getResults());
        localScroll();
    }

    @Override
    public void onItemSelected(Movie movie, View view, String path) {
        fragmentInteractionListener.onFragmentInteraction(movie, view, path);
    }
}
