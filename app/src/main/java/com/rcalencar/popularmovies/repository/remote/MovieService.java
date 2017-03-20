package com.rcalencar.popularmovies.repository.remote;

import com.rcalencar.popularmovies.repository.remote.model.Movie;
import com.rcalencar.popularmovies.repository.remote.model.PopularMovies;
import com.rcalencar.popularmovies.repository.remote.model.Posters;
import com.rcalencar.popularmovies.repository.remote.model.Trailers;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface MovieService {
    public static final String API_URL = "http://api.themoviedb.org/";
    public static final String HTTP_IMAGE_TMDB_ORG_T_P_W185 = "http://image.tmdb.org/t/p/w185/";
    public static final String HTTP_IMAGE_TMDB_ORG_T_P_W500 = "http://image.tmdb.org/t/p/w500/";

    @GET("3/movie/popular")
    Observable<PopularMovies> popular(@Query("page") int page);

    @GET("3/movie/{id}")
    Observable<Movie> movie(@Path("id") String id);

    @GET("3/movie/{id}/videos")
    Observable<Trailers> trailers(@Path("id") String id);

    @GET("3/movie/{id}/images")
    Observable<Posters> images(@Path("id") String id);
}