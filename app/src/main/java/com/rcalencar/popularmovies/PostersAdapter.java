package com.rcalencar.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rcalencar.popularmovies.repository.remote.MovieService;
import com.rcalencar.popularmovies.repository.remote.model.Poster;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class PostersAdapter extends RecyclerView.Adapter<PostersAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<Poster> posters = new ArrayList<>();

    public PostersAdapter(Context context) {
        this.context = context;
    }

    public void clear() {
        posters.clear();
        notifyDataSetChanged();
    }

    void add(List<Poster> posters) {
        this.posters.addAll(posters);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Poster item = posters.get(position);

        String path = MovieService.HTTP_IMAGE_TMDB_ORG_T_P_W500 + item.getFile_path();
        Picasso.with(context).load(path).into(holder.movie_thumbnail);
    }

    @Override
    public int getItemCount() {
        return posters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView movie_thumbnail;

        ViewHolder(View v) {
            super(v);
            movie_thumbnail = (ImageView) v.findViewById(R.id.movie_poster_id);
        }
    }
}