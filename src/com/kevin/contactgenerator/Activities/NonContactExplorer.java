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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Entities.LoggedCall;
import com.kevin.contactgenerator.Entities.TextMsg;
import com.kevin.contactgenerator.Utilities.CustomAdapter;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;

/**
 * explore the data on phone relating to a number after exploring, make decision
 * to add or not
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 * 
 */
public class NonContactExplorer extends Fragment {

    //WHY IF THIS IS SINGLETON DO I WANT IT TO BE PRIVATE?  test as public?
    private static DatabaseHelper sqldb = null;

    // number we clicked on
    String number;

    // ListView info;
    ArrayAdapter<TextMsg> textAdapter;
    ArrayAdapter<LoggedCall> callAdapter;
    ArrayList<TextMsg> texts;
    ArrayList<LoggedCall> calls;
    ListView lv;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        System.out.println("Called Oncreate");
        View view = inflater.inflate(R.layout.activity_listactivity, container,
                false);

        // receiving the number that was clicked...
        // Intent intent = getActivity().getIntent();
        // number = intent.getExtras().getString("number");
        number = getArguments().getString("number");

        sqldb = DatabaseHelper.getInstance(getActivity()
                .getApplicationContext());
        texts = sqldb.fetchTexts(number);
        calls = sqldb.fetchCalls(number);

        lv = (ListView) view.findViewById(R.id.list1);
        lv.setVerticalScrollBarEnabled(true);
        lv.setClickable(true);
        lv.setItemsCanFocus(false);

        textAdapter = new CustomAdapter<TextMsg>(getActivity()
                .getApplicationContext(), android.R.layout.simple_list_item_1,
                texts, "TEXTS");
        callAdapter = new CustomAdapter<LoggedCall>(getActivity()
                .getApplicationContext(), android.R.layout.simple_list_item_1,
                calls, "CALLS");

        // default to showing texts
        lv.setAdapter(textAdapter);

        // put onitemclicklistener for LV here - show full info for text/call
        // clicked on

        // hiding the addCon fragment
        fm = getFragmentManager();
        // addConFrag = (AddConFrag) fm.findFragmentById(R.id.addConFrag);
        // addConFrag.setNumber(number);

        // what is beginTransaction
        // fm.beginTransaction().hide(addConFrag).commit();

        setHasOptionsMenu(true);
        return view;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinf) {
        System.out.println("nonconexp options!");
            
        // Inflate the menu; this adds items to the action bar if it is present.
        menuinf=getActivity().getMenuInflater();
        menuinf.inflate(R.menu.newcon, menu);
        menu.setGroupVisible(R.id.main_menu_group, true);
        menu.setGroupVisible(R.id.main_menu_group2, false);
        menu.setGroupVisible(R.id.main_menu_group3, false);
        return;
    }    

    // is this the best way to use intentfilter
    // how to do this for every activity without having to re/un-register
    @Override
    public void onResume() {
        super.onResume();
        // toggle options stuff on/off in all frags like this...

    }

    @Override
    public void onPause() {
        super.onPause();
        //sqldb.close();
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("MainActivity", "LIFE_FLAG click for mainactivit options");
        NonContactExplorer fragment = (NonContactExplorer) getFragmentManager()
                .findFragmentById(R.id.fragment_container);
        switch (item.getItemId()) {

        case R.id.action_seetexts:
            lv.setAdapter(textAdapter);
            return true;

        // call frag method with parameter

        case R.id.action_seecalls:
            lv.setAdapter(callAdapter);
            return true;
            
        //case R.id.action_delnoncontact:
        //    return true;
            
        case R.id.action_addcontact:
            Bundle bundle = new Bundle();
            bundle.putString("number", number);
            addConFrag = new AddConFrag();
            addConFrag.setArguments(bundle);
            fm.beginTransaction().replace(R.id.fragment_container, addConFrag)
                    .addToBackStack(null).commit();
            return true;
        
        default:
            return super.onOptionsItemSelected(item);

        }
        
       
    }
    

}
