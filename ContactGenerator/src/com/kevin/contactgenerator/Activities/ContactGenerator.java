package com.kevin.contactgenerator.Activities;


import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kevin.contactgenerator.Helpers.DatabaseHelper;
import com.kevin.contactgenerator.Models.Contact;
import com.kevin.contactgenerator.Models.LoggedCall;
import com.kevin.contactgenerator.Models.TextMsg;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

//http://stackoverflow.com/questions/4521117/android-having-trouble-creating-a-subclass-of-application-to-share-data-with-mu
//http://stackoverflow.com/questions/4572338/extending-application-to-share-variables-globally

//if keeping this here should probably rename package to contexts rather than activities

/*
 * IN GENERAL NEED A BETTER WAY TO DO THIS
 * STICK WITH PUTTING RESULTS INTO SQLITE??? OR GO WITH THIS
 * IF GO WITH THIS THEN FOR THE EXPLORER VIEW WILL NEED TO QUERY ARRAYLIST RESULTS EACH TIME...
 * ALERNATIVELY, JUST QUERY CONTENT PROVIDER EACH TIME FOR THE EXPLORER VIEW?
 * BUT TO GET THE NUMBERS I HAVE TO GET ALL TEXTS/CALLS ANYWAY... SQLITE MAY BE BEST OPTION
 * so find way to make it faster/better threading ..... incremental updates etc...
 * 
 * if that ends up being decision should i still put content provider stuff here so its globally accessible???
 */
public class ContactGenerator extends Application {
	
	public ArrayList<TextMsg> myGlobalTexts = null;
	public ArrayList<LoggedCall> myGlobalCalls = null;
	public ArrayList<Contact> myGlobalCons = null;
	public ArrayList<String> myGlobalNons = null;
	DatabaseHelper sqldb;
	
	   public ContactGenerator() {
	      //myGlobalArray = new ArrayList();
	      //myGlobalArray.add("pooptest");
	      
	   }
	 
	   public void initialize() {
	   sqldb = new DatabaseHelper(getApplicationContext());
		//*****TEMP COMMENTED -- add this and the "get" methods to some kind of update method called via button on main screen
		sqldb.recreateTables();
		
		//IS THERE WEIRD THREADING HAPPENING HERE -- DOES EACH HAPPEN SEQUENTIALLY? RESEARCH ANDROID
		//FIND WAY TO MAKE ONLY RUN THESE TO ADD UPDATES (SO DOESNT TAKE FOREVER EACH TIME)
			//IE STORE LAST DATE OF UPDATE AND ONLY TAKE STUFF >= THAT DATE AGAIN...
		//have a fake progress bar here and textview saying something like "db populating"...
		
		//
		//we cant have contacts run asynchronously because noncons depends on it
		getContacts();
		
		//now that we have contacts, populate the unique list of numbers into a set
		for (int i=0; i<myGlobalCons.size(); i++) {
				
		}
		
		Runnable texts = new Runnable() {
			public void run() {
				getTextDetails();
			}
		};
       Runnable calls = new Runnable() {
			public void run() {
				getCallDetails();
			}
		};
		
       ExecutorService executor = Executors.newFixedThreadPool(2);
       executor.execute(texts);
       executor.execute(calls);
       
       //executor.shutdown();
       //while (!executor.isTerminated()){}
       System.out.println("finished populating database");
       
       sqldb.close();
	}
	   
