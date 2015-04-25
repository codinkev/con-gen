package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Entities.Contact;
import com.kevin.contactgenerator.Entities.LoggedCall;
import com.kevin.contactgenerator.Entities.TextMsg;
import com.kevin.contactgenerator.Utilities.CustomAdapter;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;

/**
 * explore the data on phone relating to a contact; can also remove contact from
 * phone
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 * 
 */
public class ContactExplorer extends Fragment {

    DatabaseHelper sqldb;

    // number/name of contact we clicked on
    String number;
    String name;

    // ListView info;
    ArrayAdapter<TextMsg> textAdapter;
    ArrayAdapter<LoggedCall> callAdapter;
    ArrayList<TextMsg> texts;
    ArrayList<LoggedCall> calls;

    ListView lv;
    EditText inputSearch;

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
        name = getArguments().getString("name");

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

        setHasOptionsMenu(true);

        inputSearch = (EditText) view.findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                    int arg3) {
                // When user changed the Text
                // this needs to become a function which can filter the texts
                // and calls ... use contactlist as an example (though that
                // filters numbers only, same concepts are used)
                refreshLV(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        return view;
    }
    
    public void refreshLV(CharSequence cs) {
        sqldb = DatabaseHelper.getInstance(getActivity()
                .getApplicationContext());
        
        ((ArrayAdapter)lv.getAdapter()).getFilter().filter(cs);
        
        ((ArrayAdapter)lv.getAdapter()).notifyDataSetChanged();
        lv.setAdapter(((ArrayAdapter)lv.getAdapter()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinf) {
        System.out.println("conexp options!");

        // Inflate the menu; this adds items to the action bar if it is present.
        menuinf = getActivity().getMenuInflater();
        menuinf.inflate(R.menu.curcon, menu);
        menu.setGroupVisible(R.id.main_menu_group3, true);
        menu.setGroupVisible(R.id.main_menu_group2, false);
        menu.setGroupVisible(R.id.main_menu_group, false);
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
        // sqldb.close();
    }

    public void deleteContact() {
        System.out.println("DELCON -- " + number + " " + name);
        Uri contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        Cursor cur = getActivity().getApplicationContext().getContentResolver()
                .query(contactUri, null, null, null, null);
        int rows = 0;
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(
                            cur.getColumnIndex(PhoneLookup.DISPLAY_NAME))
                            .equalsIgnoreCase(name)) {
                        System.out.println("DELCON -- name is: " + name);
                        String lookupKey = cur
                                .getString(cur
                                        .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(
                                ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                                lookupKey);
                        // System.out.println("ROWS DELETED: "+getActivity().getApplicationContext().getContentResolver().delete(uri,
                        // null, null));
                        if ((rows = getActivity().getApplicationContext()
                                .getContentResolver().delete(uri, null, null)) >= 1) {
                            System.out.println("(FIRST TIME) ROWS DELETED: "
                                    + rows);
                            sqldb.removeContact(new Contact(name, number));
                        }
                        System.out.println("(SECOND TIME) ROWS DELETED: "
                                + rows);
                        cur.moveToLast();
                    } else {
                    }
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
        } finally {
            Toast.makeText(getActivity().getApplicationContext(),
                    "" + rows + " contact(s) deleted.", Toast.LENGTH_SHORT)
                    .show();
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("MainActivity", "LIFE_FLAG click for mainactivit options");
        ContactExplorer fragment = (ContactExplorer) getFragmentManager()
                .findFragmentById(R.id.fragment_container);
        switch (item.getItemId()) {

        case R.id.action_seetexts:
            Log.i("MainActivity", "curcon seetexts selected");
            lv.setAdapter(textAdapter);
            return true;

            // call frag method with parameter

        case R.id.action_seecalls:
            Log.i("MainActivity", "curcon seecalls selected");
            lv.setAdapter(callAdapter);
            return true;

        case R.id.action_delcontact:
            Log.i("MainActivity", "curcon delnoncon selected");
            deleteContact();
            return true;

        default:
            return super.onOptionsItemSelected(item);

        }
        // return super.onOptionsItemSelected(item);

    }

}
