package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Entities.Contact;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;

/**
 * Listview displaying all contacts- can click on a noncontact to explore
 * data available on phone and ultimately add it as a contact if desired
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class ContactList extends Fragment {

    DatabaseHelper sqldb;
    ArrayList<String> numberList;
    ArrayAdapter<String> numberAdapter;

    // if we start the service and it finishes this tells us it is done
    // registering controlled in onresume/onpause

    /**
     * onCreate - instantiate and display the listview
     */
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listactivity);

        refreshLV();

        getListView().setVerticalScrollBarEnabled(true);
        getListView().setClickable(true);
        getListView().setItemsCanFocus(false);
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView a, View v, int position, long id) {
                System.out.println("an item has been clicked");

                String number = numberList.get(position);
                Intent i = new Intent(ContactList.this, ContactExplorer.class);

                i.putExtra("number", number);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blank, menu);
        return true;
    }
*/
    /**
     * refresh LV if any changes made by re-fetching data
     */
/*
    public void refreshLV() {
        sqldb = DatabaseHelper.getInstance(getApplicationContext());

        numberList = new ArrayList<String>();
        for (Contact c : sqldb.fetchAllContacts()) {
            if (!(c.toString() == null))
                numberList.add(c.toString());
        }

        numberAdapter = new ArrayAdapter<String>(ContactList.this,
                android.R.layout.simple_list_item_1, numberList);
        numberAdapter.notifyDataSetChanged();
        getListView().setAdapter(numberAdapter);

    }
*/
    /**
     * un/re-register receiver as this activity becomes in/active
     */
/*
    public void onResume() {
        super.onResume();

        refreshLV();

        registerReceiver(receiver, new IntentFilter(
                "com.kevin.contactgenerator.Utilities"));
        getActionBar().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
*/
    
}
