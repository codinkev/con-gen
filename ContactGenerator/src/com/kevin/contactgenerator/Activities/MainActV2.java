package com.kevin.contactgenerator.Activities;
//STARTUP SCREEN

import java.sql.Date;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Application;
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


public class MainActV2 extends Activity {

	Intent i;
	DatabaseHelper sqldb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getActionBar().hide();
		
		//instantiating activity gets what we need from content providers
		ContactGenerator newGameData = ((ContactGenerator) getApplicationContext());
        //System.out.println(newGameData.myGlobalArray);
		
		//this generated everything so after it's done we move to next activity
		System.out.println("data generated");
		
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
		
		
        
        //here: remove the db definition stuff to add all noncons automatically
        //pull down text/calls to arraylists, add to SETS, upload set to the noncons
        
        i = new Intent(MainActV2.this, NonCons.class);   
        System.out.println("*******SWITCHING*******");
		//startActivity(i);
        
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
	
		
			
}
