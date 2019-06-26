package com.codepath.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Represents a movie object that is received from the MoviesDB API
 */
public class Movie {

    private String title;
    private String summary;
    private String imagePath;

    public Movie(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString("title");
        summary = jsonObject.getString("overview");
        imagePath = jsonObject.getString("poster_path");
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getImagePath() {
        return imagePath;
    }
}
