package com.kevin.contactgenerator.Utilities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * provides adapters with a name attribute if we need to identify which is in
 * use on LV click
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 * @param <T>
 *            This describes my type parameter
 */
public class CustomAdapter<T> extends ArrayAdapter<T> {

    String name;

    /**
     * first constructor version
     * 
     * @param context
     *            Current state of the application
     * @param textViewResourceId
     *            ID of the textview clicked in the adapter
     * @param objects
     *            the list used as data in the adapter
     * @param name
     *            provided name of the adapter
     */
    public CustomAdapter(Context context, int textViewResourceId,
            List<T> objects, String name) {
        super(context, textViewResourceId, objects);
        this.name = name;

    }

    /**
     * second constructor version has array rather than arraylist of data
     * backing it
     * 
     * @param context
     *            Current state of the application
     * @param textViewResourceId
     *            ID of the textview clicked in the adapter
     * @param objects
     *            the array used as data in the adapter
     * @param name
     *            provided name of the adapter
     */
    public CustomAdapter(Context context, int textViewResourceId, T[] objects,
            String name) {
        super(context, textViewResourceId, objects);
        this.name = name;

    }

    /**
     * third constructor version no backing data/array provided
     * 
     * @param context
     *            Current state of the application
     * @param textViewResourceId
     *            ID of the textview clicked in the adapter
     * @param name
     *            provided name of the adapter
     */
    public CustomAdapter(Context context, int textViewResourceId, String name) {
        super(context, textViewResourceId);
        this.name = name;

    }

    /**
     * 
     * @return String the name of the adapter
     */
    public String toString() {
        return this.name;
    }

}
