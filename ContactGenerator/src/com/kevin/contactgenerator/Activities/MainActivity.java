package com.kevin.contactgenerator.Activities;
//STARTUP SCREEN

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;
import com.kevin.contactgenerator.Utilities.RefreshTables;


/*
 * startup activity after welcome screen
 * if no databases exist, do the refresh automatically (otherwise leave to their discretion)
 * offer navigation to noncons, cons, and a refresh button
 */
public class MainActivity extends Activity {

	Intent i;
	DatabaseHelper sqldb;
	Button see_cons;
	Button see_noncons;
	Button update_db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getActionBar().hide();
		
		//rather use nav drawer than buttons?
		
		see_cons = (Button) findViewById(R.id.see_cons);
		see_cons.setBackgroundColor(Color.RED);
		see_cons.setTextColor(Color.BLACK);
		see_cons.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				i = new Intent(MainActivity.this, Cons.class);   
		        System.out.println("*******SWITCHING*******");
				startActivity(i);
				
			}
       });
		see_noncons = (Button) findViewById(R.id.see_noncons);
		see_noncons.setBackgroundColor(Color.RED);
		see_noncons.setTextColor(Color.BLACK);
		see_noncons.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				i = new Intent(MainActivity.this, NonCons.class);   
		        System.out.println("*******SWITCHING*******");
				startActivity(i);				
			}
       });
		update_db = (Button) findViewById(R.id.update_db);
		update_db.setBackgroundColor(Color.RED);
		update_db.setTextColor(Color.BLACK);
		update_db.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//NEED A PROGRESS BAR/ALERT DIALOG HERE TO TELL THEM IT'S HAPPENING
				//instantiate to modify tables
				RefreshTables refresh = ((RefreshTables) getApplicationContext());
				refresh.totallyRefresh();
				System.out.println("Done!");
				}
       });
				
		//USE THIS CHUNK IN THE WELCOME SCREEN (5 SEC LONG WITH A THEME IMAGE)
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