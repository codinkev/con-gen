package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Models.Contact;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;

public class Cons extends Activity {
	
	ArrayList<Contact> contacts;
	DatabaseHelper sqldb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//db should already be populated from first activity
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.newcon, menu);
		return true;
	}

}