	 //contact table refresh
	 		//one row for each number/name, even if a name corresponds to >1 number
	 		//(how else store in sqlite?)
	 		//in listview display, just get distinct names and group numbers based on name...
	 		private void getContacts() {
	 			
	 			//ArrayList<Contact> contacts = new ArrayList<Contact>();
	 			
	 			Cursor cursor =
	 			        getContentResolver().query(
	 			        		ContactsContract.Contacts.CONTENT_URI,
	 			                null ,
	 			                null,
	 			                null,
	 			                null);
	 			cursor.moveToFirst();
	 			while (cursor.moveToNext()) {
	 				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
	 		        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	 		        if (Integer.parseInt(cursor.getString(
	 		              cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
	 		        	//figure out how this actually works and what getContentResolver really is...
	 		           Cursor pCur = getContentResolver().query(
	 		                     ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	 		                     null,
	 		                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
	 		                     new String[]{id}, null);
	 		           while (pCur.moveToNext()) {
	 		               String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	 		               System.out.println("Name: " + name + ", Phone No: " + phoneNo);
	 		               //add to our return vector
	 		               
	 		               //contacts.add(new Contact(name, phoneNo));
	 		               myGlobalCons.add(new Contact(name, phoneNo));
	 		               
	 		           }
	 		           pCur.close();
	 		        }
	 			}
	 			cursor.close();
	 			//return contacts;
	 			
	 		}
	 		
	 		//text refresh
	 		private void getTextDetails() {
	 			//ArrayList<TextMsg> texts = new ArrayList<TextMsg>();
	 			
	 			Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
	 			cursor.moveToFirst();
	 			
	 			/*
	 			0: _id
	 			1: thread_id
	 			2: address
	 			3: person
	 			4: date
	 			5: protocol
	 			6: read   
	 			7: status
	 			8: type
	 			9: reply_path_present
	 			10: subject
	 			11: body -- APPARENTLY 14
	 			12: service_center
	 			13: locked
	 			 */
	 			do{
	 				   String number = cursor.getString(2);
	 				   String contact = cursor.getString(3);
	 				   String datehold = cursor.getString(4);
	 				   String date = (new Date(Long.valueOf(datehold))).toString();
	 				   String body = cursor.getString(14);
	 				   System.out.println("TESTING");
	 			       System.out.println("number: "+number+" contact: "+contact+" date: "+date+" body: "+body);
	 			      // System.out.println(cursor.getString(0)+"\n"+cursor.getString(1)+"\n"+cursor.getString(6)
	 			    	//	   +"\n"+cursor.getString(7)+"\n"+cursor.getString(8)+"\n"+cursor.getString(9)+"\n"+cursor.getString(10)
	 			    	//	   +"\n"+cursor.getString(11)+"\n"+cursor.getString(12)+"\n"+cursor.getString(13)
	 			    	//	   +"\n"+cursor.getString(14)+"\n"+cursor.getString(15)
	 			    	//	   );
	 			       //texts.add(new TextMsg(number, contact, date, body));
	 			      myGlobalTexts.add(new TextMsg(number, contact, date, body));
	 			      //insertNons(number);
	 			      
	 			}while(cursor.moveToNext());
	 			
	 			cursor.close();
	 			//return texts;
	 		}
	 		
	 		//call log refresh
	 		private void getCallDetails() {
	 			
	 			//ArrayList<LoggedCall> calls = new ArrayList<LoggedCall>();
	 						
	 			StringBuffer sb = new StringBuffer();
	 			//Cursor managedCursor = managedQuery( CallLog.Calls.CONTENT_URI,null, null,null, null);
	 			Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,null, null,null, null);
	 			int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
	 			int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
	 			int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
	 			int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
	 			sb.append( "Call Details :");
	 			while ( managedCursor.moveToNext() ) {
	 			String phNumber = managedCursor.getString( number );
	 			String callType = managedCursor.getString( type );
	 			String callDate = managedCursor.getString( date );
	 			String callDayTime = (new Date(Long.valueOf(callDate))).toString();
	 			Integer callDuration = Integer.valueOf(managedCursor.getString( duration ));
	 			String dir = null;
	 			int dircode = Integer.parseInt( callType );
	 			switch( dircode ) {
	 			case CallLog.Calls.OUTGOING_TYPE:
	 			dir = "OUTGOING";
	 			break;

	 			case CallLog.Calls.INCOMING_TYPE:
	 			dir = "INCOMING";
	 			break;

	 			case CallLog.Calls.MISSED_TYPE:
	 			dir = "MISSED";
	 			break;
	 			}
	 			sb.append( "\nPhone Number:--- "+phNumber +" \nCall Type:--- "+dir+" \nCall Date:--- "+callDayTime+" \nCall duration in sec :--- "+callDuration );
	 			sb.append("\n----------------------------------");
	 			
	 			//calls.add(new LoggedCall(phNumber, dir, callDayTime, callDuration));
	 			myGlobalCalls.add(new LoggedCall(phNumber, dir, callDayTime, callDuration));
	 			//insertNons(phNumber);
	 			
	 			}
	 			managedCursor.close();
	 			//this was a textview display
	 			//call.setText(sb);
	 			System.out.println(sb);
	 			
	 			//return calls;
	 		}
	 		
	 		public void insertNons() {
	 			
	 			
	 			
	 			
	 		}
}
