package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Models.LoggedCall;
import com.kevin.contactgenerator.Models.TextMsg;
import com.kevin.contactgenerator.Utilities.CustomAdapter;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;
import com.kevin.contactgenerator.Utilities.UpdateStatusBroadcast;

/**
 * explore the data on phone relating to a number after exploring, make decision
 * to add or not
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 * 
 */
public class NonContactExplorer extends ListActivity {

    // if we start the service and it finishes this tells us it is done
    // registering controlled in onresume/onpause
    private BroadcastReceiver receiver = new UpdateStatusBroadcast();

    DatabaseHelper sqldb;

    // number we clicked on
    String number;

    // ListView info;
    ArrayAdapter<TextMsg> textAdapter;
    ArrayAdapter<LoggedCall> callAdapter;
    ArrayList<TextMsg> texts;
    ArrayList<LoggedCall> calls;

    // only pops up on add contact action to create the new contact
    AddConFrag addConFrag;
    FragmentManager fm;

    /**
     * onCreate - instantiate multiple adapters want to be able to show texts or
     * calls depending on what is clicked
     * 
     * additionally, based on actionbar click, can hide listview and display
     * entry form for adding this noncon as a contact
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noncon_exp);

        // receiving the number that was clicked...
        Intent intent = getIntent();
        number = intent.getExtras().getString("number");

        sqldb = DatabaseHelper.getInstance(getApplicationContext());
        texts = sqldb.fetchTexts(number);
        calls = sqldb.fetchCalls(number);

        getListView().setVerticalScrollBarEnabled(true);
        getListView().setClickable(true);
        getListView().setItemsCanFocus(false);

        textAdapter = new CustomAdapter<TextMsg>(NonContactExplorer.this,
                android.R.layout.simple_list_item_1, texts, "TEXTS");
        callAdapter = new CustomAdapter<LoggedCall>(NonContactExplorer.this,
                android.R.layout.simple_list_item_1, calls, "CALLS");

        // default to showing texts
        setListAdapter(textAdapter);

        // put onitemclicklistener for LV here - show full info for text/call
        // clicked on

        Toast.makeText(
                this,
                "Currently showing texts for this contact; "
                        + "choose an from the actionbar for other options",
                Toast.LENGTH_SHORT).show();

        // hiding the addCon fragment
        fm = getFragmentManager();
        addConFrag = (AddConFrag) fm.findFragmentById(R.id.addConFrag);
        addConFrag.setNumber(number);

        // what is beginTransaction
        fm.beginTransaction().hide(addConFrag).commit();

    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * 
     * @return boolean indicates if successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newcon, menu);
        return true;
    }

    /**
     * Dictates behavior based on actionbar clicks
     * 
     * @return boolean indicates if successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // action bar option to add contact selected
        case R.id.action_addcontact:
            Toast.makeText(this, "Adding contact...", Toast.LENGTH_SHORT)
                    .show();
            showAddCon();
            break;
        case R.id.action_delnoncontact:
            Toast.makeText(this, "Are you sure (implement)...",
                    Toast.LENGTH_SHORT).show();
            // put an alertdialog here which warns it takes all of their
            // texts/calls off the phone
            // and then actually remove that stuff from the phone and take them
            // out of the sql db

            // http://stackoverflow.com/questions/419184/how-to-delete-an-sms-from-the-inbox-in-android-programmatically
            // http://stackoverflow.com/questions/10947283/how-to-remove-call-logs-from-android-programmatically
            // http://stackoverflow.com/questions/16767671/delete-particular-log-from-call-log
            // (if above doesn't work)

            break;
        // action bar option to see texts for this non-contact selected
        case R.id.action_seetexts:
            Toast.makeText(NonContactExplorer.this,
                    "Showing texts for " + number, Toast.LENGTH_SHORT).show();
            setListAdapter(textAdapter);
            hideAddCon();
            break;
        // action bar option to see calls for this non-contact selected
        case R.id.action_seecalls:
            Toast.makeText(NonContactExplorer.this,
                    "Showing calls for " + number, Toast.LENGTH_SHORT).show();
            setListAdapter(callAdapter);
            hideAddCon();
            break;
        default:
            break;
        }
        return true;
    }

    //

    /**
     * hide/show methods for switching between entry methods and text/call
     * listviews depending on actionbar click (find way to combine these based
     * on current state of the frag...)
     */

    public void hideAddCon() {
        // toggling off the fragment for contact entry
        fm.beginTransaction().hide(addConFrag).commit();
        // when hiding this we want to ensure the LV is visible too
        getListView().setVisibility(View.VISIBLE);
    }

    public void showAddCon() {
        // toggling on the fragment for contact entry
        fm.beginTransaction().show(addConFrag).commit();
        // when this shows we want to also hide the LV
        getListView().setVisibility(View.GONE);
    }

    /**
     * BroadcastReceiver registration
     */
    // is this the best way to use intentfilter
    // how to do this for every activity without having to re/un-register
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                "com.kevin.contactgenerator.Utilities"));
        getActionBar().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

}
