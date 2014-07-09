package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
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

/**
 * explore the data on phone relating to a number which is not already a contact
 * after exploring, make decision to add or not
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class NonConExplorer extends ListActivity {

    DatabaseHelper sqldb;

    // number we clicked on
    String number;

    // ListView info;
    ArrayAdapter<TextMsg> textAdapter;
    ArrayAdapter<LoggedCall> callAdapter;
    ArrayList<TextMsg> texts;
    ArrayList<LoggedCall> calls;

    // only pops up on add contact action to create the new contact
    Button insert_db;
    EditText enter_name;
    EditText number_entry;

    /**
     * onCreate - instantiate multiple adapters 
     * want to be able to show texts or calls depending on what is clicked
     * 
     * additionally, based on actionbar click, can hide listview and 
     * display entry form for adding this noncon as a contact
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noncon_exp);

        // receiving the number that was clicked on noncon...
        Intent intent = getIntent();
        number = intent.getExtras().getString("number");

        sqldb = DatabaseHelper.getInstance(getApplicationContext());

        texts = sqldb.fetchTexts(number);
        calls = sqldb.fetchCalls(number);

        sqldb.close();

        getListView().setVerticalScrollBarEnabled(true);
        getListView().setClickable(true);
        getListView().setItemsCanFocus(false);

        textAdapter = new CustomAdapter<TextMsg>(NonConExplorer.this,
                android.R.layout.simple_list_item_1, texts, "TEXTS");
        callAdapter = new CustomAdapter<LoggedCall>(NonConExplorer.this,
                android.R.layout.simple_list_item_1, calls, "CALLS");

        // default to showing texts
        setListAdapter(textAdapter);

        getActionBar().show();

        Toast.makeText(
                this,
                "Currently showing texts for this contact; "
                        + "choose an from the actionbar for other options",
                Toast.LENGTH_SHORT).show();

        // method for populating and then hiding the edittext used to create a
        // new contact until we want to see it
        hideAddCon();
        // initialize the edittext being hidden with the number clicked
        number_entry = (EditText) findViewById(R.id.number_entry);
        number_entry.setText(number);
        // below is button in part of the addcon layout that is also hidden at
        // first
        insert_db = (Button) findViewById(R.id.insert_db);
        insert_db.setBackgroundColor(Color.RED);
        insert_db.setTextColor(Color.BLACK);

        insert_db.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // still need to add logic to delete from noncons etc after
                // adding
                // add to contacts, remove from noncons, whole thing updates
                // next refresh
                String name = ((EditText) findViewById(R.id.enter_name))
                        .getText().toString();
                String number_field = ((EditText) findViewById(R.id.number_entry))
                        .getText().toString();
                if (name != null && number_field != null) {
                    addContact(name, number_field);
                    Toast.makeText(NonConExplorer.this, "Contact Inserted!",
                            Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(NonConExplorer.this, "NO NULLS ALLOWED",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        // put onitemclicklistener for LV here - show full info for text/call
        // clicked on
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @return boolean indicates if successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newcon, menu);
        return true;
    }

    /**
     * Dictates behavior based on actionbar clicks
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
        // action bar option to see texts for this non-contact selected
        case R.id.action_seetexts:
            Toast.makeText(NonConExplorer.this, "Showing texts for " + number,
                    Toast.LENGTH_SHORT).show();
            setListAdapter(textAdapter);
            hideAddCon();
            break;
        // action bar option to see calls for this non-contact selected
        case R.id.action_seecalls:
            Toast.makeText(NonConExplorer.this, "Showing calls for " + number,
                    Toast.LENGTH_SHORT).show();
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
     * physically adds a new contact to the phone accessible 
     * from outside of application
     * 
     * @param name the name of the contact being added
     * @param number the number of the contact being added
     */
    private void addContact(String name, String number_field) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null).build());
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, number_field).build());
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, name).build());
        try {
            ContentProviderResult[] res = getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sqldb = DatabaseHelper.getInstance(getApplicationContext());
        sqldb.deleteNonContact(number_field);
        
    }

    /**
     * hide/show methods for switching between entry methods and 
     * text/call listviews depending on actionbar click
     */
    
    public void hideAddCon() {
        // don't make these elements visible until we are on the addcon
        // listadapter
        enter_name = (EditText) findViewById(R.id.enter_name);
        enter_name.setVisibility(View.GONE);
        number_entry = (EditText) findViewById(R.id.number_entry);
        number_entry.setVisibility(View.GONE);
        insert_db = (Button) findViewById(R.id.insert_db);
        insert_db.setVisibility(View.GONE);
        // when hiding these we want to ensure the LV is visible too
        getListView().setVisibility(View.VISIBLE);
    }

    public void showAddCon() {
        enter_name = (EditText) findViewById(R.id.enter_name);
        enter_name.setVisibility(View.VISIBLE);
        number_entry = (EditText) findViewById(R.id.number_entry);
        number_entry.setVisibility(View.VISIBLE);
        insert_db = (Button) findViewById(R.id.insert_db);
        insert_db.setVisibility(View.VISIBLE);
        // when this shows we want to also hide the LV
        getListView().setVisibility(View.GONE);
    }

    /**
     * we only want the ActionBar visible to this activity
     */
    public void onPause() {
        super.onPause();
        //getActionBar().hide();
    }

    public void onResume() {
        super.onResume();
        getActionBar().show();
    }

}
