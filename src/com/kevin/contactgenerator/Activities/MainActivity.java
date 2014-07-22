package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
    // https://developer.android.com/design/patterns/app-structure.html
    // http://androidexample.com/Incomming_SMS_Broadcast_Receiver_-_Android_Example/index.php?view=article_discription&aid=62&aaid=87
    // sms broadcast receiver info^
    // http://stackoverflow.com/questions/12128331/how-to-change-fontfamily-of-textview-in-android
    // change textview font styles... look at other ways too^

    Intent i;
    Button update_db;

    // thread we call the data update inside of
    Thread dataUpdate;

   

    /**
     * onCreate - instantiate button to refresh database navigation provided by
     * actionbar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        update_db = (Button) findViewById(R.id.update_db);
        update_db.setBackgroundColor(Color.RED);
        update_db.setTextColor(Color.BLACK);
        update_db.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // instantiate now and start after creating ondismiss action
                // would having handler here (regarding all the alertdialog
                // stuff around here) be better???
                // http://developer.android.com/guide/faq/commontasks.html
                // ^handler for alertdialog^ (???)
                RefreshTables refresh = new RefreshTables(
                        MainActivity.this);
                
                MainActivity.this.runOnUiThread(refresh);
                
                dataUpdate = new Thread(refresh);

                // start the update
                dataUpdate.start();
                
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
            Toast.makeText(this, "Showing contacts", Toast.LENGTH_SHORT).show();
            i = new Intent(MainActivity.this, Cons.class);
            System.out.println("*******SWITCHING*******");
            startActivity(i);

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