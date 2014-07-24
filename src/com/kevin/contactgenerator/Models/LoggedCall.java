package com.kevin.contactgenerator.Models;

import java.sql.Date;

/**
 * Logged Call model
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class LoggedCall {

    int duration;
    String number;
    String date;
    String type;

    /**
     * 
     * @param number
     *            number call is from
     * @param type
     *            inbound or outbound call identifier
     * @param date
     *            date call made
     * @param duration
     *            length of the call
     */
    public LoggedCall(String number, String type, String date, int duration) {
        this.duration = duration;
        this.number = number;
        this.date = date;
        this.type = type;
    }

    /**
     * 
     * setters and getters below: attributes described above
     */

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return date/length/type of call concatenated for listview display
     */
    public String toString() {
        return this.date + " | " + this.duration + " | " + this.type;
    }

}
