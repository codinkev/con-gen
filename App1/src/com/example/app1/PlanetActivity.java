/**
* Kevin Keitel, Connor Burke, Zach Pearson; CISC181-012 
* 5/19/2013, Project 
* 
* Primary activity; contains all layouts and their functionalities 
* (fully described in the README in src)
*/

package com.example.app1;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class PlanetActivity extends Activity {

	//enumerations for changing between layouts
	public enum Change {ADD, SORT, GRAPH, REVERT, VIEW, SEARCH};
	
	//declarations for buttons, editText, & image on main screen
	private Button createItem;
	private Button search;
	private Button DELETE_LIST;
	private EditText searchInput;
	private Button graphButton;
	private ImageView image;
	private Button incrementalSearch;
	//
	
	//gives the planet clicked on main list for displaying attributes
	Planet selectedPlanet;
	
	//global holders for create method (used in addLayout)
	private Double size;
	private Boolean rings;
	private Integer moons;
	private String name;
	private String solarSystem;
	private Double averageTemperature;
	private String primaryCompositionElement;
	private Double distanceFromSun;
	////
	
	//used for the create item method as well (shown in addLayout)
	private ArrayList<String> addArrayList;
	private EditText addInput;
	private ArrayAdapter<String> addArrayAdapter;
	private TextView attributeDisplay;
	private Button revert;
	//
	
	private LinearLayout mainLayout;
	private LinearLayout addLayout;
	private LinearLayout viewLayout;
	private LinearLayout sortLayout;
	private LinearLayout searchLayout;
	
	//used for bar charts
	private RelativeLayout horiLay;
	private RelativeLayout graphLayout;
	private LinearLayout graphs;
	//

	//primary arrayList for planet data
	//make it static so that we can always claim it for graphing and finding max values in view 
	//(and there is only one main list at any given time)
	static ArrayList<Planet> mainListArray = new ArrayList<Planet>();
	//adapter that uses main list array
	private ArrayAdapter<Planet> mainAdapter;
	
	//listViews used
	private ListView mainList;
	private ListView addListView; 
	private ListView sortListView;
    
	//textview for displaying particular attribute across list
	//(depending on how many bars there are it becomes impratical to label each one)
	TextView graphDisplay;	
	
	//name taken from editText to search list
	String searchName;
	
	// indices used for different on-click listeners
	int listIndex;
	int addIndex;
	int sortIndex;
	//
	
	//buttons and lists used on the sort screen
	ArrayList<String> sortArrayList;
	ArrayAdapter<String> sortAdapter;
	private Button sort;
	private Button revertFromSort;
	private Button revertFromGraph;
	//	
	
	Activity activityInstance = this;
	//height and width of display
	int height;
	int width;
	
	//used for graphing to pass to view (PlanetGraph)
	//static because we are only graphing one thing at once and want to pass 
	//this information to the PlanetGraphView 
	static ArrayList<Planet> graphList;
	static String graphAttribute; //use for deciding what attribute across the entire list is being displayed
	PlanetGraphView planetGraphInstance;
	TextView chooseButton; //text for instructing user how to graph attribute across list on screen
	
	public ArrayList<Planet> getList() {
		return graphList;
	}
	public ArrayList<Planet> getMain() {
		return mainListArray;
	}
	public String getAttribute() {
		return graphAttribute;
	}
	public void setList(ArrayList<Planet> list) {
		 graphList = list ;
	}
	
	//Buttons for graphing different attributes across list
	private Button graphMoons;
	private Button graphDistance;
	private Button graphTemp;
	private Button graphSize;
	ArrayList<String> displayList; //displaying items and attributes in list
	//	
	
	//incremental search declarations
	ListView searchListView;
	ArrayAdapter<Planet> searchAdapter;
	ArrayList<Planet> searchArrayList;
	int searchIndex;
	Button promptIncSearch;
	String incSearchText;
	ArrayList<Planet> mainListCopy;
	ArrayList<Planet> displayIncremental;
	LinearLayout vertLay;
	EditText searchInput2;
	//
	
	//instance for accessing the SQLite database
	DatabaseHelper database;

	//Context context;
    /** Called when the activity is first created. */
    
	////////////////////////////////////////////////ONCREATE/////////////////////////////////////////////////////////
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		//
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
       super.onCreate(savedInstanceState);
       makeViewLayout();
       makeAddLayout();
       makeSortLayout();
       makeGraphLayout();
       makeSearchLayout();
      
       //
    	Display display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	height = display.getHeight();
    	width = display.getWidth();
        
    	System.out.println(height);
    	
        database = new DatabaseHelper(this);
        
        //INITIALIZATION when table is empty        
        if (database.getTableSize()==0)
        	initialization();
   
        mainList = new ListView(this);
        
        mainListArray = new ArrayList<Planet>();
        
        //fill our arraylist 
        mainListArray = (ArrayList<Planet>) database.selectNumberRecords(database.getTableSize());
        
        mainAdapter = new ArrayAdapter<Planet>(activityInstance,
        android.R.layout.simple_list_item_1, mainListArray);

        mainList.setAdapter(mainAdapter);
        mainList.setClickable(true);
        mainList.setItemsCanFocus(false);
        mainList.setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView a, View v, int position, long id) {
            	
            		listIndex = position;
            		//view the selected item
            		changeLayout(Change.VIEW);          
                }
            });
                 
        mainList.setVerticalScrollBarEnabled(true);
        
        //mainList is added to the screen in mainLayout below
        
        makeMainLayout();        
        setContentView(mainLayout);
             
    }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void initialization() {
		// booleans are inserted using 1 or 0 for SQLite (false reads as 0)
		// in this order : size, rings, moons, name, solarsystem, averagetemp, distancefromearth, primarycompositionele
		database.insertPlanetRecord(new Planet(332000.0, false, 0, "Sun", "Earth''s Solar System", 5600.0, 0.0 , "gas"));
		database.insertPlanetRecord(new Planet(.055, false, 0, "Mercury", "Earth''s Solar System", 167.0, 57910.0 , "rock"));
		database.insertPlanetRecord(new Planet(.815, false, 0, "Venus", "Earth''s Solar System", 464.4, 108200.0 , "rock"));
		database.insertPlanetRecord(new Planet(1.0, false, 1, "Earth", "Earth''s Solar System", 15.0, 149600.0 , "rock"));
		database.insertPlanetRecord(new Planet(.107, false, 0, "Mars", "Earth''s Solar System", -20.0, 227940.0 , "rock"));
		database.insertPlanetRecord(new Planet(317.8, 1, 67,"Jupiter",  "Earth''s Solar System", -110.0, 778330.0 , "gas"));
		database.insertPlanetRecord(new Planet(95.2, 1, 62, "Saturn","Earth''s Solar System", -140.0,  1429400.0 , "gas"));
		database.insertPlanetRecord(new Planet(14.5, 1, 27,"Uranus",  "Earth''s Solar System", -195.0, 2870990.0 , "gas"));
		database.insertPlanetRecord(new Planet(17.1, 1, 13, "Neptune",  "Earth''s Solar System", -200.0, 4504300.0 , "gas"));
		database.insertPlanetRecord(new Planet(.002, false, 5, "Pluto", "Earth''s Solar System", -225.0, 5913520.0 , "rock"));
		database.insertPlanetRecord(new Planet(.0001, false, 0,"Ceres"  ,  "Earth''s Solar System", -106.0, 413832.0 , "rock"));
		database.insertPlanetRecord(new Planet(.01, false, 1, "Eris", "Earth''s Solar System", -240.0,  15000000.0 , "rock"));
		database.insertPlanetRecord(new Planet(.00066, false, 2, "Haumea", "Earth''s Solar System", -241.0,  7708000.0 , "rock"));
		database.insertPlanetRecord(new Planet(.0005, false, 0, "Makemake",  "Earth''s Solar System", -239.0,  7940000.0 , "rock"));
		//for these planets, "distance from sun" entered as 0 because we could not find the data
		database.insertPlanetRecord(new Planet(4.3, false, 0, "Kepler-11b", "Cygnus Solar System", 626.85,   13613., "rock"));
		database.insertPlanetRecord(new Planet(13.5, false, 0, "Kepler-11c", "Cygnus Solar System", 559.85,   15857., "rock"));
		database.insertPlanetRecord(new Planet(6.1, false, 0, "Kepler-11d", "Cygnus Solar System", 418.85,   23786. , "rock"));
		database.insertPlanetRecord(new Planet(8.4, false, 0, "Kepler-11e", "Cygnus Solar System", 343.85,   29021. , "rock"));
		database.insertPlanetRecord(new Planet(2.3, false, 0, "Kepler-11f", "Cygnus Solar System", 270.85,   37399. , "rock"));
		database.insertPlanetRecord(new Planet(0.1, false, 0, "Kepler-11g", "Cygnus Solar System", 126.85,  69114. , "rock"));
		database.insertPlanetRecord(new Planet(4.56, false, 0, "Kepler - 10b", "Draco", 1559.85 ,  2513. , "rock"));
		database.insertPlanetRecord(new Planet(19., false, 0, "Kepler - 10c", "Draco", 211.85 ,  36053. , "rock"));
		database.insertPlanetRecord(new Planet(8.7, false, 0, "Kepler - 20b", "Lyra", 740.85 , 6791. , "rock"));
		database.insertPlanetRecord(new Planet(16.1, false, 0, "Kepler - 20c", "Lyra", 439.85 , 13912. , "rock"));
		database.insertPlanetRecord(new Planet(20.1, false, 0, "Kepler - 20d", "Lyra", 95.85  , 151611. , "rock"));
		database.insertPlanetRecord(new Planet(.65, false, 0, "Kepler - 20e", "Lyra", 766.85 , 8033. , "rock"));
		database.insertPlanetRecord(new Planet(1.57, false, 0, "Kepler - 20f", "Lyra", 431.85 , 17502. , "rock"));
		
	}
	

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
   //mainLayout: first screen of the application
	void makeMainLayout(){
		  	
    	//horizontal layout holding the buttons and listView side-by-side
        mainLayout = new LinearLayout(this);
        
        //layout for holding objects (vertically) next to the listView on main screen
        LinearLayout subLayout = new LinearLayout(this);
        subLayout.setOrientation(LinearLayout.VERTICAL);        
        
        createItem = new Button(this);
        createItem.setText("Add item");
        createItem.setBackgroundColor(Color.BLACK);
        createItem.setTextColor(Color.YELLOW);
        createItem.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//move to create item layout if clicked
				changeLayout(Change.ADD);
				}
		
        });
       
        
        Button sortButton = new Button(this);
        sortButton.setText("Sort the list");
        sortButton.setBackgroundColor(Color.BLACK);
        sortButton.setTextColor(Color.YELLOW);
        sortButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//go to the sort layout if clicked
				changeLayout(Change.SORT);				
			} 			
        });
      
        
        graphButton = new Button(this);
        graphButton.setText("Graph an attribute across the whole list");
        graphButton.setBackgroundColor(Color.BLACK);
        graphButton.setTextColor(Color.YELLOW);
        graphButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				changeLayout(Change.GRAPH);
			}
        });
           
        incrementalSearch = new Button(this);
        incrementalSearch.setText("Perform an incremental search");
        incrementalSearch.setBackgroundColor(Color.BLACK);
        incrementalSearch.setTextColor(Color.YELLOW);
        incrementalSearch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				changeLayout(Change.SEARCH);
			}
			});
        
        DELETE_LIST = new Button(this);
        DELETE_LIST.setText("DELETE THIS LIST");
        DELETE_LIST.setBackgroundColor(Color.BLACK);
        DELETE_LIST.setTextColor(Color.RED);
        DELETE_LIST.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {				
                AlertDialog.Builder adb=new AlertDialog.Builder(activityInstance);//.create();
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete the list?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                	@Override
                    public void onClick(DialogInterface dialog, int which) {
                		//clear everything; empty the list and adapter (if desired)
                		mainAdapter.clear();
                        mainAdapter.notifyDataSetChanged();
                        database.deleteAllPlanetRecords();
                    }
				});
                adb.show();				
				}			
        });
        DELETE_LIST.setGravity(Gravity.CENTER);
    	
    	//entry for searching
    	searchInput = new EditText(this);
    	searchInput.setHint("Enter name to search for: ");
    	
    	//click to search for entered name
    	search = new Button(this);
    	search.setText(" Search for name entered ");
    	search.setBackgroundColor(Color.BLACK);
    	search.setTextColor(Color.GREEN);
    	search.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {				
				searchName = searchInput.getText().toString().trim();
				//matches the name with the index of the arrayList using a counter			
				int countNames = 0;
				for (Planet p : mainListArray){
					if (p.getName().toUpperCase().equals(searchName.toUpperCase())) {
						//after finding the correct item, go to its view using captured index
						listIndex = countNames; 	
						changeLayout(Change.VIEW);
					}
					else 
						countNames+=1;	
				}
			}
    	});
		search.setGravity(Gravity.CENTER);	
    	//add these params to our bottom button between the SEARCH editText/button and the rest
    	//as to create a gap between the two 
    	LayoutParams gapParams = new LayoutParams(
    	        LayoutParams.WRAP_CONTENT,      
    	        LayoutParams.WRAP_CONTENT
    	);	
    	gapParams.setMargins(0, height/24, 0, 0);
    	
    	//make the search text fully visible by establishing margins 
    	LayoutParams visibilityParams = new LayoutParams(
    	        LayoutParams.WRAP_CONTENT,      
    	        LayoutParams.WRAP_CONTENT
    	);	
    	visibilityParams.setMargins(0, 0, 0, 0);
    	searchInput.setLayoutParams(visibilityParams);
    	
       //left half of the screen
       subLayout.addView(createItem);
       subLayout.addView(sortButton);     
       subLayout.addView(graphButton, visibilityParams);
       subLayout.addView(incrementalSearch);
       subLayout.addView(DELETE_LIST);
       
       subLayout.addView(searchInput, gapParams);
       subLayout.addView(search);
       
       //add the space image (IT IS ADDED DIFFERENTLY (layout-wise) DEPENDING ON WHERE)
       image = new ImageView(this);	
       int id = getResources().getIdentifier("space", "drawable", getPackageName());
       LinearLayout.LayoutParams vp = 
           new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 
                           LayoutParams.FILL_PARENT);
       image.setLayoutParams(vp);        
       image.setImageResource(id);        
       image.setScaleType(ScaleType.FIT_XY);
       //set the height of the display to stretch the image in the left half of the layout
   	    image.getLayoutParams().height = height;
   		//        
       subLayout.addView(image);          
        
       
       //add the buttons/image on the left of this horizontal layout and THEN the listView
        mainLayout.addView(subLayout);
        mainLayout.addView(mainList);              
    }

   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
   //addLayout --> create a new item && add it to our list.
    
    @SuppressWarnings("serial") //double-brace initialization used
    
	void makeAddLayout() {
    	
    	Display display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	height = display.getHeight();
    	width = display.getWidth();
    	 
        image = new ImageView(this);   	
        int id = getResources().getIdentifier("space", "drawable", getPackageName());       
        LinearLayout.LayoutParams vp = 
            new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
                            LayoutParams.WRAP_CONTENT);
        image.setLayoutParams(vp);        
        image.setImageResource(id);        
        image.setScaleType(ScaleType.FIT_XY);
        image.getLayoutParams().width = width;
        image.getLayoutParams().height = height/8;
       
    	    	    	
    	addLayout = new LinearLayout(this);
        addLayout.setOrientation(LinearLayout.VERTICAL);
        
        
        revert = new Button(this);
        revert.setText("Return to main list");
        revert.setBackgroundColor(Color.GREEN);
        revert.setTextColor(Color.BLACK);
        
        revert.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				changeLayout(Change.REVERT);
				
			} 			
        });
        
        addListView = new ListView(activityInstance);
        addArrayList = new ArrayList<String>() {{
        	add("Enter Size (Double)");
        	add("Enter Rings (Boolean)");
        	add("Enter Moons (Integer)");
        	add("Enter Name (String)");
        	add("Enter Solar System (String)");
        	add("Enter Average Temperature (Double, Celsius)");
        	add("Enter Distance from Sun (Double)");
        	add("Enter Primary Composition Element (String)");      	
       }};
        
        addArrayAdapter = new ArrayAdapter<String>(activityInstance,
        android.R.layout.simple_list_item_1, addArrayList);
        addListView.setAdapter(addArrayAdapter);
        addListView.setClickable(true);
        addListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView a, View v, int position, long id) {
                		
            		addIndex = position; 
            		
            		AlertDialog.Builder alert = new AlertDialog.Builder(activityInstance);            		 
            		 alert.setTitle("Entry");  
            		 //this arraylist is holding messages such as "enter name here"
                     alert.setMessage(addArrayList.get(position)+":");  
           
                     // Set an EditText view to get user input 
                     addInput = new EditText(activityInstance);  
                     alert.setView(addInput);                         
                     alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
                   	 @Override
					public void onClick(DialogInterface dialog, int whichButton) {            
                   		 
                   		 //switch statement finds the attribute being entered
                   		 //based on where the user clicked in the listView 
                   		 //throws exceptions in cases where parsing a numeric from a string fails
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
                   		 
                   		 case 1: //rings
                   			if (addInput.getText().toString().trim()!=null){                   	
                   				rings = Boolean.parseBoolean(addInput.getText().toString().trim());
                   				addArrayList.set(1, "Rings (boolean) currently entered as: " + rings.toString());	
                   			}                   			                     				                   				                   		
		                   	break;		
		                   	
                   		case 2: //moons
                   			if (addInput.getText().toString().trim()!=null){
                   				try{
                   				moons = Integer.parseInt(addInput.getText().toString().trim());
                   				addArrayList.set(addIndex, "Moons (int) currently entered as: " + moons.toString());
                   			}catch(Exception e) {   
               					AlertDialog.Builder adb =new AlertDialog.Builder(activityInstance);//.create();
               					adb.setTitle("Incorrect input");
                                adb.setMessage("Enter a numeric integer value");                             
                                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0, int arg1) {												
									}
									});
                                adb.show();                                    
               				}                			                			 
               			 }                   		
                   			 	                  		
		                 break; 
		                   		
                   		 case 3: //name
                   			 if (addInput.getText().toString().trim()!=null) {
                   				name = addInput.getText().toString().trim();   
                   			    addArrayList.set(addIndex, "Name currently entered as: " + name);	
                   			 }
                   			       		
                   			 break;
                   		
                   		 case 4: //solar system
                   			 if (addInput.getText().toString().trim()!=null) {
                   				solarSystem = addInput.getText().toString().trim(); 
                   				addArrayList.set(addIndex, "SolarSys currently entered as: " + solarSystem);
                   			 }
                   		 	        
                   			 break;
                   			 
                   		 case 5: //avg. temperature
                   			 if (addInput.getText().toString().trim()!=null){
                   				 try{
                   				averageTemperature = Double.parseDouble(addInput.getText().toString().trim());
                   				addArrayList.set(addIndex, "AvgTemp currently entered as: " + averageTemperature.toString());
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
                   			 
                   		 case 6: //distance from sun
                   			 if (addInput.getText().toString().trim()!=null){
                   				 try{
                   				distanceFromSun = Double.parseDouble(addInput.getText().toString().trim());
                   			    addArrayList.set(addIndex, "DistFromSun currently entered as: " + distanceFromSun.toString());
                   				 } catch(Exception e) {   
                 					AlertDialog.Builder adb =new AlertDialog.Builder(activityInstance);//.create();
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
                   			 
                   		 case 7: //primary comp. ele
                   			 if (addInput.getText().toString().trim()!=null){
                   				primaryCompositionElement = addInput.getText().toString().trim(); 
                   				addArrayList.set(addIndex, "Primary Composition Element (String) currently entered as: " + primaryCompositionElement);	
                   			 }
                   			 
                   			 break;
                   		 }                 		
                   		 //make updates after user input given
                   		 addArrayAdapter = new ArrayAdapter<String>(activityInstance,
	    					        android.R.layout.simple_list_item_1, addArrayList);
                   		 addListView.setAdapter(addArrayAdapter);	                   	                  	
                   		 mainAdapter.notifyDataSetChanged();	                   		
                                   		
                   	 } 
                     });                      
                     alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
                       @Override
					public void onClick(DialogInterface dialog, int whichButton) {    
                       }  
                    });             
                     alert.show();  
                 }            
            });

			    //click once all attributes have been entered    
			    Button create = new Button(this);
			    create.setText("Click to create (after entering each attribute)");
			    create.setBackgroundColor(Color.GREEN);
			    create.setTextColor(Color.BLACK);			         
			    create.setOnClickListener(new OnClickListener(){
			    @Override
			 	public void onClick(View arg0) {
			 				
				
			    //if an attribute hasn't been entered, don't accept the item in the database
			 	if (size == null|| rings == null|| moons==null || name==null || solarSystem==null || averageTemperature==null || distanceFromSun==null|| primaryCompositionElement == null)
			 	{
			 		AlertDialog.Builder adb = new AlertDialog.Builder(activityInstance);
			        adb.setTitle("Missing attribute(s)");			    
			        adb.setMessage("Enter a value for each attribute.");
			        adb.setNegativeButton("Cancel", null);
			        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
			        }
				});
			     adb.show();
			 	}
			 	else{//here we are adding the item to our DB since no null values were found
			 		//use these since we can't put actual true/false values in the SQLite DB			 					 		
			 		int ringHolder;
			 		if (rings == true) ringHolder = 1;
			 		else ringHolder = 0;			 
			 		
			 		database.insertPlanetRecord(new Planet(size, ringHolder, moons, name, solarSystem, averageTemperature, distanceFromSun , primaryCompositionElement));
					
			 		//after adding a new item, update the main screen
			 		mainListArray = new ArrayList<Planet>();
			 		mainListArray = (ArrayList<Planet>) database.selectNumberRecords(database.getTableSize());
			 		mainAdapter = new ArrayAdapter<Planet>(activityInstance,
					        android.R.layout.simple_list_item_1, mainListArray);			
			 		mainList.setAdapter(mainAdapter);
			 		mainLayout.removeViewInLayout(mainList);
			 		mainLayout.addView(mainList);				
			 		mainAdapter.notifyDataSetChanged();
			 		//
			 		
			 		//reset the entry labels to facilitate creation of another new item
			 		addArrayList = new ArrayList<String>() 
			 		{{
			 			add("Enter Size in Earth Mass(Double)");
			        	add("Enter Rings (Boolean)");
			        	add("Enter Moons (Integer)");
			        	add("Enter Name (String)");
			        	add("Enter Solar System (String)");
			        	add("Enter Average Temperature (Double, Celsius)");
			        	add("Enter Distance from Sun in Megameters (double)");
			        	add("Enter Primary Composition Element (String)");		        	
			        }};
			       				
			       addArrayAdapter = new ArrayAdapter<String>(activityInstance,
					        android.R.layout.simple_list_item_1, addArrayList);
				
              		addListView.setAdapter(addArrayAdapter);
              		              		
              		mainAdapter.notifyDataSetChanged();	
          	
              		 //reset holders after creation.
              		 size=null;
              		 rings=null;
              		 moons=null;
              	     name=null;
              	     solarSystem=null;
              		 averageTemperature=null;
              		 primaryCompositionElement=null;
              		 distanceFromSun=null;								
			 				}			
			 		}
        });
             
	    addLayout.addView(revert);
	    addLayout.addView(image);
	    addLayout.addView(create);
	    addLayout.addView(addListView);
   
     }
  
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //viewLayout: how we "see" an item when we click on it in our main listView

    void makeViewLayout(){   
      	
    	viewLayout = new LinearLayout(this);
        viewLayout.setOrientation(LinearLayout.VERTICAL);
        
        //remove item 
        Button deleteItem = new Button(this);
        deleteItem.setText("DELETE THIS ITEM");
        deleteItem.setBackgroundColor(Color.RED);
        deleteItem.setTextColor(Color.BLACK);       
        deleteItem.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//remove an existing item from the list	by name						
				database.deleteItem(mainListArray.get(listIndex));
				mainListArray.remove(listIndex);
				
				//update the adapter and listView after changing the list
				mainAdapter = new ArrayAdapter<Planet>(activityInstance,
					        android.R.layout.simple_list_item_1, mainListArray);				
				mainList.setAdapter(mainAdapter);
				mainLayout.removeViewInLayout(mainList);
				mainLayout.addView(mainList);
				mainAdapter.notifyDataSetChanged();				
				
				//these views are added in the changeLayout method below after we have an instance
				//to display; we remove them before leaving to prevent overflow when we come back
				viewLayout.removeViewInLayout(attributeDisplay);
		        viewLayout.removeViewInLayout(planetGraphInstance);
		        viewLayout.removeViewInLayout(image);
		        
		        //go back to the main screen 
				changeLayout(Change.REVERT);
		
				} 
        });
        
       
        
        revert = new Button(this);
        revert.setText("Return to the main list");
        revert.setBackgroundColor(Color.GREEN);
        revert.setTextColor(Color.BLACK);
        
        revert.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
				 viewLayout.removeViewInLayout(planetGraphInstance);
			     viewLayout.removeViewInLayout(attributeDisplay);
			     viewLayout.removeViewInLayout(image);
			     
				 changeLayout(Change.REVERT);
				 
			} 
			
        });
        
        viewLayout.addView(deleteItem);
        viewLayout.addView(revert);
        
    }
    
   
    //////////////////////////////////////////////////////////////////////////////////////
    //start of sort layout: can sort by any attribute
    
    @SuppressWarnings("serial") //double-brace initialization used
    
	void makeSortLayout() {
    	
    	Display display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	height = display.getHeight();
    	width = display.getWidth(); 
    	
        image = new ImageView(this);   	
        int id = getResources().getIdentifier("space", "drawable", getPackageName());       
        LinearLayout.LayoutParams vp = 
            new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
                            LayoutParams.WRAP_CONTENT);
        image.setLayoutParams(vp);        
        image.setImageResource(id);        
        image.setScaleType(ScaleType.FIT_XY);
        image.getLayoutParams().width = width;
        image.getLayoutParams().height = height/8;
    	
        //clickable items representing attributes to sort by
    	sortListView = new ListView(activityInstance);
    	sortLayout = new LinearLayout(this);
        sortLayout.setOrientation(LinearLayout.VERTICAL);       
      
        sortArrayList = new ArrayList<String>() {{
       	add("Size");
        	add("Rings");
        	add("Moons");
        	add("Name");
        	add("Solar System");
        	add("Average Temperature (Celsius)");
        	add("Distance from Sun");
       	add("Primary Composition Element");
        	
       }};
        
        sortAdapter = new ArrayAdapter<String>(activityInstance,
        android.R.layout.simple_list_item_1, sortArrayList);
    
        sortListView.setAdapter(sortAdapter);
        sortListView.setClickable(true);
        sortListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView a, View v, int position, long id) {
             //place clicked
             sortIndex = position;
           	 AlertDialog.Builder adb=new AlertDialog.Builder(activityInstance);
             adb.setTitle("Confirm");
             adb.setMessage("Sort by " + sortArrayList.get(sortIndex)+"?");
             adb.setNegativeButton("Cancel", null);
             adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
             	@Override
                 public void onClick(DialogInterface dialog, int which) {             		
             		switch(sortIndex) {
             		case 0: //size
             			Collections.sort(mainListArray, new CompareSize());
             		break;
             		case 1: //rings
             			Collections.sort(mainListArray, new CompareRings());
             		break;
             		case 2: //moon
             			Collections.sort(mainListArray, new CompareMoons());
             		break;
             		case 3: //name
             			Collections.sort(mainListArray, new CompareName());
             		break;
             		case 4: //solar system
             			Collections.sort(mainListArray, new CompareSolarSystem());
             		break;
             		case 5: //average temp
             			Collections.sort(mainListArray, new CompareAvgTemp());
             		break;
             		case 6: //dist from planet's own sun
             			Collections.sort(mainListArray);
             		break;
             		case 7: //primary composition element
             			Collections.sort(mainListArray, new ComparePrimaryComp());
             		break;             		 					
 				}             		
             	    //update listView and adapter after sorting the list        		
             		mainAdapter = new ArrayAdapter<Planet>(activityInstance,
 	 				        android.R.layout.simple_list_item_1, mainListArray); 
 	 				mainList.setAdapter(mainAdapter);
 					mainLayout.removeViewInLayout(mainList);
 					mainLayout.addView(mainList);
 					mainAdapter.notifyDataSetChanged();
 					
 				     //after sorting, give user ability to return to main list or sort again
 		             AlertDialog.Builder adb2 = new AlertDialog.Builder(activityInstance);
 		             adb2.setTitle("Sorting complete");
 		             adb2.setMessage("Return to main list?");
 		             adb2.setNegativeButton("Cancel", null);
 		             adb2.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
 		             	@Override
 		                 public void onClick(DialogInterface dialog, int which) {
 		             		changeLayout(Change.REVERT);
 		             	}
 		             	});
 		             adb2.show();		
                 }
				});
             adb.show();            	
            }
        });
  
        revertFromSort = new Button(this);
        revertFromSort.setText("Return to the main list");
        revertFromSort.setBackgroundColor(Color.GREEN);
        revertFromSort.setTextColor(Color.BLACK);
        
        revertFromSort.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
				changeLayout(Change.REVERT);
				 
			} 
			
        });
        
        chooseButton = new TextView(this);
        chooseButton.setText("Choose an attribute to sort by: ");
        chooseButton.setTextColor(Color.parseColor("#FFFFFF")); //white
        
        //add the listview and the button to the layout 
        sortLayout.addView(revertFromSort); 
        sortLayout.addView(image);
        sortLayout.addView(chooseButton);
        sortLayout.addView(sortListView);
               
    }
  
    //////////////////////////////////////////////////////////////////////////////////////////////
    //graphLayout: used for graphing attributes across whole list 
    //goes immediately to displaying distance upon entering the layout but gives options for all numeric attributes


   void makeGraphLayout() {
	   
	   
	   
	   Display display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
   		height = display.getHeight();
   		width = display.getWidth();
	  
	  chooseButton = new TextView(this);
	  chooseButton.setText("Choose a button to see that attribute graphed across the list: ");
	  chooseButton.setTextColor(Color.parseColor("#FFFFFF"));

       image = new ImageView(this);   	
       int id = getResources().getIdentifier("space", "drawable", getPackageName());       
       LinearLayout.LayoutParams vp = 
           new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
                           LayoutParams.WRAP_CONTENT);
       vp.setMargins(0, 0, 0, height/24);
       image.setLayoutParams(vp);        
       image.setImageResource(id);        
       image.setScaleType(ScaleType.FIT_XY);
       image.getLayoutParams().width = width;
       image.getLayoutParams().height = height/8;
       
	    
	   //graphs added as we choose to (changing layouts, shown below)
	   //graphLayout = (RelativeLayout) findViewById(R.id.graphLayout);
       //graphLayout = R.layout.xmlayout2;
	   
	   revertFromGraph = new Button(this);
	   revertFromGraph.setText("Return to the main list");
	   revertFromGraph.setBackgroundColor(Color.GREEN);
	   revertFromGraph.setTextColor(Color.BLACK);
	   revertFromGraph.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//as in viewLayout, remove things as we leave to prevent overflow
				graphs.removeViewInLayout(planetGraphInstance);
				graphs.removeViewInLayout(image);
				graphs.removeViewInLayout(graphDisplay);
				
				changeLayout(Change.REVERT);					
			} 
			
       });
	   
	   //the following four buttons are for choosing a numeric attribute to display across list
	   
	   	 
   		
   		
   		graphDistance = (Button) findViewById(R.id.button1);
	   
	   //graphDistance = (Button)findViewById(R.id.button1);
	   //graphDistance.setText("Distance");
	   //graphDistance.setBackgroundColor(Color.BLACK);
	   //graphDistance.setTextColor(Color.YELLOW);
	   
			   
	   View.OnClickListener method1 = new OnClickListener() {
		   @Override
			public void onClick(View arg0) {
				//remove what was there originally 
				graphs.removeViewInLayout(planetGraphInstance);
				graphs.removeViewInLayout(graphDisplay);				
				
				//add in the new graph for this attribute
				graphAttribute = "DISTANCE";
				
				graphDisplay = new TextView(PlanetActivity.this);
				
		    	//display each planet with its attribute
				displayList = new ArrayList<String>();
				int countInList=1;
		    	for (Planet p : mainListArray) {
		    		displayList.add("("+countInList+") "+p.getName()+": "+p.getDistanceFromSun());
		    		countInList++;
		    	}
		    	graphDisplay.setText("Distance from Sun (megameters) for each planet, with bars ordered respectively: \n\n" + 
		    			displayList +"\n\n(The Kepler Planets all have 0 as distance because the data was not available)"
		    	);
		    	graphDisplay.setTextColor(Color.parseColor("#FFFFFF"));
		    	
		    	//helper method which creates instance of the graph view and displays everything
		    	//(shown directly below this method)*********
				graphIt();								
				
			}
		   
	   };
	   
	   //graphDistance.setOnClickListener(method1);	   
	  
			
			/*
	   
	   graphMoons = (Button) contentView.findViewById(R.id.button2);
	   //graphMoons.setText("Number of moons");
	   //graphMoons.setBackgroundColor(Color.BLACK);
	   //graphMoons.setTextColor(Color.YELLOW);
	   graphMoons.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//remove what we had originally
				graphs.removeViewInLayout(planetGraphInstance);
				graphs.removeViewInLayout(graphDisplay);				
				//add in the new graph for this attribute
				graphAttribute = "MOONS";
				
				graphDisplay = new TextView(PlanetActivity.this);
		    	
				//display each planet with its attribute
				displayList = new ArrayList<String>();
				int countInList=1;
		    	for (Planet p : mainListArray) {
		    		displayList.add("("+countInList+") "+p.getName()+": "+p.getMoons());
		    		countInList++;
		    	}
		    	graphDisplay.setText("Number of moons for each planet, with bars ordered respectively: \n\n" + 
		    			displayList 
		    	);
		    	graphDisplay.setTextColor(Color.parseColor("#FFFFFF"));
		    	
				graphIt();
				
			}
			});
	   
	   graphTemp = (Button) contentView.findViewById(R.id.button3);
	   //graphTemp.setText("Avg. Temperature");
	   //graphTemp.setBackgroundColor(Color.BLACK);
	   //graphTemp.setTextColor(Color.YELLOW);
	   graphTemp.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//remove what was there originally
				graphs.removeViewInLayout(planetGraphInstance);
				graphs.removeViewInLayout(graphDisplay);				
				//add in the new graph for this attribute
				graphAttribute = "TEMP";
				
				graphDisplay = new TextView(PlanetActivity.this);

		    	//display each planet with its attribute
				displayList = new ArrayList<String>();
				int countInList = 1;
		    	for (Planet p : mainListArray) {
		    		displayList.add("("+countInList+") "+p.getName()+": "+p.getAverageTemperature());
		    		countInList++;
		    	}
		    	graphDisplay.setText("Average Temperature (Celsius) for each planet, with bars ordered respectively: \n\n" + 
		    			displayList 
		    	);
		    	graphDisplay.setTextColor(Color.parseColor("#FFFFFF"));
		    	
				graphIt();		
				
			}
			});
	   
	   graphSize = (Button) contentView.findViewById(R.id.button4);
	   //graphSize.setText("Size");
	   //graphSize.setBackgroundColor(Color.BLACK);
	   //graphSize.setTextColor(Color.YELLOW);
	   graphSize.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//remove what was there originally
				graphs.removeViewInLayout(planetGraphInstance);
				graphs.removeViewInLayout(graphDisplay);				
				//add in the new graph for this attribute
				graphAttribute = "SIZE";
				
				graphDisplay = new TextView(PlanetActivity.this);

		    	//display each planet with its attribute
				displayList = new ArrayList<String>();
				int countInList = 1;
		    	for (Planet p : mainListArray) {
		    		displayList.add("("+countInList+") "+p.getName()+": "+p.getSize());
		    		countInList++;
		    	}
		    	graphDisplay.setText("Size of each planet in Earth mass, with bars ordered respectively: \n\n" + 
		    			displayList 
		    	);
		    	graphDisplay.setTextColor(Color.parseColor("#FFFFFF"));
		    	
				graphIt();		
				
			}
			});
	   */
	   LayoutParams gapParams = new LayoutParams(
	   	        LayoutParams.FILL_PARENT,      
	   	        LayoutParams.WRAP_CONTENT
	   	);
	  
	   gapParams.setMargins(0, 0, 0, height/48);
				
		   
   }    
   ////////////////////////////////////////////////////////////////////////////////////////////
   //helper method for displaying attributes across list 
   //(creates the instance of our996 view after determining which attribute to show)
   public void graphIt() {
	   if (mainListArray!=null) {
		   graphList = mainListArray; //contains data to graph	
			planetGraphInstance = new PlanetGraphView(PlanetActivity.this);   
	   
			LinearLayout.LayoutParams pvLayout = 
       		new LinearLayout.LayoutParams(
       				ViewGroup.LayoutParams.WRAP_CONTENT,
       				ViewGroup.LayoutParams.WRAP_CONTENT);       
			planetGraphInstance.setLayoutParams(pvLayout);                  
         
			LayoutParams gapParams = new LayoutParams(
   	        LayoutParams.WRAP_CONTENT,      
   	        LayoutParams.WRAP_CONTENT
   	);
			//leave a space below it
			gapParams.setMargins(0, 0, 0, height/24);
	   
	   //add the graph and display of items
	   graphs.addView(graphDisplay);//, gapParams);
       graphs.addView(planetGraphInstance);
	   }
	   
   }
 ///////////////////////////////////////////////////////////////////////////////////
  //start of searchLayout: used for incremental search
   
