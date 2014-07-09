/**
* Kevin Keitel, Connor Burke, Zach Pearson; CISC181-012 
* 5/19/2013, Project 
* 
*
*Access to our database so that data can persist
*/


package com.example.app1;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper {

    public static final String DATABASE_NAME = "MyLabData";
    public static final String TABLE_NAME = "Number_Records";
    
    Context context;
    SQLiteDatabase database;
    
    /**
     * Creates a database for the MyLabData application. Creates/opens a connection
     * to the underlying Android database.
     * 
     * @param context
     */
    public DatabaseHelper(Context context) {
        this.context = context;
        this.database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        
        createPlanetRecordsTable();
    }
    
    /**
     * Creates a table in the database if it does not exist already.
     */  
    
    private void createPlanetRecordsTable() {
    	//use this drop statement if getting table column errors
    	//database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        database.execSQL("CREATE TABLE IF NOT EXISTS "
                        + TABLE_NAME
                        + " (Size DOUBLE, Rings BOOLEAN, Moons INTEGER, Name STRING, "
                        + " SolarSystem STRING, AverageTemperature DOUBLE, DistanceFromSun DOUBLE, PrimaryCompositionElement STRING );");
    }   
    
    /**
     * Resets the database table to empty by deleting all rows.
     */
    public void deleteAllPlanetRecords() {
        this.database.execSQL("DELETE FROM " + TABLE_NAME);
    }   
    
    /**
     * Inserts a single record (row) into the database table.
     * 
     * @param record
     */   
    
    public void insertPlanetRecord(Planet record) {
        database.execSQL("INSERT INTO "
                        + TABLE_NAME
                        + " (Size, Rings, Moons, Name, SolarSystem, AverageTemperature, DistanceFromSun, PrimaryCompositionElement)"
                        + " VALUES (" + record.getSize() 
                        + ", " + record.getRingsInt()
                        + ", " + record.getMoons()                       
                        + ", '" + record.getName()
                        + "', '" + record.getSolarSystem()
                        + "', " + record.getAverageTemperature()                    
                        + ", " + record.getDistanceFromSun()
                        + ", '" + record.getPrimaryCompositionElement() +
                        "');");       
    }
    
    public void deleteItem(Planet p) {//by name	
    	this.database.delete(TABLE_NAME, "Name="+"='" + p.getName() + "'" , null);    		
    }   
            
    public int getTableSize() {   
    	int numRows = (int) DatabaseUtils.queryNumEntries(this.database, TABLE_NAME);		    	
    	return numRows;        
    }
   
    
    /**
     * Gets the current top numbers by querying the table for high numbers ordered
     * by the number and date in descending order.  Returns only the first
     * number of these.
     * 
     * @return
     */
    public List<Planet> selectNumberRecords(int number) {
        Cursor c = database.rawQuery("SELECT Size, Rings, Moons, Name, SolarSystem, AverageTemperature, DistanceFromSun, PrimaryCompositionElement" +
                                    " FROM " + TABLE_NAME 
                                    + " ORDER BY Name ASC;",
                                    null);
        /* Get the indices of the columns we will need */
        int sizeColumn = c.getColumnIndex("Size");
        int ringsColumn = c.getColumnIndex("Rings");
        int moonsColumn = c.getColumnIndex("Moons");   
        int nameColumn = c.getColumnIndex("Name");
        int solarSysColumn = c.getColumnIndex("SolarSystem");
        int avgTempColumn = c.getColumnIndex("AverageTemperature");       
        int earthDistColumn = c.getColumnIndex("DistanceFromSun");
        int primaryCompColumn = c.getColumnIndex("PrimaryCompositionElement");       
        List<Planet> highNumbers = new ArrayList<Planet>(number);
       
        if (c.moveToFirst()) {
            int i = 1;
            do {
                Planet record = new Planet(c.getDouble(sizeColumn),
                    c.getInt(ringsColumn)>0,
                    c.getInt(moonsColumn), 
                    c.getString(nameColumn), 
                    c.getString(solarSysColumn), 
                    c.getDouble(avgTempColumn), 
                   
                    c.getDouble(earthDistColumn), 
                    c.getString(primaryCompColumn));
                highNumbers.add(record);
                i++;
            }
            while (i <= number && c.moveToNext());
        }
        
        return highNumbers;
    }
    	
}
