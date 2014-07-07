package com.kevin.contactgenerator.Activities;
//STARTUP SCREEN

import java.sql.Date;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.contactgenerator.R;

import com.kevin.contactgenerator.Helpers.DatabaseHelper;
import com.kevin.contactgenerator.Models.Contact;
import com.kevin.contactgenerator.Models.LoggedCall;
import com.kevin.contactgenerator.Models.TextMsg;

public class MainActivity extends Activity {

	Intent i;
	DatabaseHelper sqldb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getActionBar().hide();
		
		//http://www.vogella.com/tutorials/AndroidSQLite/article.html
		//READ ^ FOR INFO ON ANDROID CONTACTS AND CONTENT PROVIDERS!!! (used below... 
		//possible way to move them to other class etc after better understanding?)
		
		//have a lead-in screen (thread) and then buttons to navigate to cons/noncons
		//initialize databases in some kind of service/bg thread??? ensure ready for user
		//HAVE A MENU RATHER THAN BUTTONS? LEARN HOW TO USE FOR NAVIGATION?
			//read for using menu
			//http://kenneththorman.blogspot.com/2010/10/adding-menu-to-your-android-application.html
		//WHAT WAS THE OTHER NAVIGATION THING THAT SIDE SWIPE WAS THREATENING -- RESEARCH.
		//MENU NOT FOR NAVIGATION??? WHAT IS PURPOSE.  HAVE IT ON NONCONS FOR DELETE/SEARCH ETC??? RESEARCH
		
		//determine: is creation of this at startup resetting tables?
		//add logic "create if not exists" or ENSURE they're being dropped? decide.
		
		//IT LOOKED LIKE IT WORKED FOR NONCONS
		//NEXT STEPS = BREAK INTO OWN ACTIVITIES; ADD LOGIC FOR VIEWING TEXTS/DELETING ALL THATT
		//NUMBERS INFO OFF PHONE/ACTUALLY ADDING NEW CONTACT ETC
		
		//ENSURE ITS WORKING AND STOP THROWING THE CONSTRAINT EXCEPTION FOR UNIQUE NUMBER
		//(FIND WAY TO PREVENT IF FROM GOING TO LOGCAT)
			//--> LEARN BETTER EXCEPTION HANDLING HERE! IE CATCH THE EXCEPTION ETC. LEARN BEST PRACTICES
			//checked vs unchecked; throws vs try/catch; when it terminates prog and not etc.
		//ENSURE NO EXISTING CONTACTS IN THERE AS WELL
		//ALSO STILL NEED TO ENSURE NUMBERS LIKE 3029433220 AND 9433220 ARE COUNTED AS SAME THING... DUPES
		
		//KEEP EXISTING CODE SAME BUT HAVE THE STUFF ALL DEDUPE BEFORE INSERTING TO NONCONS
		//AS IS NOW, MAKING A LOT OF SYSTEM STRESS... USE SET TO DEDUPE THE ARRAYLISTS OR SOMETHING?
		
		//OTHER NOTES: WRITTEN ON SANJAY HOBBY PACKET THING AND README.TXT THING SEE FOR GUIDANCE
		
		//have logic for ending db connection in onresume/onpause etc where necessary
		//disadvantages of single static db instance vs this? advantages?  is 
		
		//would it make sense to have these calls for populating databases elsewhere?
		//at least in another class??? (but need a context...)
        //ENSURE ONE COMPLETES BEFORE OTHER STARTS - NO FREAKY THREAD STUFFS
		
		sqldb = new DatabaseHelper(getApplicationContext());
		//*****TEMP COMMENTED -- add this and the "get" methods to some kind of update method called via button on main screen
		sqldb.recreateTables();
		
		//IS THERE WEIRD THREADING HAPPENING HERE -- DOES EACH HAPPEN SEQUENTIALLY? RESEARCH ANDROID
		//FIND WAY TO MAKE ONLY RUN THESE TO ADD UPDATES (SO DOESNT TAKE FOREVER EACH TIME)
			//IE STORE LAST DATE OF UPDATE AND ONLY TAKE STUFF >= THAT DATE AGAIN...
		//have a fake progress bar here and textview saying something like "db populating"...
		
		//*****TEMP COMMENTED
		//we cant have contacts run asynchronously because noncons depends on it
		getContacts();
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
		getTextDetails();
		getCallDetails();
        //ExecutorService executor = Executors.newFixedThreadPool(2);
        //executor.execute(texts);
        //executor.execute(calls);
        
        //executor.shutdown();
        //while (!executor.isTerminated()){}
        System.out.println("finished populating database");
        
        sqldb.close();
        
        //here: remove the db definition stuff to add all noncons automatically
        //pull down text/calls to arraylists, add to SETS, upload set to the noncons
        
        i = new Intent(MainActivity.this, NonCons.class);   
        System.out.println("*******SWITCHING*******");
		startActivity(i);
		//long time = System.currentTimeMillis();
		
		//change activities after a pause
		/*
		Runnable timer = new Runnable() {
				public void run() {
					try {
						//sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						
						finish();
					}
			}
		};
		new Thread(timer).start();
		*/
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.newcon, menu);
		return true;
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
		               sqldb.insertContacts(new Contact(name, phoneNo));
		           
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
			       sqldb.insertTexts(new TextMsg(number, contact, date, body));
			       
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
			sqldb.insertCalls(new LoggedCall(phNumber, dir, callDayTime, callDuration));
			
			}
			managedCursor.close();
			//this was a textview display
			//call.setText(sb);
			System.out.println(sb);
			
			//return calls;
		}
			
}
