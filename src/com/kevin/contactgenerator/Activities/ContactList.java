package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
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
 * Listview displaying all contacts- can click on a noncontact to explore data
 * available on phone and ultimately add it as a contact if desired
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class ContactList extends Fragment {

    DatabaseHelper sqldb;
    ArrayList<Contact> numberList;
    ArrayAdapter<Contact> numberAdapter;
    ContactExplorer conExpFrag;
    ListView lv;

    /**
     * onCreate - instantiate and display the listview
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_listactivity, container,
                false);

        conExpFrag = new ContactExplorer();

        lv = (ListView) view.findViewById(R.id.list1);
        lv.setVerticalScrollBarEnabled(true);
        lv.setClickable(true);
        lv.setItemsCanFocus(false);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView a, View v, int position, long id) {
                System.out.println("an item has been clicked");
                String number = numberList.get(position).getNumber();
                String name = numberList.get(position).getName();
                // rather than passing number in the intent used to start the
                // explorer activity, do it when you start the fragment with a
                // bundle or something
                Bundle bundle = new Bundle();
                bundle.putString("number", number);
                bundle.putString("name", name);
                
                conExpFrag.setArguments(bundle);

                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, conExpFrag)
                        .addToBackStack(null).commit();
            }
        });

        // go back to calling this in onresume???
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
        sqldb = DatabaseHelper.getInstance(getActivity()
                .getApplicationContext());

        numberList = new ArrayList<Contact>();
        for (Contact c : sqldb.fetchAllContacts()) {
            numberList.add(c);
        }

        numberAdapter = new ArrayAdapter<Contact>(getActivity()
                .getApplicationContext(), android.R.layout.simple_list_item_1,
                numberList);
        numberAdapter.notifyDataSetChanged();
        lv.setAdapter(numberAdapter);

    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

}
