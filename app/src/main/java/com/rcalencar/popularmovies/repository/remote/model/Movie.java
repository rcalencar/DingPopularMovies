package com.rcalencar.popularmovies.repository.remote.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Movie {
    private String id;
    private String original_title;
    private String poster_path;
    private String release_date;
    private String overview;
    private List<Genre> genres;
    private float popularity;

    public Movie() {
    }

    public String getYear() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(simpleDateFormat.parse(release_date));
            return String.valueOf(calendar.get(Calendar.YEAR));
        } catch (ParseException e) {
            return release_date;
        }
    }

    public String genresAsString() {
        String genres = "";
        for (Genre g : getGenres()) {
            if (genres.length() > 0) {
                genres = genres + " - " + g.getName();
            } else {
                genres = g.getName();
            }
        }
        return genres;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
