package com.example.maximeweekhout.bioscoopvandaag;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class Show implements Serializable {

    public int start, end;
    private int durationInMinutes;
    private String ticketUrl;

    public Show(JSONObject o) throws Exception {
        this.start = o.has("start_time") ? o.getInt("start_time") : 0;
        this.end = o.has("end_time") ? o.getInt("end_time") : 0;
        this.durationInMinutes = o.has("duration") ? o.getInt("duration") : 0;
        this.ticketUrl = o.has("ticket_url") ? o.getString("ticket_url") : "";
    }

    /**
     * Get duration in minutes
     * @return duration
     */
    public int getDuration() {
        return durationInMinutes;
    }

    /**
     * Get parsed start time
     * @return time
     */
    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
        Date netDate = new Date(this.start * 1000L);
        String date = sdf.format(netDate);

        if (date.equals(sdf.format(new Date(System.currentTimeMillis())))) {
            return "Vandaag";
        } else if (date.equals(sdf.format(new Date(System.currentTimeMillis()  - 24*3600*1000L)))){
            return "Gister";
        }

        return date;
    }

    /**
     * Get parsed start time
     * @return time
     */
    public String getStart() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date netDate = new Date(this.start * 1000L);
        return sdf.format(netDate);
    }

    /**
     * Get parsed end time
     * @return time
     */
    public String getEnd() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date netDate = new Date(this.end * 1000L);
        return sdf.format(netDate);
    }

    /**
     * Get ticketURL
     * @return url
     */
    public String getTicketUrl() {
        return ticketUrl;
    }
}
