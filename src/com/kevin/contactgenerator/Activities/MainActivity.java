package com.kevin.contactgenerator.Activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
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
import android.support.v4.view.ActionProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;
import com.kevin.contactgenerator.Utilities.UpdateService;

/**
 * startup activity after welcome screen access to all top-level activities and
 * ability to refresh the database provided via button
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */

// read:
// http://code.tutsplus.com/tutorials/android-sdk_fragments--mobile-5367
// http://code.tutsplus.com/tutorials/android-compatibility-working-with-fragments--mobile-5431
// http://android-developers.blogspot.com/2011/02/android-30-fragments-api.html

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
    private MenuItem actions;
    private MenuItem refresh_progress;

    // communication for update service to main thread
    private UpdateStatusBroadcast receiver = new UpdateStatusBroadcast();

    // added for navdrawer
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    // only pops up on add contact action to create the new contact
    NonContactList nonConFrag;
    FragmentManager fm;
    AddConFrag addConFrag;

    /**
     * onCreate - instantiate button to refresh database navigation provided by
     * actionbar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        getActionBar().setTitle("ContactGenerator");
        getActionBar().setDisplayShowTitleEnabled(true);

        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        String[] actions = getResources().getStringArray(R.array.actions);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, actions));
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

        // create all fragment instances and mgr
        fm = getFragmentManager();
        nonConFrag = new NonContactList();
        addConFrag = new AddConFrag();
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i("MainActivity", "LIFE_FLAG onprep_optionsmenu");
        // If the nav drawer is open, hide action items related to the content
        // view
        // boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        // menu.findItem(R.id.actions).setVisible(!drawerOpen);
        if (isMyServiceRunning(UpdateService.class)) {
            setProgressBarIndeterminateVisibility(true);
        } else {
            setProgressBarIndeterminateVisibility(false);
        }
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
        actions = menu.findItem(R.id.actions);

        setProgressBarIndeterminateVisibility(false);

        return super.onCreateOptionsMenu(menu);
    }

    // learn this <?> stuff -- is it reflection (learn more about reflection
    // anyway)
    // http://stackoverflow.com/questions/37628/what-is-reflection-and-why-is-it-useful
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * menu onclick options
     * 
     * @param boolean tells us if click response successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("MainActivity", "LIFE_FLAG click for mainactivit options");
        switch (item.getItemId()) {

        case R.id.updatedb:
            if (!isMyServiceRunning(UpdateService.class)) {
                Log.i("MainActivity", "LIFE_FLAG we start update");
                new AlertDialog.Builder(this)
                        // .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Refresh Database?")
                        .setMessage(
                                "Updates database to reflect current data on phone")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {

                                        setProgressBarIndeterminateVisibility(true);
                                        
                                        Intent intent = new Intent(MainActivity.this, UpdateService.class);
                                        // need below link to ensure service completes before this method...
                                        // http://stackoverflow.com/questions/16934425/call-an-activity-method-from-a-broadcastreceiver-class
                                        startService(intent);
                                    }
                                }).setNegativeButton("No", null).show();
            } else {
                // service is running, so we would only click this again to kill
                // it.
                new AlertDialog.Builder(this)
                        // .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Cancel Update?")
                        .setMessage("Old data will be preserved.")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {

                                        Log.i("MainActivity", "LIFE_FLAG we cancel update");
                                        // take away visibility and kill thing
                                        // (not this yet, alter):
                                        // refresh.setActionView(R.layout.progbar);
                                        // STOPSERVICE
                                        Intent intent = new Intent(
                                                MainActivity.this,
                                                UpdateService.class);
                                        // async task sees that we kill service
                                        // here, and does hiding of progress
                                        // swirl thing etc

                                        // stop service
                                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                        List<RunningAppProcessInfo> runningAppProcesses = am
                                                .getRunningAppProcesses();

                                        Iterator<RunningAppProcessInfo> iter = runningAppProcesses
                                                .iterator();

                                        while (iter.hasNext()) {
                                            RunningAppProcessInfo next = iter
                                                    .next();

                                            String processName = getPackageName()
                                                    + ":service";

                                            if (next.processName
                                                    .equals(processName))
                                                    //&& completed == false) 
                                                {
                                                android.os.Process
                                                        .killProcess(next.pid);
                                                
                                                setProgressBarIndeterminateVisibility(false);
                                                Toast.makeText(MainActivity.this, "Update cancelled.",
                                                        Toast.LENGTH_SHORT).show();                                                
                                                break;
                                            }
                                        }
                                        //
                                        // the above block stops the service
                                        // instead of this
                                        // stopService(intent);

                                    }
                                }).setNegativeButton("No", null).show();
            }
            break;

        // add other options here for tabs that will be added by fragment (in
        // onpause/resume of each)
        // will need to have a method in each fragment acting as a listener
        // essentially for each button,
        // since clicking it will trigger a call to the fragment method to
        // accomplish the action.
        // ExampleFragment fragment = (ExampleFragment)
        // getFragmentManager().findFragmentById(R.id.example_fragment);
        // fragment.<specific_function_name>();

        case R.id.action_addcontact:
            Toast.makeText(this, "Adding contact...", Toast.LENGTH_SHORT)
                    .show();
            // launch the addconfrag, BUT DO IT FROM THE FRAG
            // (call the method which brings out the addconfrag in the nonconexp
            // frag, so gets added to back stack
            fm.beginTransaction().replace(R.id.fragment_container, addConFrag)
                    .addToBackStack(null).commit();
            break;

        case R.id.action_delnoncontact:
            Toast.makeText(this, "Are you sure (implement)...",
                    Toast.LENGTH_SHORT).show();
            // Needs to remove all noncon text/calls on phone, removing all
            // trace of them on phone
            // TODO

            break;

        case R.id.action_seetexts:

            NonContactExplorer fragment = (NonContactExplorer) getFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            fragment.actionBarClick(1);
            break;

        // call frag method with parameter

        case R.id.action_seecalls:

            NonContactExplorer fragment2 = (NonContactExplorer) getFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            fragment2.actionBarClick(2);

            break;

        default:
            break;

        }
        return mDrawerToggle.onOptionsItemSelected(item);
    }
    



    
    public class UpdateStatusBroadcast extends BroadcastReceiver {
        // http://stackoverflow.com/questions/20515966/broadcast-receiver-throughtout-the-application
        // want bcast rcvr available to entire app ... will toast complete no matter
        // where we are in the app
        // after starting service
        // http://www.grokkingandroid.com/android-tutorial-broadcastreceiver/
        
        Activity main = null;
        public void setMainActivityHandler(Activity main){
            this.main = main;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Broadcast indeed!");
            String message = intent.getExtras().getString("message");
            
            setProgressBarIndeterminateVisibility(false);
            Toast.makeText(MainActivity.this, "Update complete.",
                    Toast.LENGTH_SHORT).show();
            
            
        }
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
     * and here only to make concurrent access easier -- (is this smart?)
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
            // logic to return to home screen -- no fragments covering etc
        }
        if (position == 1) {

            Toast.makeText(MainActivity.this, "Showing noncons...",
                    Toast.LENGTH_SHORT).show();
            /*
             * i = new Intent(MainActivity.this, NonContactList.class);
             * System.out.println("*******SWITCHING*******"); startActivity(i);
             */
            // fm.beginTransaction().hide(nonConFrag).commit();
            // fm.beginTransaction().show(nonConFrag).commit();
            fm.beginTransaction().replace(R.id.fragment_container, nonConFrag)
                    .addToBackStack(null).commit();
        }
        if (position == 2) {

            Toast.makeText(MainActivity.this, "Showing contacts",
                    Toast.LENGTH_SHORT).show();
            /*
             * i = new Intent(MainActivity.this, ContactList.class);
             * System.out.println("*******SWITCHING*******"); startActivity(i);
             */
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