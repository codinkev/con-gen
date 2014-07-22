package com.kevin.contactgenerator.Models;

import java.sql.Date;

/**
 * Text message model
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class TextMsg {

    String number;
    String contact;
    String date;
    String body;

    /**
     * 
     * @param number
     *            number text is from
     * @param contact
     *            name of the person sending text (if we have it)
     * @param date
     *            date text sent
     * @param body
     *            content of the text message
     */
    public TextMsg(String number, String contact, String date, String body) {
        this.number = number;
        this.contact = contact;
        this.date = date;
        this.body = body;
    }

    /**
     * 
     * setters and getters below: attributes described above
     */

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 
     * @return content of the text message for listview display
     */
    public String toString() {
        return this.body;// .substring(1,Math.min(this.body.length(), 20));
    }

}
