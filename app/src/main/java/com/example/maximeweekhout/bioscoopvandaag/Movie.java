package com.example.maximeweekhout.bioscoopvandaag;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxime Weekhout on 18-10-2016.
 */

public class Movie implements Serializable {
    private String title, theater;
    private List<Show> shows = new ArrayList<>();
    private String imdbId, posterUrl;

    // https://developer.android.com/reference/org/json/JSONObject.html
    // http://www.tutorialspoint.com/android/android_json_parser.htm
    Movie(JSONObject o) throws Exception {

        // Parse JsonObject in Movie Object
        this.title = o.has("title") ? o.getString("title") : "Unknown Title";
        this.theater = o.has("theaterName") ? o.getString("theaterName") : "";

        // Iterate over each time
        if (o.has("times")) {
            JSONArray times =  o.getJSONArray("times");
            for (int i = 0; i < times.length() ; i++) {
                shows.add(new Show(times.getJSONObject(i)));
            }
        }

        // Parse IMDB if there is an positive result
        if (o.has("possibleImdb")) {
            if (!o.getString("possibleImdb").equals("null")) {
                JSONObject imdb = o.getJSONObject("possibleImdb");
                this.imdbId = imdb.has("imdbID") ? imdb.getString("imdbID") : null;
                this.posterUrl = imdb.has("Poster") ? imdb.getString("Poster") : null;
            }
        }
    }

    /**
     * Get title of movie
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get all shows that are available
     * @return array
     */
    public List<Show> getShows() {
        return shows;
    }

    /**
     * Return ImdbID when available
     * @return id or empty string
     */
    public String getImdbId() {
        if (imdbId == null) {
            return "";
        }
        return imdbId;
    }

    /**
     * Return poster url when available
     * @return url or empty string
     */
    public String getPoster() {
        if (posterUrl == null) {
            return "";
        }
        return posterUrl;
    }

    /**
     * Return name of theater
     * @return theatername
     */
    public String getTheatre() {
        return theater;
    }
}
