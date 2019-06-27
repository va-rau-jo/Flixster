package com.codepath.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 *  Represents a movie object that is received from the MoviesDB API
 */
@Parcel
public class Movie {

    public String title;
    public String summary;
    public String imagePath;

    public Movie() {}

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
