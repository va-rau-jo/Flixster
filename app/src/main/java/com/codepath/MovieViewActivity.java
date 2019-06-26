package com.codepath;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

import cz.msebera.android.httpclient.Header;

public class MovieViewActivity extends AppCompatActivity {

    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // parameter name/key, the actual key is the value
    public final static String API_KEY_PARAM = "api_key";
    public final static String TAG = "MovieListActivity";

    // used to get data from the movies api
    private AsyncHttpClient client;

    private ArrayList<Movie> movies;
    MovieAdapter movieAdapter;
    RecyclerView recyclerView;
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new AsyncHttpClient();
        movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(movies);
        recyclerView = findViewById(R.id.rvMovies);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(movieAdapter);

        getConfiguration();
        Log.i(TAG, "EASFDADSF");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ((GridLayoutManager) (recyclerView.getLayoutManager())).setSpanCount(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1);
    }

    private void getNowPlaying() {
        String url = API_BASE_URL + "/movie/now_playing";
        RequestParams params = getBasicParams();

        // GET request to get the currently playing movies
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //load the results
                try {
                    JSONArray results = response.getJSONArray("results");
                    // create movie objects
                    for(int i = 0; i < results.length(); i++) {
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        movieAdapter.notifyItemInserted(i);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse the now playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now_playing endpoint", throwable, true);
            }
        });
    }

    /**
     * Gets configuration information such as where to find the image urls and stores
     * it in a
     */
    private void getConfiguration() {
        String url = API_BASE_URL + "/configuration";
        RequestParams params = getBasicParams();

        // GET request
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try{
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configuration with base url %s and poster size %s",
                            config.getImageBaseUrl(), config.getPosterSize()));
                    movieAdapter.setConfig(config);
                    getNowPlaying();
                }
                catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });
    }

    private RequestParams getBasicParams() {
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        return params;
    }

    private void logError(String message, Throwable error, boolean alertUser) {
        Log.e(TAG, message, error);

        if(alertUser)
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
