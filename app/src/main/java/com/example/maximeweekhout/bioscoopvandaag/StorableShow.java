package com.example.maximeweekhout.bioscoopvandaag;

import org.json.JSONObject;

import java.io.Serializable;

public class StorableShow implements Serializable {

    private String title;
    private String theater;
    private Show show;
    private JSONObject imdb = new JSONObject();

    StorableShow(String title, String theater, Show show, JSONObject imdb) {
        this.title = title;
        this.theater = theater;
        this.show = show;

        if (imdb != null) {
            this.imdb = imdb;
        }
    }

    StorableShow(String json) {
        try {
            JSONObject o = new JSONObject(json);
            title = o.getString("title");
            theater = o.getString("theater");
            show = new Show(o.getJSONObject("show"));
            imdb = o.getJSONObject("imdb");
        } catch (Exception e) {
            title = "Unknown";
        }
    }

    /**
     * Get movie title that is stored
     * @return String title of movie
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get theater
     * @return String title of theater
     */
    public String getTheater() {
        return theater;
    }

    /**
     * Get movie
     * @return Show stored show
     */
    public Show getShow() {
        return show;
    }

    /**
     * Get jsonobject of imdb
     * @return JSONObject imdb resuls if available
     */
    public JSONObject getImdb() {
        return imdb;
    }

    /**
     * Convert object to Json for storage usage
     * @return String parced json
     */
    public String getJson() {
        JSONObject jReturn = new JSONObject();

        try {
            JSONObject jObjectData = new JSONObject();

            jObjectData.put("start_time", show.start);
            jObjectData.put("end_time", show.end);
            jObjectData.put("duration", show.getDuration());
            jObjectData.put("ticket_url", show.getTicketUrl());

            jReturn.put("title", title);
            jReturn.put("theater", theater);
            jReturn.put("show", jObjectData);
            jReturn.put("imdb", imdb);

            System.out.println(jReturn.toString());
            return jReturn.toString();
        } catch (Exception e){

        }
        return "";
    }
}
