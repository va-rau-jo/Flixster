package com.codepath.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Represents a movie object that is received from the MoviesDB API
 */
public class Movie {

    private String title;
    private String overview;
    private String posterPath;

    public Movie(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        posterPath = jsonObject.getString("poster_path");
    }
}
