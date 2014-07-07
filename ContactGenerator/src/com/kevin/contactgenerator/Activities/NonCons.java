package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Helpers.DatabaseHelper;
import com.kevin.contactgenerator.Models.LoggedCall;
import com.kevin.contactgenerator.Models.TextMsg;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class NonCons extends Activity {
	
	ArrayList<String> noncons;
	DatabaseHelper sqldb;
	ListView nonContactList;
	ArrayAdapter<String> nonContactAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noncons);
		
		getActionBar().hide();
		
		sqldb = new DatabaseHelper(getApplicationContext());
		
		//helper method below
		//populateNonCons();
		
		noncons = sqldb.fetchAllNonContacts();
		System.out.println("noncons are: "+noncons);
		
  		nonContactList = (ListView)findViewById(R.id.nonContactList);
  		nonContactList.setVerticalScrollBarEnabled(true);
    
        nonContactAdapter = new ArrayAdapter<String>(NonCons.this,
        		android.R.layout.simple_list_item_1, noncons);

        nonContactList.setAdapter(nonContactAdapter);
        nonContactList.setClickable(true);
        nonContactList.setItemsCanFocus(false);
        nonContactList.setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView a, View v, int position, long id) {
            		System.out.println("an item has been clicked");
            		//position variable is where in the arraylist was clicked
            		
            		//call this method to deploy the spinner for selection of seeing text/call
            		//ultimately have ability to see 3029433220 and 9433220 in same view
            		//ie something like as long as n digits match give user the offer to merge results
            		
            		//STEP INTO COMPLETE NEW ACTIVITY, PASS NUMBER IN BUNDLE TO NEW ACTIVITY
            		//LHS IS FOUR BUTTONS: ADD AS CONTACT (brings up form etc)
            		//or see texts calls etc...
            		//if text/call, populate RHS listview with that info and tell them what they're seeing
            		String number = noncons.get(position);
            		Intent i = new Intent(NonCons.this, NonConExplorer.class);
            		
            		i.putExtra("number", number);
	           		startActivity(i); 
	           		           		
            }
        });
		
	}
	
	 /* 
	 * helper method for deduping texts/calls ahead of time and getting only numbers
     * to cut down processing time.  consider relocating this method?
     * 
     * NO LONGER NEEDED FOR NOW -- IMPLEMENTED BETTER EXCEPTION HANDLING
     * still learn about stuff below
     */
    public void populateNonCons() {
    	ArrayList<TextMsg> texts = sqldb.fetchAllTexts();
    	ArrayList<LoggedCall> calls = sqldb.fetchAllCalls();
    	//figure out what a hashset actually is -- does doing addAll auto dedupe???
    	//iterating over; WHAT IS AN ITERATOR; etc... what is a reducer, etc...
    	//what is reflection???
    	Set<String> noncons = new HashSet<String>();
    	String number=null;
    	for (int i=0; i< texts.size(); i++) {
    		number = texts.get(i).getNumber();
    		noncons.add(number);
    	}
    	for (int j=0; j< calls.size(); j++) {
    		number = calls.get(j).getNumber();
    		noncons.add(number);
    	}
    	
    	//add to noncon db
    	for (String s : noncons) {
    		sqldb.insertNonContact(s);
    	}
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.newcon, menu);
		return true;
	}
	

		
		//APPARENTLY ONRESUME IS CALLED EACH TIME THAT ONCREATE IS CALLED! DONT WANT THIS.
		//FUNCTIONALITY I WANTED IS TO BE CALLED AFTER THE FIRST TIME SINCE THATS WHEN ONCREATE ISNT CALLED
		//AND I WANT TO USE THIS TO RE-OPEN DB ETC...
		//RESEARCH REPLACEMENTS LIKE ONRESTART() OR FIND A WORKAROUND TO ACHIEVE THIS FUNCTIONALITY
		//TODO -- implement these to open and close db respectively
		
		public void onResume() {
			super.onResume();
			System.out.println("Resume is called, at least.");
		}
		
		public void onPause() {
			super.onPause();
			sqldb.close();
			
			//temp -- re-creates each time visiting. don't want permanently 
			//(? -- maybe do. evaluate performance)
			//comment for now -- if finish, when click back exits whole app
			//NonCons.this.finish();
		}
	
}
