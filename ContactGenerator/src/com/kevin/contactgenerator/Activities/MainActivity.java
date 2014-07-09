package com.kevin.contactgenerator.Activities;

//STARTUP SCREEN

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;
import com.kevin.contactgenerator.Utilities.RefreshTables;

/**
 * startup activity after welcome screen access to all toplevel activities and
 * ability to refresh the database provided via button
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class MainActivity extends Activity {

    Intent i;
    DatabaseHelper sqldb;
    Button see_cons;
    Button see_noncons;
    Button update_db;
    
    //used while refreshing database
    AlertDialog.Builder builder; 
    ProgressDialog warning;
    
    /**
     * onCreate - instantiate button to refresh database navigation provided by
     * actionbar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        // getActionBar().hide();

        update_db = (Button) findViewById(R.id.update_db);
        update_db.setBackgroundColor(Color.RED);
        update_db.setTextColor(Color.BLACK);
        update_db.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // NEED A PROGRESS BAR/ALERT DIALOG HERE TO TELL THEM IT'S
                // HAPPENING
                // instantiate to modify tables
                
                //commented for now until safe way to cancel mid-refresh
                //RefreshTables refresh = ((RefreshTables) getApplicationContext());
                //refresh.totallyRefresh();
                
                //alert dialog for informing us of update status
                builder = new AlertDialog.Builder(MainActivity.this); 
                builder.setTitle("Updating")
                 .setMessage("Press anywhere to dismiss and cancel the update.");
                 
                warning = ProgressDialog.show(MainActivity.this, "", 
                        "Updating. Please wait...", true);
                warning.setCancelable(true);
               
                        //builder.create();
                warning.setOnDismissListener(new DialogInterface.OnDismissListener(){
                 @Override
                 public void onDismiss(final DialogInterface dialog) {
                
                     new AlertDialog.Builder(MainActivity.this) 
                     .setTitle("Update dismissed")  
                     .setMessage("Original information preserved.")  
                     .setPositiveButton(android.R.string.ok, null)  
                 .setCancelable(false)  
                 .create()  
                 .show();
                     }
                 });
             warning.show();       
             System.out.println("Done!");
            }
        });
    }

    /**
     * navigation to toplevel activities
     * 
     * @param boolean tells us if successful creation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainnav, menu);
        return true;
    }

    /**
     * menu onclick options
     * 
     * @param boolean tells us if click response successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // action bar option to see cons
        case R.id.action_seecons:
            Toast.makeText(this, "Showing contacts", Toast.LENGTH_SHORT)
                    .show();
            //i = new Intent(MainActivity.this, Cons.class);
            //System.out.println("*******SWITCHING*******");
            //startActivity(i);
          
            break;
        // action bar option to see noncons
        case R.id.action_seenoncons:
            Toast.makeText(MainActivity.this, "Showing noncons...",
                    Toast.LENGTH_SHORT).show();
            
            i = new Intent(MainActivity.this, NonCons.class);
            System.out.println("*******SWITCHING*******");
            startActivity(i);

            break;
        default:
            break;
        }
        return true;
    }

}