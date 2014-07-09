package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;

/**
 * Listview displaying all noncontacts- can click on a noncontact to explore
 * data available on phone and ultimately add it as a contact if desired
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class NonCons extends Activity {

    ArrayList<String> noncons;
    DatabaseHelper sqldb;
    ListView nonContactList;
    ArrayAdapter<String> nonContactAdapter;

    /**
     * onCreate - instantiate and display the listview
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noncons);

        //getActionBar().hide();

        sqldb = DatabaseHelper.getInstance(getApplicationContext());

        noncons = sqldb.fetchAllNonContacts();
        System.out.println("noncons are: " + noncons);
        sqldb.close();

        nonContactList = (ListView) findViewById(R.id.nonContactList);
        nonContactList.setVerticalScrollBarEnabled(true);

        nonContactAdapter = new ArrayAdapter<String>(NonCons.this,
                android.R.layout.simple_list_item_1, noncons);

        nonContactList.setAdapter(nonContactAdapter);
        nonContactList.setClickable(true);
        nonContactList.setItemsCanFocus(false);
        nonContactList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView a, View v, int position, long id) {
                System.out.println("an item has been clicked");

                String number = noncons.get(position);
                Intent i = new Intent(NonCons.this, NonConExplorer.class);

                i.putExtra("number", number);
                startActivity(i);
            }
        });

    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.noncon, menu);
        return true;
    }
    
    /**
     * refresh LV if any changes made
     */
    public void onResume() {
        super.onResume();
        sqldb = DatabaseHelper.getInstance(getApplicationContext());
        noncons = sqldb.fetchAllNonContacts();
        System.out.println("noncons are: " + noncons);
        sqldb.close();
        nonContactAdapter = new ArrayAdapter<String>(NonCons.this,
                android.R.layout.simple_list_item_1, noncons);
        nonContactAdapter.notifyDataSetChanged();
        nonContactList.setAdapter(nonContactAdapter);  
    }

    public void onPause() {
        super.onPause();

    }

}
