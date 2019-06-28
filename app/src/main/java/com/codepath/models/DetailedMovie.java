package com.codepath.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 *  Represents a movie object that is received from the MoviesDB API
 */
@Parcel
public class DetailedMovie extends Movie {

    Integer runTime;
    String releaseDate;
    String[] genres;
    String youtubeId;

    DetailedMovie() {
        super();
    }

    /**
     * Creates the detailed movie object, does not set the youtube id though
     * @param jsonObject The json object retrieved from movies/{movieId}
     * @throws JSONException
     */
    public DetailedMovie(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        runTime = jsonObject.getInt("runtime");
        releaseDate = jsonObject.getString("release_date");

        JSONArray genreArray = jsonObject.getJSONArray("genres");
        genres = new String[genreArray.length()];
        for(int i = 0; i < genreArray.length(); i++) {
            JSONObject movie = genreArray.getJSONObject(i);
            genres[i] = movie.getString("name");
        }
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String[] getGenres() {
        return genres;
    }

    public String getYoutubeId() { return youtubeId; }
}
