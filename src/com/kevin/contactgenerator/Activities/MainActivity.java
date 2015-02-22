package com.kevin.contactgenerator.Activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;
import com.kevin.contactgenerator.Utilities.UpdateService;
import com.kevin.contactgenerator.Utilities.UpdateStatusBroadcast;

/**
 * startup activity after welcome screen access to all top-level activities and
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

    // used in this attempt to use an action indicator under action bar as a
    // persistent progress bar
    private MenuItem refresh;

    // communication for update service to main thread
    private UpdateStatusBroadcast receiver = new UpdateStatusBroadcast();

    // added for navdrawer
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    /**
     * switch, global to this activity, to tell the async task the service is
     * done want a way to do this for all activities more generally...
     */
    static volatile boolean completed;

    public static void setComplete(boolean boo) {
        completed = boo;
    }

    /**
     * onCreate - instantiate button to refresh database navigation provided by
     * actionbar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        getActionBar().setDisplayShowTitleEnabled(false);

        mTitle = mDrawerTitle = getTitle();
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        String[] actions = getResources().getStringArray(R.array.actions);
        
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, actions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
        mDrawerLayout, /* DrawerLayout object */
        R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
        R.string.drawer_open, /* "open drawer" description for accessibility */
        R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //if (savedInstanceState == null) {
        //    selectItem(0);
        //}

    }
    
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
        public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_updatedb).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    
    /**
     * navigation to toplevel activities
     * http://stackoverflow.com/questions/10676517
     * /actionbar-progress-indicator-and-refresh-button
     * 
     * @param boolean tells us if successful creation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainnav, menu);

        refresh = menu.findItem(R.id.action_updatedb);

        // refresh.setActionView(R.layout.progbar);

        // refresh.collapseActionView();
        // now, cause progressbar to show/disappear depending on user action
        // using the expand/collapseAV

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * menu onclick options
     * 
     * @param boolean tells us if click response successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_updatedb:
            new AlertDialog.Builder(this)
                    //.setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Refresh Database?")
                    .setMessage("Updates database to reflect current data on phone")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.setComplete(false);
                                    refresh.setActionView(R.layout.progbar);
                                    new CompleteService().execute();
                                }
                            }).setNegativeButton("No", null).show();
            break;

        default:
            break;
        }
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver.setMainActivityHandler(new MainActivity());
        registerReceiver(receiver, new IntentFilter(
                "com.kevin.contactgenerator.Utilities"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * this is called when the application is exited; close the database here
     * and here only to make concurrent access easier
     * 
     * (verify in lifecycle that onpause is called anyway before this, or
     * unregister receiver too)
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHelper sqldb = DatabaseHelper
                .getInstance(getApplicationContext());
        sqldb.close();
    }

    /* experimental below; adding async task for starting service */
    // http://developer.android.com/reference/android/os/AsyncTask.html
    // http://stackoverflow.com/questions/6053602/what-arguments-are-passed-into-asynctaskarg1-arg2-arg3
    private class CompleteService extends AsyncTask<Void, Void, Void> {
        // via link above, use on pre-execute to launch the progress bar
        // want progress bar to have a button for cancelling as well
        // these should be visible regardless of activity switching until the
        // service is dead
        ProgressDialog pdLoading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // remove prog dialog in here totally once have action bar ready;
            // for now just don't show it
            // also ultimately include functionality here into the action bar
            // click itself
            pdLoading = new ProgressDialog(MainActivity.this);
            // this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // cancel service, get rid of dialog, tell user
                            // update cancelled (db preserved)
                            // also need to stop the async task...
                            dialog.dismiss();
                        }
                    });
            // pdLoading.show();

            // Toast.makeText(MainActivity.this, "Updating info...",
            // Toast.LENGTH_SHORT).show();
        }

        // invoke the service here
        // look into using onprogressupdate somehow
        @Override
        protected Void doInBackground(Void... voids) {
            Intent intent = new Intent(MainActivity.this, UpdateService.class);
            // need below link to ensure service completes before this method...
            // http://stackoverflow.com/questions/16934425/call-an-activity-method-from-a-broadcastreceiver-class
            startService(intent);
            while (!completed) {
            }
            return null;
        }

        // kill the progress bar
        // need better logging: debug vs error? read below
        // http://stackoverflow.com/questions/18393888/why-shouldnt-i-use-system-out-println-in-android
        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            // Toast.makeText(MainActivity.this, "SERVICE COMPLETE",
            // Toast.LENGTH_SHORT).show();
            System.out.println("Service complete...");
            // pdLoading.dismiss();
            refresh.setActionView(null);
            Toast.makeText(MainActivity.this, "Update complete.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        if (position == 0) {
            Toast.makeText(MainActivity.this, "Showing noncons...",
                    Toast.LENGTH_SHORT).show();
            i = new Intent(MainActivity.this, NonContactList.class);
            System.out.println("*******SWITCHING*******");
            startActivity(i);
        } else {
            Toast.makeText(MainActivity.this, "Showing contacts",
                    Toast.LENGTH_SHORT).show();
            i = new Intent(MainActivity.this, ContactList.class);
            System.out.println("*******SWITCHING*******");
            startActivity(i);
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}