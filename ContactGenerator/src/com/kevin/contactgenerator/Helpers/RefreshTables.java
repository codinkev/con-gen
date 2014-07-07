package com.kevin.contactgenerator.Helpers;

import java.sql.Date;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import java.util.ArrayList;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.view.Menu;

public class RefreshTables {
	//TODO: needs the methods for accessing content providers etc
	//first make the existing contacts db
	//then make the calls/text one, which has all info
	//then make a table allnums which has all numbers (distinct from call/text log) aside from existing contacts
	//can edit existing contacts and see their info [plain #s etc] on the view existing contacts activity listivew...	
	
	//since this method is called on startup but also after each insertion, it needs to totally
	//reset the database (erase it etc)
	//(after insert only need to remove number from allnums, so no need for reset -- just need to start
	// fresh every restart of app to catch all new texts/calls...)
	
	//THESE METHODS NEED A CONTEXT -- so for now, since they are only called on activity startup
	//as it is, (until we allow new stuff received etc to be loaded using a broadcast receiver?)
	//just have the methods in the MainActivity
}
