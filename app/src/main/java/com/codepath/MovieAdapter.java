package com.codepath;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.models.Config;
import com.codepath.models.Movie;

import java.util.ArrayList;

/**
 * The controller of an MVC design. It contains a list of all the movies that can be displayed
 * within the app, and a config that stores segments of the image url.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> movies;

    // Keeps track of the base image url and poster size
    private Config config;

    // the parent context (the main view)
    private Context context;

    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    /**
     * Creates a new ViewHolder inflated from the layout item_movie file
     * @param parent the parent "container"
     * @return returns a new ViewHolder within the parent that contains an inflated item_movie
     * layout object (no actual values in the objects)
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    /**
     * Binds the data from movies[position] to the given ViewHolder
     * @param holder the parent "container"
     * @param position the index of the
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.title.setText(movie.getTitle());
        holder.summary.setText(movie.getSummary());

        String imageUrl = config.getImageUrl(config.getPosterSize(), movie.getImagePath());

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.flicks_movie_placeholder)
                .into(holder.image);
    }

    /**
     * @return The number of movies returned from the API
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * Internal class that represents one row item containing an image preview, a title, and a
     * small summary of the movie.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // objects in the view
        private ImageView image;
        private TextView title;
        private TextView summary;
        private ScrollView summaryScroller;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivMovieImage);
            title = itemView.findViewById(R.id.tvTitle);
            summary = itemView.findViewById(R.id.tvSummary);
            summaryScroller = itemView.findViewById(R.id.scroller);
        }
    }
}
