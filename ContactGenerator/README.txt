con-gen
=======

ContactGenerator (Android app) -- Generate contacts using data available on your phone

Common term - noncon or noncontact - means a number which is not a contact on phone but has data on phone 
(such as texts/calls)

GENERAL OVERVIEW - 
  accesses several content providers on phone (ContactsContract, CallLog, sms/inbox for text messages) 
  and then places this info in a sqlite database (description of where content providers accessed below).
  Uses the info to identify numbers with texts/calls on phone that are not currently contacts (noncons) and 
  then gives you ability to view all noncons and their associated data to ultimately convert these noncons 
  to contacts if desired.
  Can also use the info to view and edit existing contacts.



Motivating case: 
  Someone texts you their name so you can add them as a contact later, but you forget.  This will help you see
  which numbers aren't contacts but have sent texts of that nature.  
  This app can also be used to manage texts/calls more easily, deleting unwanted information from the phone.
  Also will offer an incremental search to find texts if you know there is an important text 
  but can't remember from whom.



TO USE: on startup, click VIEW POTENTIAL CONTACTS on actionbar to see a list of noncons.  
Click a noncon to further explore the data available to describe this noncon (texts/calls).  
On this same explorer view, click "add as contact" on actionbar to enter a name and number and insert to your 
actual contact list.

From startup can also click VIEW EXISTING CONTACTS to see and edit current contacts on the phone 
(or take a picture to associate with this contact using camera API).

On startup, for first launch (and subsequent desired refreshes) will need to hit "refresh database" 
to gather all text/call data and generate a list of noncons and existing contacts. 
(NOTE: refresh functionality currently commented out until better way to allow cancelling mid-refresh)


***OVERVIEW OF CLASSES - 

  in Activities section
  
    WelcomeScreen - splash screen on startup
    
    MainActivity - has navigation to all toplevel activities; offers button for refresh of sqlite database 
    backing phone
    
    NonCons - listview displaying all numbers which are not contacts yet but have texts/calls on phone (all noncons).  
    Clicking number on this listview for a number brings us to NonConExplorer
      
    NonConExplorer - for a given number clicked on in NonCons, can see all text/call activity and also have ability to 
    add the contact if you recognize info and want to add them
      
    Cons - uncompleted activity which will display and allow you to modify existing contacts.  
    Will offer camera API ability to take picture associated with a contact as well
      
  in Utilties section
  
    RefreshTables - contains methods for accessing content providers and storing in database
    
    DatabaseHelper - helper class for accessing SQLLite database
