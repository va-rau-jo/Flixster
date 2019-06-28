package com.codepath;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.models.DetailedMovie;
import com.codepath.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class DetailActivity extends YouTubeBaseActivity {

    // Obtained from the parcel in the intent
    private DetailedMovie movie;

    private Tooltip genreTooltip;

    // Views that must be populated from the properties of the DetailedMovie
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvOverview) TextView tvOverview;
    @BindView(R.id.rbVoteAverage) RatingBar rbVoteAverage;
    @BindView(R.id.tvReleaseDate) TextView tvReleaseDate;
    @BindView(R.id.tvRunTime) TextView tvRunTime;
    @BindView(R.id.tvGenres) TextView tvGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Dark);
        super.onCreate(savedInstanceState);
        // Get the basic movie from the intent's parameters
        Movie basicMovie = Parcels.unwrap(getIntent()
                .getParcelableExtra(Movie.class.getSimpleName()));
        createDetailedMovie(basicMovie);
    }

    private void initView() {
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
    }

    /**
     * Given a basic movie, it fetches additional details to create a detailed movie with the
     * same movie ID
     * @param basicMovie The basic movie
     */
    private void createDetailedMovie(final Movie basicMovie) {
        String url = MovieViewActivity.API_BASE_URL + "/movie/" + basicMovie.id;
        RequestParams params = MovieViewActivity.getBasicParams(this);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    movie = new DetailedMovie(response);
                    getYoutubeId(basicMovie);
                    initView();
                    updateDetails();
                } catch (JSONException e) {
                    Log.e("detail activity", "Failed to get data from the detailed endpoint");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("detail activity", "Failed to get data from the detailed endpoint");
            }
        });
    }

    /**
     * Given a basic movie, it uses the id to find the youtube id (if it exists) of a trailer or
     * some other video.
     * @param basicMovie The basic movie
     */
    private void getYoutubeId(Movie basicMovie) {
        String url = MovieViewActivity.API_BASE_URL + "/movie/" + basicMovie.id + "/videos";
        RequestParams params = MovieViewActivity.getBasicParams(this);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    boolean trailerFound = false;
                    for(int i = 0; i < results.length(); i++) {
                        JSONObject video = results.getJSONObject(i);
                        // Display a trailer from YouTube if possible
                        if(video.getString("type").equals("Trailer") &&
                                video.getString("site").equals("YouTube")) {
                            trailerFound = true;
                            movie.setYoutubeId(video.getString("key"));
                            setUpYoutubePlayer();
                        }
                    }
                    // If you cant find a trailer, show any video
                    if(!trailerFound && results.length() > 0) {
                        JSONObject o = results.getJSONObject(0);
                        movie.setYoutubeId(o.getString("key"));
                        setUpYoutubePlayer();
                    }
                } catch (JSONException e) {
                    Log.e("detail activity", "Failed to get data from the detailed endpoint");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("detail activity", "Failed to get data from the detailed endpoint");
            }
        });
    }

    /**
     * Uses the detailed movie and loads the video with the given Youtube ID.
     */
    private void setUpYoutubePlayer() {
        YouTubePlayerView playerView = findViewById(R.id.player);

        playerView.initialize(getString(R.string.youtube_api_key),
                new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                assert movie.getYoutubeId() != null;
                youTubePlayer.cueVideo(movie.getYoutubeId());
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
            }
        });
    }

    /**
     * Method called after the asynchronous call to the MovieDB API, finish populating the
     * additional details in the layout.
     */
    private void updateDetails() {
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getSummary());
        tvReleaseDate.setText(formatDate(movie.getReleaseDate()));
        tvRunTime.setText(formatTime(movie.getRunTime()));

        String allGenres = "";
        for(String genre : movie.getGenres()) {
            allGenres += ", " + genre;
        }

        tvGenres.setText(allGenres.substring(2));

        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
    }

    @OnClick(R.id.tvGenres)
    public void showFullGenre(TextView textView) {
        if(genreTooltip != null && genreTooltip.isShowing()) {
            genreTooltip.dismiss();
        }
        else if(textView.getLayout().getEllipsisCount(0) > 0) {
            genreTooltip = new Tooltip.Builder(textView)
                .setText(textView.getText().toString())
                .setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                .show();
        }
    }

    /**
     * Formats the given time in minutes into HH:MM
     * @param runTime The run time in minutes
     * @return The formatted time in proper format Ex. 100 -> 1:40
     */
    private static String formatTime(Integer runTime) {
        int hours = runTime / 60;
        int minutes = runTime % 60;
        return hours + ":" + String.format(Locale.US, "%02d", minutes);
    }

    /**
     * Formats the date string given into a more user friendly version
     * @param dateString Date string in the format yyyy-MM-dd
     * @return The date in the format MMMM dd, yyyy
     */
    private static String formatDate(String dateString) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString);
            return new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(date);
        } catch (ParseException e) {
            Log.e("date stuff", e.getMessage());
            return "";
        }
    }
}
