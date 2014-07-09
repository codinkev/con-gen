package com.kevin.contactgenerator.Models;

/**
 * Contact model
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class Contact {

    String name;
    String number;

    /**
     * 
     * @param name
     *            name of contact
     * @param number
     *            number of contact
     */
    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    /**
     * 
     * setters and getters below: attributes described above
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * 
     * @return name of contact for listview display
     */
    public String toString() {
        return this.name;
    }

}
