package com.rcalencar.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rcalencar.popularmovies.repository.remote.MovieService;
import com.rcalencar.popularmovies.repository.remote.model.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<Movie> movies = new ArrayList<>();
    private OnItemSelectedListener mOnItemSelectedListener;

    public interface OnItemSelectedListener {
        void onItemSelected(Movie movie, View view, String path);
    }

    public MoviesAdapter(Context context, OnItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    public void clear() {
        movies.clear();
        notifyDataSetChanged();
    }

    void add(List<Movie> tasks) {
        this.movies.addAll(tasks);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Movie movie = movies.get(position);

        String path = MovieService.HTTP_IMAGE_TMDB_ORG_T_P_W185 + movie.getPoster_path();
        Picasso.with(context)
                .load(path)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        holder.movie_thumbnail.setImageBitmap(bitmap);
                        Palette.from(bitmap)
                                .generate(palette -> {
                                    Palette.Swatch swatch = palette.getDominantSwatch();
                                    if (swatch != null) {
                                        holder.movie_item_container.setBackgroundColor(swatch.getRgb());
                                        holder.movie_title.setTextColor(swatch.getTitleTextColor());
                                        holder.movie_release_year.setTextColor(swatch.getBodyTextColor());
                                        holder.movie_popularity.setTextColor(swatch.getBodyTextColor());
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

        holder.movie_title.setText(movie.getOriginal_title());
        holder.movie_release_year.setText(movie.getYear());
        holder.movie_popularity.setText(String.format(Locale.getDefault(), "%s %.2f", context.getString(R.string.popularity), movie.getPopularity()));
        holder.movie_item_container.setOnClickListener(v -> mOnItemSelectedListener.onItemSelected(movie, holder.movie_thumbnail, path));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View movie_item_container;
        View movie_description_container;
        ImageView movie_thumbnail;
        TextView movie_title;
        TextView movie_release_year;
        TextView movie_popularity;
        TextView movie_genres;

        ViewHolder(View v) {
            super(v);
            movie_item_container = v.findViewById(R.id.movie_item_container);
            movie_description_container = v.findViewById(R.id.movie_description_container);
            movie_thumbnail = (ImageView) v.findViewById(R.id.movie_thumbnail);
            movie_title = (TextView) v.findViewById(R.id.movie_title);
            movie_release_year = (TextView) v.findViewById(R.id.movie_release_year);
            movie_popularity = (TextView) v.findViewById(R.id.movie_popularity);
            movie_genres = (TextView) v.findViewById(R.id.movie_genres);
        }
    }
}