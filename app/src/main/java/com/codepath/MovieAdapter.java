package com.codepath;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.models.Config;
import com.codepath.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * The controller of an MVC design. It contains a list of all the movies that can be displayed
 * within the app, and a config that stores segments of the poster url.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> movies;

    // Keeps track of the base poster url and poster size
    private Config config;

    // the parent context (the main view)
    private Context context;

    MovieAdapter(ArrayList<Movie> movies) {
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

        boolean inPortraitMode = context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT;

        String imageUrl = inPortraitMode ?
                config.getImageUrl(config.getPosterSize(), movie.getImagePath()) :
                config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());

        int placeHolderId = inPortraitMode ?
                R.drawable.flicks_movie_placeholder :
                R.drawable.flicks_backdrop_placeholder;

        ImageView imageView = inPortraitMode ? holder.poster : holder.backdrop;
        assert imageView != null;

        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 30, 0))
                .placeholder(placeHolderId)
                .into(imageView);
    }

    /**
     * @return The number of movies returned from the API
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    void setConfig(Config config) {
        this.config = config;
    }

    /**
     * Internal class that represents one row item containing an poster preview, a title, and a
     * small summary of the movie.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // objects in the view
        // ImageViews are nullable because they only exist in the landscape or portrait mode, not
        // both
        @Nullable @BindView(R.id.ivPosterImage) ImageView poster;
        @Nullable @BindView(R.id.ivBackdropImage) ImageView backdrop;
        @BindView(R.id.tvTitle) TextView title;
        @BindView(R.id.tvSummary) TextView summary;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * Gets adapter position from the view holder, and starts a detail intent with the movie
         * class as a parameter.
         * @param v The view item you just clicked
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Movie movie = movies.get(position);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                context.startActivity(intent);
            }
        }
    }
}
