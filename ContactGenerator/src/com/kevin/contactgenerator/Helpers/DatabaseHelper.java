package com.kevin.contactgenerator.Helpers;

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
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	// Logcat cat
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Table Names
    private static final String TABLE_texts = "texts";
    private static final String TABLE_calls = "calls";
    //the single-column table of all numbers from the texts/calls that aren't already contacts
    //for displaying in the listview -- excludes existing contacts
    private static final String TABLE_noncontacts = "noncontacts";
    //table for existing contacts (just the numbers/names here)
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


    //see below website for handling this sqlite stuff properly
    //http://www.sqlite.org/lang_createtable.html
    
    // Table Create Statements
    // texts table create statement
    private static final String CREATE_TABLE_texts = "CREATE TABLE "
            + TABLE_texts + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER + " TEXT, " 
    		+ TEXT_CONTACT + " TEXT, " 
            + TEXT_DATE + " DATETIME, " 
    		+ TEXT_BODY + " TEXT" 
    		+ ")";

    // calls table create statement
    private static final String CREATE_TABLE_calls = "CREATE TABLE " 
    + TABLE_calls + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER + " TEXT, " 
    		+ CALL_DURATION + " INTEGER, "
            + CALL_TYPE + " TEXT, " 
            + CALL_DATE + " DATETIME" +")";

    // existing contacts table
    private static final String CREATE_TABLE_contacts = "CREATE TABLE " 
    	    + TABLE_contacts + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER + " TEXT UNIQUE, " 
    	            + CONTACT_NAME + " TEXT" +")";
    
    // other table create statements -- ones derived from other tables -- where put?
    // only table all_num afaik -- create this by getting all distinct nums from text/call then 
    // excluding those from contacts table
    private static final String CREATE_TABLE_noncontacts = "CREATE TABLE "
            + TABLE_noncontacts + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER + " TEXT UNIQUE" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //is this bad practice?
        //onCreate(this.getWritableDatabase());
    }
    
    //************************************************************************
    //method to be called in oncreate of mainactivity to dump existing tables;
    //want them to be completely refreshed on startup since pulling system memory
    //BETTER WAY TO HANDLE THIS??? need way to do incrememntal updates
    //************************************************************************
    public void recreateTables() {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	//refresh each re-run of application...
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_texts);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_calls);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_contacts);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_noncontacts);
        
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	// creating required tables
        db.execSQL(CREATE_TABLE_texts);
        db.execSQL(CREATE_TABLE_calls);
        db.execSQL(CREATE_TABLE_contacts);
        db.execSQL(CREATE_TABLE_noncontacts);
    }

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
    
    //http://androidgreeve.blogspot.com/2014/01/android-sqlite-multiple-table-basics.html#.U4IYXqEjvQo
    //http://stackoverflow.com/questions/1556930/sharing-sqlite-database-between-multiple-android-activities
    //http://stackoverflow.com/questions/4234030/android-can-i-use-one-sqliteopenhelper-class-for-multiple-database-files
    //http://stackoverflow.com/questions/6905524/using-singleton-design-pattern-for-sqlitedatabase
    
    //then we have our CRUD operations for each model:
    
    //create (insert) methods
    public void insertNonContact(String number) /*throws Exception*/ { //NEEDS TO BE UNIQUECONSTRAINT EXCEPTION
        SQLiteDatabase db = this.getWritableDatabase();
        //make sure number is not existing contact
        Cursor c = db.rawQuery("SELECT COUNT(NUMBER) as Count" +
                " FROM " + TABLE_contacts 
                + " WHERE NUMBER="
                +"'"+number+"'"+";",
                null);
		int colIndex = c.getColumnIndex("Count");
		int result=0;
		if (c.moveToFirst()) {
			result = c.getInt(colIndex);
		}
		if (result==0) {
	        ContentValues values = new ContentValues();
	        values.put(NUMBER, number);
	        // insert row
	        try{
	        	db.insertOrThrow(TABLE_noncontacts, null, values);
	        } catch (SQLException e) {
	        	//WHAT IS LOG VS SYSTEM.OUT.PRINTLN
	        	Log.e(DATABASE_NAME, e.toString()+" ("+number+")");
	        }
		}
    }
    
    public void insertContacts(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NUMBER, contact.getNumber());
        values.put(CONTACT_NAME, contact.getName());
        // insert row
        try{
        	db.insertOrThrow(TABLE_contacts, null, values);
        } catch (SQLException e) {
        	//WHAT IS LOG VS SYSTEM.OUT.PRINTLN
        	Log.e(DATABASE_NAME, e.toString()+" ("+contact.getNumber()+")");
        }
    }
    
    //can't have date type in the values.put???

    public void insertTexts(TextMsg textmsg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NUMBER, textmsg.getNumber());
        values.put(TEXT_CONTACT, textmsg.getContact());
        values.put(TEXT_DATE, textmsg.getDate());
        values.put(TEXT_BODY, textmsg.getBody());
        // insert row
        db.insert(TABLE_texts, null, values);
        
        //FOR THIS TABLE, anytime one is inserted should also insert to noncontacts
        //since it checks if it should be there (if not a contact) 
        //and has a unique constraint so will dedupe
        //(issue with db probably being open/called both times???)
        insertNonContact(textmsg.getNumber());
        /*
        try{
        	insertNonContact(textmsg.getNumber());
        } catch (Exception e) { 
        	System.out.println("Error inserting"+textmsg.getNumber()); 
        }
        */
    }
    
    public void insertCalls(LoggedCall loggedcall) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NUMBER, loggedcall.getNumber());
        values.put(CALL_TYPE, loggedcall.getType());
        values.put(CALL_DURATION, loggedcall.getDuration());
        values.put(CALL_DATE, loggedcall.getDate());
        // insert row
        db.insert(TABLE_calls, null, values);
        
        //FOR THIS TABLE, anytime one is inserted should also insert to noncontacts
        //since it checks if it should be there (if not a contact) 
        //and has a unique constraint so will dedupe
        //(issue with db probably being open/called both times???)
        insertNonContact(loggedcall.getNumber());
        /*
        try{
        	insertNonContact(loggedcall.getNumber());
        } catch (Exception e) { 
        	System.out.println("Error inserting"+loggedcall.getNumber()); 
        }
        */
    }
    
    //fetch (read) methods
    //will never need to retrieve specific calls/texts etc unless changes happen later...
    //so for now just grab everything from each table based on number and return arraylist 
    //however, also need way to grab ALL from each table, and possibly by date etc later on
    //also by NAME for existing contacts if have multiple nums (later on...)
    
    //calls
    public ArrayList<LoggedCall> fetchCalls(String number) {
    	 SQLiteDatabase db = this.getWritableDatabase();
         Cursor c = db.rawQuery("SELECT number, type, date, duration " +
                 " FROM " + TABLE_calls   
                 + " WHERE number=" 
                 + "'" + number + "'" + ";",
                 null);
 		/* Get the indices of the columns we will need */
 		int numberColumn = c.getColumnIndex("number");
 		int typeColumn = c.getColumnIndex("type");
 		int dateColumn = c.getColumnIndex("date");   
 		int durationColumn = c.getColumnIndex("duration");
 		ArrayList<LoggedCall> callsArray = new ArrayList<LoggedCall>();
 		
 		if (c.moveToFirst()) {
 		do {
 		LoggedCall record = new LoggedCall(c.getString(numberColumn),
 		c.getString(typeColumn),
 		c.getString(dateColumn),
 		c.getInt(durationColumn)
 		);
 		callsArray.add(record);
 		}
 		while (c.moveToNext());
 		}
 		c.close();
 		return callsArray;
    }
    public ArrayList<LoggedCall> fetchAllCalls() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT number, type, date, duration " +
                " FROM " + TABLE_calls + ";",   
                null);
		/* Get the indices of the columns we will need */
		int numberColumn = c.getColumnIndex("number");
		int typeColumn = c.getColumnIndex("type");
		int dateColumn = c.getColumnIndex("date");   
		int durationColumn = c.getColumnIndex("duration");
		ArrayList<LoggedCall> callsArray = new ArrayList<LoggedCall>();
		
		if (c.moveToFirst()) {
		int i = 1;
		do {
			LoggedCall record = new LoggedCall(c.getString(numberColumn),
			c.getString(typeColumn),
			c.getString(dateColumn),
			c.getInt(durationColumn)
		);
		callsArray.add(record);
		}
		while (c.moveToNext());
		}
		c.close();
		return callsArray;
    }
    
    //texts
    public ArrayList<TextMsg> fetchTexts(String number) {
   	 SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT number, contact, date, body " +
                " FROM " + TABLE_texts   
                + " WHERE number=" 
                + "'" + number + "'" + ";",
                null);
		/* Get the indices of the columns we will need */
		int numberColumn = c.getColumnIndex("number");
		int contactColumn = c.getColumnIndex("contact");
		int dateColumn = c.getColumnIndex("date");   
		int bodyColumn = c.getColumnIndex("body");
		ArrayList<TextMsg> textArray = new ArrayList<TextMsg>();
		
		if (c.moveToFirst()) {
		do {
			TextMsg record = new TextMsg(c.getString(numberColumn),
			c.getString(contactColumn),
			c.getString(dateColumn),
			c.getString(bodyColumn)
		);
		textArray.add(record);
		}
		while (c.moveToNext());
		}
		c.close();
		return textArray;
   }
   public ArrayList<TextMsg> fetchAllTexts() {
       SQLiteDatabase db = this.getWritableDatabase();
       Cursor c = db.rawQuery("SELECT number, type, date, duration " +
               " FROM " + TABLE_texts + ";",   
               //+ " ORDER BY date;",
               null);
       /* Get the indices of the columns we will need */
		int numberColumn = c.getColumnIndex("number");
		int contactColumn = c.getColumnIndex("contact");
		int dateColumn = c.getColumnIndex("date");   
		int bodyColumn = c.getColumnIndex("body");
		ArrayList<TextMsg> textArray = new ArrayList<TextMsg>();
		
		if (c.moveToFirst()) {
		do {
			TextMsg record = new TextMsg(c.getString(numberColumn),
			c.getString(contactColumn),
			c.getString(dateColumn),
			c.getString(bodyColumn)
		);
		textArray.add(record);
		}
		while (c.moveToNext());
		}
		c.close();
		return textArray;
   }
   
   public ArrayList<Contact> fetchAllContacts() {
       SQLiteDatabase db = this.getWritableDatabase();
       Cursor c = db.rawQuery("SELECT name, number " +
               " FROM " + TABLE_contacts + ";",   
               //+ " ORDER BY date;",
               null);
       /* Get the indices of the columns we will need */
		int nameColumn = c.getColumnIndex("name");
		int numberColumn = c.getColumnIndex("number");
		ArrayList<Contact> contactArray = new ArrayList<Contact>();
		
		if (c.moveToFirst()) {
		do {
			Contact record = new Contact(c.getString(nameColumn),
			c.getString(numberColumn)
		);
		contactArray.add(record);
		}
		while (c.moveToNext());
		}
		c.close();
		return contactArray;
   }
   
   public ArrayList<String> fetchAllNonContacts() {
	   SQLiteDatabase db = this.getWritableDatabase();
       Cursor c = db.rawQuery("SELECT number " +
               " FROM " + TABLE_noncontacts + ";",   
               //+ " ORDER BY date;",
               null);
       /* Get the indices of the columns we will need */
		int numberColumn = c.getColumnIndex("number");
		ArrayList<String> noncontactArray = new ArrayList<String>();
		
		if (c.moveToFirst()) {
		do {
			String noncontactNumber = c.getString(numberColumn);
			noncontactArray.add(noncontactNumber);
		}
		while (c.moveToNext());
		}
		c.close();
		return noncontactArray;
   }
   
    //probably no need to update since records are static...
   //TODO -- future will possibly need way to update existing contacts
    
    //delete methods: only needed for noncontacts... possibly need for existing contacts if debugging issues.
   public void deleteNonContact(String number) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_noncontacts, NUMBER + " = "+"'"+number+"'",
	           null);
	}
   
   //then our database close() method
   //http://stackoverflow.com/questions/4557154/android-sqlite-db-when-to-close
   public void closeDB() {
       SQLiteDatabase db = this.getReadableDatabase();
       if (db != null && db.isOpen())
           db.close();
   }
   
   //re-opening after closing (verify this is right!!!)
   //it is opened when getWritableDB or getReadableDB called...
   //so i can close as leave activity and it will be re-opened if necessary by other methods in the activity

}
