package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Models.Contact;
import com.kevin.contactgenerator.Utilities.CustomAdapter;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;

/**
 * listview containing all contacts that exist on phone (shows name and number
 * currently)
 * 
 * TO BE COMPLETED
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class Cons extends Activity {

    ArrayList<Contact> contacts;
    DatabaseHelper sqldb;

    ListView contactList;
    ArrayAdapter<Contact> contactAdapter;

    /**
     * onCreate - instantiate and display the listview
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cons);

        sqldb = DatabaseHelper.getInstance(Cons.this);
        contacts = sqldb.fetchAllContacts();
        sqldb.close();

        contactAdapter = new CustomAdapter<Contact>(Cons.this,
                android.R.layout.simple_list_item_1, contacts, "CONTACTS");

        contactList = (ListView) findViewById(R.id.contactList);
        contactList.setVerticalScrollBarEnabled(true);
        contactList.setAdapter(contactAdapter);
        
        contactList.setClickable(true);
        contactList.setItemsCanFocus(false);
        contactList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView a, View v, int position, long id) {
                System.out.println("an item has been clicked");

                String number = contacts.get(position).getNumber();
                //Intent i = new Intent(Cons.this, ConExplorer.class);

                //i.putExtra("number", number);
                //startActivity(i);
            }
        });

    }

    /*
     * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
     * menu; this adds items to the action bar if it is present.
     * getMenuInflater().inflate(R.menu.newcon, menu); return true; }
     */

    /**
     * refresh LV if any changes made
     */
    public void onResume() {
        super.onResume();
        sqldb = DatabaseHelper.getInstance(getApplicationContext());
        contacts = sqldb.fetchAllContacts();
        System.out.println("cons are: " + contacts);
        sqldb.close();
        contactAdapter = new CustomAdapter<Contact>(Cons.this,
                android.R.layout.simple_list_item_1, contacts, "CONTACTS");
        contactAdapter.notifyDataSetChanged();
        contactList.setAdapter(contactAdapter);
    }

    public void onPause() {
        super.onPause();

    }

}
