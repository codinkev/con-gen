package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Entities.Contact;
import com.kevin.contactgenerator.Utilities.DatabaseHelper;

import android.app.Fragment;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Independent component for contact entry; used in combination with
 * NonContactExplorer
 * 
 * do fragments need to configured for different orientations??? (am i missing
 * anything on this setup)
 * 
 * @author kevin
 * 
 */
public class AddConFrag extends Fragment {
    // http://stackoverflow.com/questions/14347588/show-hide-fragment-in-android
    // http://www.vogella.com/tutorials/AndroidFragments/article.html
    String number;
    EditText number_entry;
    View view;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_addcon, container, false);
        number = getArguments().getString("number");
        System.out.println("ADDCONFRAG -- number: "+number);
        //setNumber(number);
        
        //WHY NPE EXCEPTION HERE: HOW FIND EDITTEXT WITHIN A FRAGMENT IS PROB THE ISSUE!
        number_entry = (EditText) view.findViewById(
                R.id.number_entry);
        number_entry.setText(number);
        
        
        Button insert_db = (Button) view.findViewById(R.id.insert_db);
        insert_db.setBackgroundColor(Color.RED);
        insert_db.setTextColor(Color.BLACK);

        insert_db.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // physically insert the contact with entered fields to db
                String name = ((EditText) view.findViewById(
                        R.id.enter_name)).getText().toString();
                String number_field = (number_entry).getText().toString();
                if (name != null && number_field != null) {
                    addContact(name, number_field);
                    Toast.makeText(getActivity(), "Contact Inserted!",
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "NO NULLS ALLOWED",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    // initialize the edittext number entry with the number clicked
    //public void setNumber(String number) {
    //    EditText number_entry = (EditText) getView().findViewById(
    //            R.id.number_entry);
    //    number_entry.setText(number);
    //}

    public void setName(String name) {

    }

    /**
     * physically adds a new contact to the phone accessible from outside of
     * application
     * 
     * @param name
     *            the name of the contact being added
     * @param number
     *            the number of the contact being added
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
            ContentProviderResult[] res = getActivity().getContentResolver()
                    .applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        DatabaseHelper sqldb = DatabaseHelper.getInstance(getActivity());
        sqldb.recordUpdate(new Contact(name, number_field));
        //incrementally remove this number from noncon list now, and add to contact list, so tables are up to date without needing to refresh
        //so obviously do this same thing for remove contact, etc (take them off the contact list, just leave off noncon too)
        //sqldb.        
        //so basically add this name and number dynamically to contacts
        //sqldb.insertContacts(new Contact(name, number_field));
    }
}
