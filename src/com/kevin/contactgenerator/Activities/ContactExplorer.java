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
 * explore the data on phone relating to a contact; can also remove contact from
 * phone
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 * 
 */
public class ContactExplorer extends ListActivity {

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
        setContentView(R.layout.activity_con_exp);

        // receiving the number that was clicked...
        Intent intent = getIntent();
        number = intent.getExtras().getString("number");

        sqldb = DatabaseHelper.getInstance(getApplicationContext());
        texts = sqldb.fetchTexts(number);
        calls = sqldb.fetchCalls(number);

        getListView().setVerticalScrollBarEnabled(true);
        getListView().setClickable(true);
        getListView().setItemsCanFocus(false);

        textAdapter = new CustomAdapter<TextMsg>(ContactExplorer.this,
                android.R.layout.simple_list_item_1, texts, "TEXTS");
        callAdapter = new CustomAdapter<LoggedCall>(ContactExplorer.this,
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

    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * 
     * @return boolean indicates if successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.curcon, menu);
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
        case R.id.action_delcontact:
            Toast.makeText(this, "Removing contact...", Toast.LENGTH_SHORT)
                    .show();
            // http://stackoverflow.com/questions/527216/how-to-remove-a-contact-programmatically-in-android
            // deleteContact();

            break;
        // action bar option to see texts for this contact selected
        case R.id.action_seetexts:
            Toast.makeText(ContactExplorer.this, "Showing texts for " + number,
                    Toast.LENGTH_SHORT).show();
            setListAdapter(textAdapter);

            break;
        // action bar option to see calls for this contact selected
        case R.id.action_seecalls:
            Toast.makeText(ContactExplorer.this, "Showing calls for " + number,
                    Toast.LENGTH_SHORT).show();
            setListAdapter(callAdapter);

            break;
        default:
            break;
        }
        return true;
    }

    /**
     * BroadcastReceiver registration
     */
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
