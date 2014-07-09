package com.kevin.contactgenerator.Utilities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.kevin.contactgenerator.Models.Contact;
import com.kevin.contactgenerator.Models.LoggedCall;
import com.kevin.contactgenerator.Models.TextMsg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CallLog;
import android.util.Log;

/**
 * Collection of helper methods for accessing our sqlite database
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // singleton implementation
    // http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
    private static DatabaseHelper dbInstance;

    // General database details
    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";

    // Table Names
    private static final String TABLE_texts = "texts";
    private static final String TABLE_calls = "calls";
    private static final String TABLE_noncontacts = "noncontacts";
    private static final String TABLE_contacts = "contacts";

    // Common column names
    private static final String NUMBER = "number";

    // calls Table - column names
    private static final String CALL_DURATION = "duration";
    private static final String CALL_TYPE = "type";
    private static final String CALL_DATE = "date";

    // texts Table - column names
    private static final String TEXT_CONTACT = "contact";
    private static final String TEXT_DATE = "date";
    private static final String TEXT_BODY = "body";

    // contacts table - columns
    private static final String CONTACT_NAME = "name";

    // Table Create Statements

    // texts table create statement
    private static final String CREATE_TABLE_texts = "CREATE TABLE "
            + TABLE_texts + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER
            + " TEXT, " + TEXT_CONTACT + " TEXT, " + TEXT_DATE + " DATETIME, "
            + TEXT_BODY + " TEXT" + ")";

    // calls table create statement
    private static final String CREATE_TABLE_calls = "CREATE TABLE "
            + TABLE_calls + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER
            + " TEXT, " + CALL_DURATION + " INTEGER, " + CALL_TYPE + " TEXT, "
            + CALL_DATE + " DATETIME" + ")";

    // existing contacts table
    private static final String CREATE_TABLE_contacts = "CREATE TABLE "
            + TABLE_contacts + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NUMBER + " TEXT UNIQUE, " + CONTACT_NAME + " TEXT" + ")";

    private static final String CREATE_TABLE_noncontacts = "CREATE TABLE "
            + TABLE_noncontacts + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NUMBER + " TEXT UNIQUE" + ")";

    /**
     * Prevent multiple instances of the database across activity lifecycle --
     * call this in lieu of constructor directly
     * 
     * @param context
     *            The current state of the application
     * @return DatabaseHelper instance
     */
    public static DatabaseHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    /**
     * Constructor -- private to prevent using outside of the singleton fetcher
     * 
     * @param context
     *            The current state of the application
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TEMP COMMENTED
        // onCreate(this.getWritableDatabase());
    }

    /**
     * method to be called in update to dump existing tables; want them to be
     * completely refreshed on startup since pulling system memory
     */
    public void recreateTables() {
        SQLiteDatabase db = this.getWritableDatabase();

        // refresh each re-run of application...
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_texts);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_calls);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_contacts);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_noncontacts);

        onCreate(db);
    }

    /**
     * executes create table statements on the database instance
     * 
     * @param SQLiteDatabase
     *            instance of sqlitedatabase associated with our helper instance
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_texts);
        db.execSQL(CREATE_TABLE_calls);
        db.execSQL(CREATE_TABLE_contacts);
        db.execSQL(CREATE_TABLE_noncontacts);
    }

    /**
     * drops and recreates database instance tables for upgrade
     * 
     * @param SQLiteDatabase
     *            instance of sqlitedatabase associated with our helper instance
     * @param oldVersion
     *            the current version of database in use
     * @param newVersion
     *            the version being upgraded to
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_texts);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_calls);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_contacts);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_noncontacts);

        // create new tables
        onCreate(db);
    }

    // then we have our CRUD operations for each model:
    // create (insert) methods

    /**
     * create a "noncontact": a number which has data on the phone but is not
     * associated with an existing contact this method is ONLY called any time a
     * text or call is inserted the number is checked against existing contacts
     * before insertion
     * 
     * @param Number
     *            the number associated with text or call being inserted
     * @throws UniqueConstraintException
     *             thrown/caught when we try to insert a noncontact number that
     *             already exists
     */
    public void insertNonContact(String number) /* throws Exception */{
        SQLiteDatabase db = this.getWritableDatabase();
        // make sure number is not existing contact
        Cursor c = db.rawQuery("SELECT COUNT(NUMBER) as Count" + " FROM "
                + TABLE_contacts + " WHERE NUMBER=" + "'" + number + "'" + ";",
                null);
        int colIndex = c.getColumnIndex("Count");
        int result = 0;
        if (c.moveToFirst()) {
            result = c.getInt(colIndex);
        }
        if (result == 0) {
            ContentValues values = new ContentValues();
            values.put(NUMBER, number);
            // insert row
            try { // may throw a unique constraint exception
                db.insertOrThrow(TABLE_noncontacts, null, values);
            } catch (SQLException e) {
                Log.e(DATABASE_NAME, e.toString() + " (" + number + ")");
            }
        }
    }

    /**
     * insert a contact based on accessing the ContactsContract content provider
     * 
     * @param contact
     *            the contact object fetched from the content provider to insert
     */
    public void insertContacts(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NUMBER, contact.getNumber());
        values.put(CONTACT_NAME, contact.getName());
        // insert row
        try {
            db.insertOrThrow(TABLE_contacts, null, values);
        } catch (SQLException e) {
            Log.e(DATABASE_NAME, e.toString() + " (" + contact.getNumber()
                    + ")");
        }
    }

    /**
     * insert a text msg based on accessing the content://sms/inbox content
     * provider
     * 
     * @param textmsg
     *            the textmsg object fetched from the content provider to insert
     */
    public void insertTexts(TextMsg textmsg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NUMBER, textmsg.getNumber());
        values.put(TEXT_CONTACT, textmsg.getContact());
        values.put(TEXT_DATE, textmsg.getDate());
        values.put(TEXT_BODY, textmsg.getBody());
        // insert row
        db.insert(TABLE_texts, null, values);

        // FOR THIS TABLE, any time one is inserted should also insert to
        // noncontacts
        // since it checks if it should be there (if not a contact)
        // and has a unique constraint so will de-dupe
        insertNonContact(textmsg.getNumber());
    }

    /**
     * insert a call based on accessing the CallLog.Calls content provider
     * 
     * @param loggedcall
     *            the loggedcall object fetched from the content provider to
     *            insert
     */
    public void insertCalls(LoggedCall loggedcall) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NUMBER, loggedcall.getNumber());
        values.put(CALL_TYPE, loggedcall.getType());
        values.put(CALL_DURATION, loggedcall.getDuration());
        values.put(CALL_DATE, loggedcall.getDate());
        // insert row
        db.insert(TABLE_calls, null, values);

        // FOR THIS TABLE, any time one is inserted should also insert to
        // noncontacts
        // since it checks if it should be there (if not a contact)
        // and has a unique constraint so will de-dupe
        insertNonContact(loggedcall.getNumber());
    }

    // read methods

    /**
     * fetch all call data for a particular number from phone
     * 
     * @param number
     *            the number we want call information about
     * @return ArrayList<LoggedCall> list of all calls associated with the
     *         number on phone
     */
    public ArrayList<LoggedCall> fetchCalls(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT number, type, date, duration "
                + " FROM " + TABLE_calls + " WHERE number=" + "'" + number
                + "'" + ";", null);

        int numberColumn = c.getColumnIndex("number");
        int typeColumn = c.getColumnIndex("type");
        int dateColumn = c.getColumnIndex("date");
        int durationColumn = c.getColumnIndex("duration");
        ArrayList<LoggedCall> callsArray = new ArrayList<LoggedCall>();

        if (c.moveToFirst()) {
            do {
                LoggedCall record = new LoggedCall(c.getString(numberColumn),
                        c.getString(typeColumn), c.getString(dateColumn),
                        c.getInt(durationColumn));
                callsArray.add(record);
            } while (c.moveToNext());
        }
        c.close();
        return callsArray;
    }

    /**
     * fetch all call data for all numbers on phone
     * 
     * @return ArrayList<LoggedCall> list of all calls associated with all
     *         numbers on phone
     */
    public ArrayList<LoggedCall> fetchAllCalls() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT number, type, date, duration "
                + " FROM " + TABLE_calls + ";", null);

        int numberColumn = c.getColumnIndex("number");
        int typeColumn = c.getColumnIndex("type");
        int dateColumn = c.getColumnIndex("date");
        int durationColumn = c.getColumnIndex("duration");
        ArrayList<LoggedCall> callsArray = new ArrayList<LoggedCall>();

        if (c.moveToFirst()) {
            int i = 1;
            do {
                LoggedCall record = new LoggedCall(c.getString(numberColumn),
                        c.getString(typeColumn), c.getString(dateColumn),
                        c.getInt(durationColumn));
                callsArray.add(record);
            } while (c.moveToNext());
        }
        c.close();
        return callsArray;
    }

    /**
     * fetch all text data for a particular number from phone
     * 
     * @param number
     *            the number we want text information about
     * @return ArrayList<TextMsg> list of all texts associated with the number
     *         on phone
     */
    public ArrayList<TextMsg> fetchTexts(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT number, contact, date, body " + " FROM "
                + TABLE_texts + " WHERE number=" + "'" + number + "'" + ";",
                null);

        int numberColumn = c.getColumnIndex("number");
        int contactColumn = c.getColumnIndex("contact");
        int dateColumn = c.getColumnIndex("date");
        int bodyColumn = c.getColumnIndex("body");
        ArrayList<TextMsg> textArray = new ArrayList<TextMsg>();

        if (c.moveToFirst()) {
            do {
                TextMsg record = new TextMsg(c.getString(numberColumn),
                        c.getString(contactColumn), c.getString(dateColumn),
                        c.getString(bodyColumn));
                textArray.add(record);
            } while (c.moveToNext());
        }
        c.close();
        return textArray;
    }

    /**
     * fetch all text data for all numbers on phone
     * 
     * @return ArrayList<TextMsg> list of all texts associated with all numbers
     *         on phone
     */
    public ArrayList<TextMsg> fetchAllTexts() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT number, type, date, duration "
                + " FROM " + TABLE_texts + ";",
        // + " ORDER BY date;",
                null);

        int numberColumn = c.getColumnIndex("number");
        int contactColumn = c.getColumnIndex("contact");
        int dateColumn = c.getColumnIndex("date");
        int bodyColumn = c.getColumnIndex("body");
        ArrayList<TextMsg> textArray = new ArrayList<TextMsg>();

        if (c.moveToFirst()) {
            do {
                TextMsg record = new TextMsg(c.getString(numberColumn),
                        c.getString(contactColumn), c.getString(dateColumn),
                        c.getString(bodyColumn));
                textArray.add(record);
            } while (c.moveToNext());
        }
        c.close();
        return textArray;
    }

    /**
     * fetch all contacts stored in our database
     * 
     * @return ArrayList<Contact> list of all contacts in the database
     */
    public ArrayList<Contact> fetchAllContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT name, number " + " FROM "
                + TABLE_contacts + ";",
        // + " ORDER BY date;",
                null);

        int nameColumn = c.getColumnIndex("name");
        int numberColumn = c.getColumnIndex("number");
        ArrayList<Contact> contactArray = new ArrayList<Contact>();

        if (c.moveToFirst()) {
            do {
                Contact record = new Contact(c.getString(nameColumn),
                        c.getString(numberColumn));
                contactArray.add(record);
            } while (c.moveToNext());
        }
        c.close();
        return contactArray;
    }

    /**
     * fetch all noncontacts stored in our database ...where a noncontact is a
     * number with text/call on phone but not currently a contact
     * 
     * @return ArrayList<String> list of all noncontact numbers in the database
     */
    public ArrayList<String> fetchAllNonContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT number " + " FROM " + TABLE_noncontacts
                + ";",
        // + " ORDER BY date;",
                null);

        int numberColumn = c.getColumnIndex("number");
        ArrayList<String> noncontactArray = new ArrayList<String>();

        if (c.moveToFirst()) {
            do {
                String noncontactNumber = c.getString(numberColumn);
                noncontactArray.add(noncontactNumber);
            } while (c.moveToNext());
        }
        c.close();
        return noncontactArray;
    }

    /**
     * once a noncontact is added as a contact, we remove it from the noncon
     * table since it is no longer a noncon
     * 
     * @param number
     *            the number we want to remove as a noncon
     */
    public void deleteNonContact(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_noncontacts, NUMBER + " = " + "'" + number + "'", null);
    }

    // http://stackoverflow.com/questions/4557154/android-sqlite-db-when-to-close
    /**
     * close the sqlitedatabase when we are done using to avoid memory leaks
     */
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
