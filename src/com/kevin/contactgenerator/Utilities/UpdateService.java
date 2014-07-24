package com.kevin.contactgenerator.Utilities;

import java.sql.Date;

import com.kevin.contactgenerator.Activities.MainActivity;
import com.kevin.contactgenerator.Models.Contact;
import com.kevin.contactgenerator.Models.LoggedCall;
import com.kevin.contactgenerator.Models.TextMsg;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;


/**
 * 
 * @author kevin
 * 
 * Service for updating database/accessing content providers.  
 * all methods provided here
 */
public class UpdateService extends IntentService {
 //http://www.vogella.com/tutorials/AndroidServices/article.html#service_overview
 //http://stackoverflow.com/questions/15524280/service-vs-intent-service
 //http://stackoverflow.com/questions/19849963/how-can-i-operate-sqlite-when-i-use-intentservice
 //http://stackoverflow.com/questions/15755785/android-java-intentservice-onhandleintent-does-not-get-called
    
    DatabaseHelper sqldb;
    
    public void onCreate() {
        super.onCreate();
    }

    public UpdateService() {
        super("UpdateService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        // start the update
        System.out.println("Intent: "+intent.toString());
        System.out.println("Initiating update in the service...");
        
        sqldb = DatabaseHelper.getInstance(UpdateService.this);
        // in case last backup didn't finish
        sqldb.recreateBackups();
        
        // we can't have contacts run asynchronously because noncons depends
        // on it
        getContacts(UpdateService.this);
        
        // pool these together? performance gain? alternatives?
        getTextDetails(UpdateService.this);
        getCallDetails(UpdateService.this);
        
        //after those are done we can call this...
        sqldb.generateNonContacts();
        
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
        sqldb.close();
     
        System.out.println("Done!");
        // should this intent and the one in onpause of main be different
        // (probably)? research
        
        Intent main_intent = new Intent("com.kevin.contactgenerator.Utilities");
        String message = "Testing";
        main_intent.putExtra("message", message);
        sendBroadcast(main_intent);
    }
    
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