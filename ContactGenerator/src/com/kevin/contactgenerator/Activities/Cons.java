package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Models.Contact;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;

/**
 * listview containing all contacts that exist on phone
 * (shows name and number currently)
 * 
 * TO BE COMPLETED
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class Cons extends Activity {
	
	ArrayList<Contact> contacts;
	DatabaseHelper sqldb;
	
	/**
     * onCreate - instantiate and display the listview
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//db should already be populated from first activity
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.newcon, menu);
		return true;
	}
	*/

}