public void makeSearchLayout() {
		
	//space image again
		Display display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		height = display.getHeight();
		width = display.getWidth();
	    image = new ImageView(this);   	
	    int id = getResources().getIdentifier("space", "drawable", getPackageName());       
	    LinearLayout.LayoutParams vp = 
	        new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
	                        LayoutParams.WRAP_CONTENT);
	    vp.setMargins(0, 0, 0, height/24);
	    image.setLayoutParams(vp);        
	    image.setImageResource(id);        
	    image.setScaleType(ScaleType.FIT_XY);
	    image.getLayoutParams().width = width;
	    image.getLayoutParams().height = height/8;
	    
	    revert = new Button(this);
        revert.setText("Return to main list");
        revert.setBackgroundColor(Color.GREEN);
        revert.setTextColor(Color.BLACK);
        
        revert.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				changeLayout(Change.REVERT);
				
			} 			
        });
	   
	    searchLayout = new LinearLayout(this);
    	searchInput2 = new EditText(this);	
    	searchInput2.setHint("Enter string to search for: ");
  
	    //populates based on search results; can click to see that particular planet
    	searchListView = new ListView(activityInstance);     
    
        //backed by a copy of main list which we eliminate from as we search for incremental input string
    	mainListCopy = mainListArray;
        searchAdapter = new ArrayAdapter<Planet>(activityInstance,
        android.R.layout.simple_list_item_1, mainListCopy);
    
        searchListView.setAdapter(searchAdapter);
        searchListView.setClickable(true);
        searchListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView a, View v, int position, long id) {
            	listIndex = position;
            	changeLayout(Change.VIEW);
            }
            });
        
        promptIncSearch = new Button(this);
        promptIncSearch.setText("Search");
        promptIncSearch.setBackgroundColor(Color.BLACK);
        promptIncSearch.setTextColor(Color.GREEN);
        promptIncSearch.setOnClickListener(new OnClickListener(){
 			@Override
 			public void onClick(View arg0) {
 				
 				mainListCopy = mainListArray;
 				incSearchText = searchInput2.getText().toString().trim();
 				System.out.println("test");
 				System.out.println(incSearchText);
 				displayIncremental = new ArrayList<Planet>();
 				
 				//If a planet has any string attribute with the string we add it to our list
 				for (Planet planet: mainListCopy){
 		    			if ((planet.getName().contains(incSearchText)==true) || 
 		    					(planet.getSolarSystem().toLowerCase().contains(incSearchText.toLowerCase())==true) ||
 		    					(planet.getPrimaryCompositionElement().toLowerCase().contains(incSearchText.toLowerCase())==true)){					    				
		    			
 		    				displayIncremental.add(planet);
 		    			}
 		    		}
 				
 				searchAdapter = new ArrayAdapter<Planet>(activityInstance,
 				        android.R.layout.simple_list_item_1, displayIncremental);
 				
 				searchListView.setAdapter(searchAdapter);	                   	                  	
          		searchAdapter.notifyDataSetChanged();
          		
          		searchLayout.removeViewInLayout(searchListView);
          		searchLayout.addView(searchListView);
 				
 							
 			}//
 			});
        
        vertLay = new LinearLayout(this);
        vertLay.setOrientation(LinearLayout.VERTICAL);
        
        vertLay.addView(revert);
        vertLay.addView(searchInput2);
        vertLay.addView(promptIncSearch);
            
        searchLayout.addView(vertLay);
        searchLayout.addView(searchListView);
		
}
 ///////////////////////////////////////////////////////////////////////////////////
 //start of change layout : facilitates switching between layouts
    
