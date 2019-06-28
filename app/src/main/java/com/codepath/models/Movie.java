package com.codepath.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 *  Represents a movie object that is received from the MoviesDB API
 */
@Parcel
public class Movie {

    public Integer id;
    public String title;
    public String summary;
    public String imagePath;
    public String backdropPath;
    public Double voteAverage;

    public Movie() {}

    public Movie(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        title = jsonObject.getString("title");
        summary = jsonObject.getString("overview");
        imagePath = jsonObject.getString("poster_path");
        backdropPath = jsonObject.getString("backdrop_path");
        voteAverage = jsonObject.getDouble("vote_average");
    }

    public Integer getId() {
        return id;
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

    public String getBackdropPath() {
        return backdropPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

}
