package com.codepath;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.models.Config;
import com.codepath.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MovieViewActivity extends AppCompatActivity {

    // Base URL for the MovieDB API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // Tag for logging purposes
    public final static String TAG = "MovieListActivity";
    // used to get data from the movies api
    private AsyncHttpClient client;
    // List of movies, passed into the adapter
    private ArrayList<Movie> movies;
    // Controller for displaying the movies
    private MovieAdapter movieAdapter;
    // Config that stores the base image urls
    private Config config;

    @BindView(R.id.rvMovies) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        client = new AsyncHttpClient();
        movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(movies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(movieAdapter);

        getConfiguration();
    }

    /**
     * Gets the currently playing movies from the MoviesDB API, and adds it to the movies list
     * and movie adapter.
     */
    private void getNowPlaying() {
        String url = API_BASE_URL + "/movie/now_playing";
        RequestParams params = getBasicParams(this);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for(int i = 0; i < results.length(); i++) {
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        movieAdapter.notifyItemInserted(i);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse the now playing movies", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now_playing endpoint", throwable);
            }
        });
    }

    /**
     * Gets configuration information such as where to find the image urls and stores
     * it in a custom class, that is passed to the adapter.
     */
    private void getConfiguration() {
        String url = API_BASE_URL + "/configuration";
        RequestParams params = getBasicParams(this);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try{
                    config = new Config(response);
                    Log.i(TAG, String.format
                            ("Loaded configuration with base url %s and poster size %s",
                            config.getImageBaseUrl(), config.getPosterSize()));
                    movieAdapter.setConfig(config);
                    getNowPlaying();
                }
                catch (JSONException e) {
                    logError("Failed parsing configuration", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                logError("Failed getting configuration", throwable);
            }
        });
    }

    /**
     * Creates a request params object and adds the api key to it
     * @return A request params object with the api key added to it
     */
    public static RequestParams getBasicParams(Context context) {
        RequestParams params = new RequestParams();
        params.put("api_key", context.getString(R.string.api_key));
        return params;
    }

    public static void logError(String message, Throwable error) {
        Log.e(TAG, message, error);
    }
}
