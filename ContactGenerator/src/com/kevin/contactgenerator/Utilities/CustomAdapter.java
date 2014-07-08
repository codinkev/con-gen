package com.kevin.contactgenerator.Utilities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

/*
 * provides adapters with a name attribute if we need to identify which is in use on LV click
 */
public class CustomAdapter<T> extends ArrayAdapter<T> {
	
	String name;
	
	public CustomAdapter(Context context, int textViewResourceId, List<T> objects, String name) {
		super(context, textViewResourceId, objects);
		this.name = name;
		// TODO Auto-generated constructor stub
	}
	
	//probably dont need this one anymore
	public CustomAdapter(Context context, int textViewResourceId, T[] objects, String name) {
		super(context, textViewResourceId, objects);
		this.name = name;
		// TODO Auto-generated constructor stub
	}
	public CustomAdapter(Context context, int textViewResourceId, String name) {
		super(context, textViewResourceId);
		this.name = name;
		// TODO Auto-generated constructor stub
	}
	
	public String toString() {
		return this.name;
	}

	
}
