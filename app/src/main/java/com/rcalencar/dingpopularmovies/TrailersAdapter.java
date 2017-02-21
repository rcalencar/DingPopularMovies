package com.rcalencar.dingpopularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rcalencar.dingpopularmovies.repository.remote.model.Trailer;

import java.util.ArrayList;
import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private static final String YOUTUBE = "YouTube";

    private Context context;
    private ArrayList<Trailer> trailers = new ArrayList<>();

    public TrailersAdapter(Context c) {
        context = c;
    }

    public void clear() {
        trailers.clear();
        notifyDataSetChanged();
    }

    public void add(List<Trailer> movies) {
        this.trailers.addAll(movies);
        notifyDataSetChanged();
    }

    public Trailer get(int i) {
        return trailers.get(i);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailer, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Trailer trailer = trailers.get(position);
        holder.title.setText(trailer.getName());
        holder.itemView.setOnClickListener(v -> playItem(trailer));

    }

    private void playItem(Trailer movie) {
        if (!YOUTUBE.equals(movie.getSite())) {
            Toast.makeText(context, context.getString(R.string.video_not_supported), Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + movie.getKey()));
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl(movie)));
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.movie_trailer_title);
        }
    }

    public static String youtubeUrl(Trailer movie) {
        return "http://www.youtube.com/watch?v=" + movie.getKey();
    }
}