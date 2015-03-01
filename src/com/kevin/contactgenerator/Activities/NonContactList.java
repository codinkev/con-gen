package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Entities.Contact;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;

/**
 * Listview displaying all noncontacts- can click on a noncontact to explore
 * data available on phone and ultimately add it as a contact if desired
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */

//http://stackoverflow.com/questions/16575177/fragment-is-transparent-and-shows-activity-below
public class NonContactList extends Fragment {

    //WHY IF THIS IS SINGLETON DO I WANT IT TO BE PRIVATE?  test as public?
    private static DatabaseHelper sqldb = null;
    
    ArrayList<String> numberList;
    ArrayAdapter<String> numberAdapter;
    ListView lv;
    NonContactExplorer nonConExpFrag;

    /**
     * onCreate - instantiate and display the listview
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_listactivity, container,
                false);
        
        nonConExpFrag = new NonContactExplorer();
        
        lv = (ListView) view.findViewById(R.id.list1);   
        lv.setVerticalScrollBarEnabled(true);
        lv.setClickable(true);
        lv.setItemsCanFocus(false);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView a, View v, int position, long id) {
                System.out.println("an item has been clicked");
                String number = numberList.get(position);
                // rather than passing number in the intent used to start the
                // explorer activity, do it when you start the fragment with a
                // bundle or something
                Bundle bundle = new Bundle();
                bundle.putString("number", number );
                
                nonConExpFrag.setArguments(bundle);
                
                getActivity().getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, nonConExpFrag)
                .addToBackStack(null)
                .commit();
            }
        });
        
        //go back to calling this in onresume???
        refreshLV();
        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * refresh LV if any changes made by re-fetching data
     */
    public void refreshLV() { 
            sqldb = DatabaseHelper.getInstance(getActivity().getApplicationContext());
            
            numberList = new ArrayList<String>();
            numberList = sqldb.fetchAllNonContacts();
            
            // http://stackoverflow.com/questions/8215308/using-context-in-fragment
            numberAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                    android.R.layout.simple_list_item_1, numberList);
            numberAdapter.notifyDataSetChanged();
            
            lv.setAdapter(numberAdapter);
    }

    /**
     * un/re-register receiver as this activity becomes in/active
     */
    public void onResume() {
        super.onResume();
        //refreshLV();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
