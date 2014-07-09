package com.kevin.contactgenerator.Utilities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kevin.contactgenerator.Models.Contact;
import com.kevin.contactgenerator.Models.LoggedCall;
import com.kevin.contactgenerator.Models.TextMsg;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

/**
 * globally accessible methods for refreshing the database via phone data
 * 
 * @author kevin
 */
public class RefreshTables extends Application {
    // http://stackoverflow.com/questions/14446915/multiple-classes-extending-application
    // http://stackoverflow.com/questions/4521117/android-having-trouble-creating-a-subclass-of-application-to-share-data-with-mu
    // http://stackoverflow.com/questions/4572338/extending-application-to-share-variables-globally

    DatabaseHelper sqldb;

    /**
     * desired functionality: completely erase and re-update regardless if
     * tables exist
     */
    public void totallyRefresh() {
        sqldb = DatabaseHelper.getInstance(getApplicationContext());
        sqldb.recreateTables();

        // we can't have contacts run asynchronously because noncons depends on
        // it
        getContacts();

        // now that we can check against existing cons, run text and call
        // refresh asynchronously
        // this is the major overhead load anyway (more texts/calls than
        // contacts)
        Runnable texts = new Runnable() {
            public void run() {
                getTextDetails();
            }
        };
        Runnable calls = new Runnable() {
            public void run() {
                getCallDetails();
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(texts);
        executor.execute(calls);

        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        System.out.println("finished populating database");
        sqldb.close();
    }

    // content provider access
    // http://developer.android.com/guide/topics/providers/content-provider-basics.html

    /**
     * existing contacts refresh - get all current contact data in our sqlite
     * database
     */
    private void getContacts() {

        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if (Integer.parseInt(cursor.getString(
            // iterating over EACH number associated with EACH name (internal
            // cursor)
            cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor pCur = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?", new String[] { id }, null);
                while (pCur.moveToNext()) {
                    String phoneNo = pCur
                            .getString(pCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    System.out.println("Name: " + name + ", Phone No: "
                            + phoneNo);

                    // insert each number associated with the contact
                    sqldb.insertContacts(new Contact(name, phoneNo));

                }
                pCur.close();
            }
        }
        cursor.close();

    }

    /**
     * text refresh - store all text data in our database
     */
    private void getTextDetails() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://sms/inbox"), null, null, null, null);
        cursor.moveToFirst();

        /*
         * FIELDS: (this is an undocumented content provider) 0: _id 1:
         * thread_id 2: address 3: person 4: date 5: protocol 6: read 7: status
         * 8: type 9: reply_path_present 10: subject 11: body -- APPARENTLY 14
         * 12: service_center 13: locked 14: body
         */
        do {
            String number = cursor.getString(2);
            String contact = cursor.getString(3);
            String datehold = cursor.getString(4);
            String date = (new Date(Long.valueOf(datehold))).toString();
            String body = cursor.getString(14);
            System.out.println("TESTING");
            System.out.println("number: " + number + " contact: " + contact
                    + " date: " + date + " body: " + body);

            sqldb.insertTexts(new TextMsg(number, contact, date, body));

        } while (cursor.moveToNext());

        cursor.close();
    }

    /**
     * call refresh - store all call data in our database
     */
    private void getCallDetails() {

        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI, null, null, null, null);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            String callDayTime = (new Date(Long.valueOf(callDate))).toString();
            Integer callDuration = Integer.valueOf(managedCursor
                    .getString(duration));
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dir = "OUTGOING";
                break;

            case CallLog.Calls.INCOMING_TYPE:
                dir = "INCOMING";
                break;

            case CallLog.Calls.MISSED_TYPE:
                dir = "MISSED";
                break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");

            sqldb.insertCalls(new LoggedCall(phNumber, dir, callDayTime,
                    callDuration));
        }
        managedCursor.close();
    }
}
