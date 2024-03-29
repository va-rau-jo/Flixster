package com.codepath.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {

    private String imageBaseUrl;
    private String posterSize;
    private String backdropSize;

    /**
     * Using object it gets the "secure_base_url" path and selects w342 for the poster size
     * @param object the json object obtain from
     * @throws JSONException
     */
    public Config(JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");
        imageBaseUrl = images.getString("secure_base_url");
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        posterSize = posterSizeOptions.optString(3, "w342");
        backdropSize = backdropSizeOptions.optString(1, "w780");
    }

    public String getImageUrl(String size, String path) {
        return imageBaseUrl + size + path;
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
