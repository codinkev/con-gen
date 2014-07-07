package com.kevin.contactgenerator.Activities;

import java.util.ArrayList;

import com.example.contactgenerator.R;
import com.kevin.contactgenerator.Helpers.DatabaseHelper;
import com.kevin.contactgenerator.Models.CustomAdapter;
import com.kevin.contactgenerator.Models.LoggedCall;
import com.kevin.contactgenerator.Models.TextMsg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NonConExplorer extends ListActivity {
	
	//menu and nav drawer are in near future... 
	//rather than get caught up on that first get the contact adding functionality
	//then explore improvements...
	
	
	//http://developer.android.com/guide/topics/ui/menus.html
	//^ read this for menu help. not for navigating; its for user actions.
	//have different types of menus depending if its an action affecting whole app or just the activity etc
	//so add contact and see texts etc rather than LHS buttons here should prob. be menu
	
	
	//for navigation: what is navigation drawer
	//http://www.reddit.com/r/Android/comments/26dqft/possible_systemwide_horizontal_edge_swipe_gesture/
	//START ACTUALLY DOWNLOADING AND USING NEW APPS TO LEARN WHAT THEY ARE DOING TO IMPROVE MY OWN
	//http://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/
	
	
	//http://code.tutsplus.com/tutorials/android-essentials-creating-simple-user-forms--mobile-1758
	//next step is read above link and create a button for adding the contact; 
	//clicking button has 2 fields (for now): name and number
	//gives the number you clicked on by default but you can edit it too
	//rather than start new activity, have something just pop up there? option??? try below link
	//http://stackoverflow.com/questions/9699743/how-can-i-create-a-text-entry-dialog-in-android
	
	//HOW FIX XML FILES TO HAVE @STRING RESOUCE STORED SOMEWHERE -- WHERE STORE	
	DatabaseHelper sqldb;
	String number;
	
	//ListView info;
	ArrayAdapter<TextMsg> textAdapter;
	ArrayAdapter<LoggedCall> callAdapter;
	ArrayAdapter<String> addConAdapter;
	
	ArrayList<TextMsg> texts;
	ArrayList<LoggedCall> calls;
	//dont need now? remove.
	String[] addcon;
	
	Button seeTexts;
	Button seeCalls;
	//only pops up on add contact action to create the new contact
	Button insert_db;
	EditText enter_name;
	EditText number_entry;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noncon_exp);
		
		//RECEIVE NUMBER HERE AS BUNDLE FROM NONCON FOR POPULATING LISTVIEW ON RHS
		//OF A HORIZONTAL LAYOUT...
		sqldb = new DatabaseHelper(getApplicationContext());
		
		//receiving the number that was clicked on noncon...
		Intent intent = getIntent();
		number = intent.getExtras().getString("number");
		
		texts = sqldb.fetchTexts(number);
		calls = sqldb.fetchCalls(number);
		addcon = getResources().getStringArray(R.array.addConArray);
  		
		getListView().setVerticalScrollBarEnabled(true);
		getListView().setClickable(true);
		getListView().setItemsCanFocus(false);
    
        textAdapter = new CustomAdapter<TextMsg>(NonConExplorer.this, 
        		android.R.layout.simple_list_item_1, texts, "TEXTS");
        callAdapter = new CustomAdapter<LoggedCall>(NonConExplorer.this, 
        		android.R.layout.simple_list_item_1, calls, "CALLS");
        //addConAdapter = new CustomAdapter<String>(NonConExplorer.this, 
        //		android.R.layout.simple_list_item_1, addcon, "ADDCON");
        
        addConAdapter = new CustomAdapter<String>(NonConExplorer.this, 
        		R.layout.edittext_list_item, "ADDCON");
       
        
        setListAdapter(textAdapter);
        
        enter_name = (EditText)findViewById(R.id.enter_name);
        enter_name.setVisibility(View.GONE);
        number_entry = (EditText)findViewById(R.id.number_entry);
        number_entry.setVisibility(View.GONE);
        
        ActionBar actionBar = getActionBar();
        actionBar.show();
        
        Toast.makeText(this, 
				"Currently showing texts for this contact; " +
				"choose an from the actionbar for other options"
				, Toast.LENGTH_SHORT)
        .show();
        
        insert_db = (Button) findViewById(R.id.insert_db);
        insert_db.setBackgroundColor(Color.RED);
		insert_db.setTextColor(Color.BLACK);
		//don't make visible until we are on the addcon listadapter
        insert_db.setVisibility(View.GONE);
        insert_db.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
				//CALL INSERT CONTACT METHOD HERE
				//AFTER THAT, HAVE THIS DELETE THE THING FROM NONCONS ETC RATHER THAN REFRESH EVERYTHING ETC
				//TAKE US BACK TO PRIOR ACTIVITY AND GIVE A SUCCESS/FAILURE MSG... ETC
				
				//can extract the fields for creating the contact model using the xml id in the layout of the edittext :)
				//System.out.println(texts);
				//wrapper around addContact
				String name = ((EditText)findViewById(R.id.enter_name)).getText().toString();
				String number_field = ((EditText)findViewById(R.id.number_entry)).getText().toString();
				if (name!=null && number_field!=null) {
					addContact(name,number_field);
					Toast.makeText(NonConExplorer.this, "Contact Inserted!", Toast.LENGTH_SHORT).show();
					finish();
					
					//refresh THESE DATABASES WITH JUST TEH NEW CON AND REMOVING THE NONCON
					//when re-update all, will do the actual longer content provider part...
					
				} else {
					Toast.makeText(NonConExplorer.this, "NO NULLS ALLOWED", Toast.LENGTH_SHORT).show();
				}
				
				
			}
       });
        
        /*
        seeTexts = (Button) findViewById(R.id.texts);
		seeTexts.setBackgroundColor(Color.TRANSPARENT);
		seeTexts.setTextColor(Color.BLACK);
		seeTexts.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Toast.makeText(NonConExplorer.this, "Showing texts for "+number, Toast.LENGTH_SHORT).show();
				info.setAdapter(textAdapter);
				//System.out.println(texts);
			}
       });
       */
        
        /*
		seeCalls = (Button) findViewById(R.id.calls);
		seeCalls.setBackgroundColor(Color.TRANSPARENT);
		seeCalls.setTextColor(Color.BLACK);
		seeCalls.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Toast.makeText(NonConExplorer.this, "Showing calls for "+number, Toast.LENGTH_SHORT).show();
				info.setAdapter(callAdapter);
			}
       });
       */
        	
		//TODO -- MAKE IT SO YOU CAN ACTUALLY CLICK ON THE THINGS IN THE LV 
		//TO GET MORE INFO ON THEM...
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            //what actually is adapterview, view, etc... in this context
			public void onItemClick(AdapterView a, View v, int position, long id) {
            		System.out.println("an item has been clicked");
            		//when clicking this have it generate an alert dialog which
            		//gives full listing of all the attributes of that call/text
            		
            		//investigate that alert dialog with its own contentview etc more
            		//not for this part, but i mean in general wtf was that?
            		
            		//need to be able to tell which adapter is currently in use
            		//find better way to do this ... bad to use same listview???
            		//identifier for which adapter being used
            		final String identifier=getListAdapter().toString();
            		
            		//position of the clicked item
            		final int field = position;
            		//dealing with the clicked item
            		final String cellcontents = a.getItemAtPosition(field).toString();
            		
            		if (identifier=="ADDCON") {
            			System.out.println("Adding contact time");
            			//fields that correspond to the position clicked
            			final int NAME=0;
            			final int NUMBER=1;
            			
            			//keeping what's originally there so we don't append to appends (enables editing of responses)
            			final String entry_field=getResources().getStringArray(R.array.addConArray)[field];
            			
                		AlertDialog.Builder alert = new AlertDialog.Builder(NonConExplorer.this);            		 
                		alert.setTitle("New Contact - "+entry_field);  
                		//this arraylist is holding messages such as "enter name here"
                        alert.setMessage("Enter "+entry_field+":");  
               
                         // Set an EditText view to get user input 
                         final EditText addInput = new EditText(NonConExplorer.this);  
                         alert.setView(addInput); 
                         
                         alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	                       	 @Override
	                       	 public void onClick(DialogInterface dialog, int whichButton) {
	                       		 //STILL NEED: HOW SAVE EACH INDIVIDUAL COMPONENT OF THE THING BEING ADDED
	                       		 //CHECK TO ENSURE NO NULLS ENTERED ETC... (KIND OF LIKE I DID ON OLD PROJ)
	                       		 //clean up this part in general and also how seeing which adapter being used
	                       		 //**should i just have separate listview deployed depending on actionbar click???
	                       		 //**USING GLOBAL VS FINAL VARIABLES IN ANDROID (OR APP. CONTEXT VARS???) bad to have a bunch listed at top like I have???
	                       		 addcon[field]=entry_field+": "+addInput.getText().toString();
	                       		 switch (field) {
	                       		 case (NAME): 
	                       			 //con_name=addInput.getText().toString();
	                       		 case (NUMBER):
	                       			 //con_number=addInput.getText().toString();
	                 
	                       		 }
	                       		 /*
	                       		 switch (addIndex) {                   		 
	                       		 case 0: //size entry            			
	                       			 if (addInput.getText().toString()!=null){                   				 
	                       				try{	 
	                       					size = Double.parseDouble(addInput.getText().toString());
	                       					addArrayList.set(addIndex, "Size (Double) currently entered as: " + size.toString());
	                       				}catch(Exception e) {   
	                       					AlertDialog.Builder adb =new AlertDialog.Builder(activityInstance);
	                       					adb.setTitle("Incorrect input"); 
	                                        adb.setMessage("Enter a numeric (double-precision) value");                                  
	                                        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
	    										@Override
	    										public void onClick(DialogInterface arg0, int arg1) {													
	    										}
	    										});
	                                        adb.show();                                    
	                       				}                			                    			 
	                       			 }                   			                   		 		                   		                           		                   		
	                       		 break; 
	                       		 */
	                       		 
	                       		 //make updates after user input given
	                       		 addConAdapter = new ArrayAdapter<String>(NonConExplorer.this,
	    	    					        android.R.layout.simple_list_item_1, addcon);
	                       		 setListAdapter(addConAdapter);	                   	                  	
	                       		 addConAdapter.notifyDataSetChanged();	                   		
	                                    		
	                       	 }
                         });                      
                         alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
                           @Override
    					public void onClick(DialogInterface dialog, int whichButton) {  
                        	   //do nothing
                           }  
                        });
                    
                        //alert.show();
            		}
                    
            		//how to iterate over all attributes of a given object without knowing type etc
            		else if (identifier=="TEXTS") {
            			System.out.println("Text");
            		}
            		else if (identifier=="CALLS") {
            			System.out.println("Call");
            		} else {
            			System.out.println("None of the above -- error");
            		}
            		
            }
        });
       

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.newcon, menu);
		return true;
	}
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // action with ID action_refresh was selected
	    case R.id.action_addcontact:
	        Toast.makeText(this, "Adding contact...", Toast.LENGTH_SHORT)
	           .show();
	        //setListAdapter(addConAdapter);
	        //do i need to repeat this find view here thing every time?
	        enter_name = (EditText)findViewById(R.id.enter_name);
	        enter_name.setVisibility(View.VISIBLE);
	        number_entry = (EditText)findViewById(R.id.number_entry);
	        number_entry.setVisibility(View.VISIBLE);
	        
	        number_entry.setText(number);
	        
	        insert_db = (Button) findViewById(R.id.insert_db);
	        insert_db.setVisibility(View.VISIBLE);
	        getListView().setVisibility(View.GONE);
	        
	        //new listview adapter with fields for adding the guy
	        //uses addcontact method below...
	        //better way to use content provider??? vogella
	        break;
	    // action with ID action_settings was selected
	    case R.id.action_seetexts:
	    	Toast.makeText(NonConExplorer.this, "Showing texts for "+number, Toast.LENGTH_SHORT)
	    	   .show();
			setListAdapter(textAdapter);
			//don't need the functionality of this button for this view
			insert_db = (Button) findViewById(R.id.insert_db);
			insert_db.setVisibility(View.GONE);
			getListView().setVisibility(View.VISIBLE);
			enter_name = (EditText)findViewById(R.id.enter_name);
	        enter_name.setVisibility(View.GONE);
	        number_entry = (EditText)findViewById(R.id.number_entry);
	        number_entry.setVisibility(View.GONE);
	        break;
	    case R.id.action_seecalls:
			Toast.makeText(NonConExplorer.this, "Showing calls for "+number, Toast.LENGTH_SHORT)
			   .show();
			setListAdapter(callAdapter);
			//don't need the functionality of this button for this view
			//starting to get spaghetti-ish -- make this going visible / invisible stuff its own method??
			insert_db = (Button) findViewById(R.id.insert_db);
			insert_db.setVisibility(View.GONE);
			getListView().setVisibility(View.VISIBLE);
			enter_name = (EditText)findViewById(R.id.enter_name);
	        enter_name.setVisibility(View.GONE);
	        number_entry = (EditText)findViewById(R.id.number_entry);
	        number_entry.setVisibility(View.GONE);
		    break;
	    default:
	      break;
	    }
	    return true;
	  } 
	
	
	
	//this method will eventually be part of the activity where i am investigating new contacts to add
	//NOT used for populating db...
	private void addContact(String name, String number_field) {
		//this works for adding contact ... make into function etc...
				ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
				int rawContactInsertIndex = ops.size();

				ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				   .withValue(RawContacts.ACCOUNT_TYPE, null)
				   .withValue(RawContacts.ACCOUNT_NAME,null )
				   .build());
				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				   .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
				   .withValue(Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE)
				   .withValue(Phone.NUMBER, number_field)
				   .build());
				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				   .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
				   .withValue(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE)
				   .withValue(StructuredName.DISPLAY_NAME, name)
				   .build());  
				try {
					ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//TODO -- need to also ensure when added as contact, inserted to contacts sqlite table as well
	}
	
	//we only want the ActionBar visible to this activity
	//is this bad practice???
	public void onPause(){
		super.onPause();
		getActionBar().hide();
	}
	public void onResume(){
		super.onResume();
		getActionBar().show();
	}

}