public void changeLayout(Change var){
	
	//space image again
	Display display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	height = display.getHeight();
	width = display.getWidth();
    image = new ImageView(this);   	
    int id = getResources().getIdentifier("space", "drawable", getPackageName());       
    LinearLayout.LayoutParams vp = 
        new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
                        LayoutParams.WRAP_CONTENT);
    vp.setMargins(0, 0, 0, height/24);
    image.setLayoutParams(vp);        
    image.setImageResource(id);        
    image.setScaleType(ScaleType.FIT_XY);
    image.getLayoutParams().width = width;
    image.getLayoutParams().height = height/8;
   
	
	switch(var){ 
	
	case SEARCH:
		this.setContentView(searchLayout);
		break;
	
	case SORT:
		this.setContentView(sortLayout);
		break;
	
	case ADD: 
		
		this.setContentView(addLayout);				
		break;
		
	case REVERT: 
		this.setContentView(mainLayout);	
		break;
	
	case GRAPH: //more code here (and in view) because we are adding to the layout as views are changed 
				//this code for adding the distance graph by default is necessary because 
				//before any views have been added we don't want to remove them
		
		//when we first switch to this view, we show the distances graph by default
		
		
		if (mainListArray!=null) {
			//graphLayout = (RelativeLayout) findViewById(R.id.graphLayout);
			graphList = mainListArray;
			graphAttribute = "DISTANCE";
			
		planetGraphInstance = new PlanetGraphView(this);   		
    	         
        LinearLayout.LayoutParams pvLayout = 
        		new LinearLayout.LayoutParams(
        				ViewGroup.LayoutParams.WRAP_CONTENT,
        				ViewGroup.LayoutParams.WRAP_CONTENT);       
        planetGraphInstance.setLayoutParams(pvLayout);                  
          
        LayoutParams gapParams = new LayoutParams(
    	        LayoutParams.WRAP_CONTENT,      
    	        LayoutParams.WRAP_CONTENT
    	);
    	
        //leave space below
    	gapParams.setMargins(0, 0, 0, height/24);
    	
    	//display each planet with its size 
    	//use this instead of labeling the bars since as more bars are added labelling becomes impractical
    	ArrayList<String> displayList = new ArrayList<String>();
    	
    	int countInList = 1;
    	for (Planet p : mainListArray) {
    		displayList.add("("+countInList+") "+p.getName()+": "+p.getDistanceFromSun());
    		countInList++;
    	}
    	
    	graphDisplay = new TextView(this);
    	graphDisplay.setText("Distance from Sun (megameters) for each planet, with bars ordered respectively: \n\n" + 
    			displayList 
    	);
    	graphDisplay.setTextColor(Color.parseColor("#FFFFFF"));
    	
        
    	
        
        
    	RelativeLayout.LayoutParams lprams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
    	lprams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    	
    	
    	
    	LayoutInflater mInflater = LayoutInflater.from(this);  
    	View contentView = mInflater.inflate(R.layout.xmlayout2, null); 
    	graphLayout = (RelativeLayout) contentView.findViewById(R.id.graphLayout);// mContainerIconExtension in your case
    	graphs = (LinearLayout) contentView.findViewById(R.id.graph);
    	
    	//graphs.addView(graphDisplay);//, gapParams);
    	graphs.addView(planetGraphInstance, lprams);
    	
    	
		this.setContentView(graphLayout);		
		}
		break;
		
	case VIEW: //looking at a particular planet	
		
		//planet we clicked on
		selectedPlanet = mainListArray.get(listIndex);
		attributeDisplay = new TextView(this);
               
        if (selectedPlanet!=null) {        
       
        	attributeDisplay.setText(
        			"Name: " + selectedPlanet.getName() + "\n" +       			  
        			"Solar System: " + selectedPlanet.getSolarSystem() + "\n" +        			     			
        			"Primary composition element: " + selectedPlanet.getPrimaryCompositionElement() + "\n" + "\n" +
        			"Shown graphically below: \nDistance from the Sun (megameters), Size (in Earth mass), Avg. Temp. (Celsius), Number of moons, and Rings respectively" +
        			"\n\n(each numeric is scaled as a percentage (absolute value) of the maximum for that value in the list)**\n\n\n");              
        attributeDisplay.setTextColor(Color.parseColor("#FFFFFF"));   	        
        }
                   
    		ArrayList<Planet> graphPlanet = new ArrayList<Planet>();
    		//here graphList is just a single instance since we are graphing only a planet; not across the list
    		graphPlanet.add(selectedPlanet);
    		graphList = graphPlanet;
    		planetGraphInstance = new PlanetGraphView(this);   		 	
                    
        LinearLayout.LayoutParams pvLayout = 
        		new LinearLayout.LayoutParams(
        				ViewGroup.LayoutParams.WRAP_CONTENT,
        				ViewGroup.LayoutParams.WRAP_CONTENT);       
        planetGraphInstance.setLayoutParams(pvLayout);                  
        
        viewLayout.addView(image);
        viewLayout.addView(attributeDisplay);
        viewLayout.addView(planetGraphInstance);
   
        this.setContentView(viewLayout);
		break;
	}}	 
        
     @Override
    protected void onResume() {
    	// Ideally a game should implement onResume() and onPause()
    	// to take appropriate action when the activity loses focus
    	super.onResume();

    }

    @Override
    protected void onPause() {
    	// Ideally a game should implement onResume() and onPause()
    	// to take appropriate action when the activity loses focus
    	super.onPause();

    }
   
}



