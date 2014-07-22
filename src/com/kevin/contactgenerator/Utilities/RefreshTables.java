package com.kevin.contactgenerator.Utilities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kevin.contactgenerator.Activities.MainActivity;
import com.kevin.contactgenerator.Models.Contact;
import com.kevin.contactgenerator.Models.LoggedCall;
import com.kevin.contactgenerator.Models.TextMsg;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.provider.ContactsContract;

/**
 * globally accessible methods for refreshing the database via phone data
 * 
 * @author kevin
 */
public class RefreshTables implements Runnable {
    // http://stackoverflow.com/questions/14446915/multiple-classes-extending-application
    // http://stackoverflow.com/questions/4521117/android-having-trouble-creating-a-subclass-of-application-to-share-data-with-mu
    // http://stackoverflow.com/questions/4572338/extending-application-to-share-variables-globally

    DatabaseHelper sqldb;
    Context context;
    
    // used while refreshing database
    ProgressDialog warning;

    /**
     * 
     * @param context
     *            activity calling the method
     */
    public RefreshTables(Context curcontext) {
        this.context = curcontext;
    }

    /**
     * desired functionality: completely erase and re-update regardless if
     * tables exist
     * 
     * find way to avoid having more runnables inside run method also need a
     * way, if the interrupt happens in that small time frame where the main
     * tables are dropped and the switching is occurring, that that operation
     * must complete ... (synchronized or something?) also need way to make this
     * incremental (so that if cancelled at least still has progress saved...)
     */
    public void run() {
        
        while (!Thread.currentThread().isInterrupted()) {
            
            // progress bar for data update
            warning = ProgressDialog.show(context, "",
                    "Updating available data. Please wait...", true);

            warning.setCancelable(true);
            warning.setCanceledOnTouchOutside(true);
            // what we show when the user prevents the update from
            // completing
            // need better distinction of dismiss vs. cancel...
            warning.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(final DialogInterface dialog) {

                    // stop the update
                    Thread.currentThread().interrupt();

                    new AlertDialog.Builder(context)
                            .setTitle("Update dismissed")
                            .setMessage("Original information preserved.")
                            .setPositiveButton(android.R.string.ok, null)
                            .setCancelable(false).create().show();
                }
            });

            warning.show();
            System.out.println("running...");
            
            // now start the update
            // ensure tables are reset no matter what
            this.sqldb = DatabaseHelper.getInstance(context);

            // we can't have contacts run asynchronously because noncons depends
            // on
            // it
            getContacts(context);

            // now that we can check against existing cons, run text and call
            // refresh asynchronously
            // this is the major overhead load anyway (more texts/calls than
            // contacts)
            Runnable texts = new Runnable() {
                public void run() {
                    System.out.println("fetching texts...");
                    getTextDetails(context);
                }
            };
            Runnable calls = new Runnable() {
                public void run() {
                    System.out.println("fetching calls...");
                    getCallDetails(context);
                }
            };

            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.execute(texts);
            executor.execute(calls);

            executor.shutdown();
            while (!executor.isTerminated()) {
            }

            // needs to be immune to interrupt (see last response from below and
            // verify if this is OK practice)
            // http://stackoverflow.com/questions/337903/how-can-you-ensure-in-java-that-a-block-of-code-can-not-be-interrupted-by-any-ot
            new Thread(new Runnable() {
                public void run() {
                    System.out.println("CRITICAL POINT");
                    sqldb.switchTables();
                    System.out.println("DONE CRIT POINT");
                }
            }).start();

            System.out.println("finished populating database");
            new AlertDialog.Builder(context)
                    .setTitle("Update Completed")
                    .setMessage("New data is now available.")
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(false).create().show();

            sqldb.close();
            warning.dismiss();
        }
    }

    // content provider access
    // http://developer.android.com/guide/topics/providers/content-provider-basics.html

    /**
     * existing contacts refresh - get all current contact data in our sqlite
     * database
     */
    private void getContacts(Context context) {

        Cursor cursor = (context.getContentResolver()).query(
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
                Cursor pCur = (context.getContentResolver()).query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
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
    private void getTextDetails(Context context) {
        Cursor cursor = (context.getContentResolver()).query(
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
    private void getCallDetails(Context context) {

        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = (context.getContentResolver()).query(
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
