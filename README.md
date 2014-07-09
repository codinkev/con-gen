con-gen
=======

(Android app) -- Generate contacts using data available on your phone

Common term - noncon or noncontact - means a number which is not a contact on phone but has data on phone (such as text/call)

GENERAL OVERVIEW - 
  accesses several content providers on phone (ContactsContract, CallLog, sms/inbox for text messages) 
    and then places this info in my own sqlite database (description of where content providers accessed below)

  Uses the info to find data associated with numbers that are not currently contacts and then gives you ability to see this information
    and decide if you want to make a contact out of it

  Can also use the info to view and edit existing contacts



Motivating case: 
  someone texts you their name so you can add them as a contact later but you forget.  This will help you see
    which numbers aren't contacts but have sent texts like that.  

  App can also be used to manage texts/calls more easily, deleting unwanted information from the phone.
  
  Also will offer an incremental search to find texts if you know there is an important text but can't remember from whom.



***OVERVIEW OF CLASSES - 

  in Activities section
  
    WelcomeScreen - splash screen on startup
    
    MainActivity - has navigation to all toplevel activities; offers button for refresh of sqlite database backing phone
    
    NonCons - listview displaying all numbers which are not contacts yet but have texts/calls on phone.  
      Clicking number on this listview for a number bring us to NonConExplorer
      
    NonConExplorer - for a given number clicked on in NonCons, can see all text/call activity and also have ability to 
      add the contact if you recognize info and want to add them
      
    Cons - uncompleted activity which will display and allow you to modify existing contacts.  Will offer camera API ability to take picture
      associated with a contact as well
      
  in Utilties section
  
    RefreshTables - contains methods for accessing content providers and storing in database
    
    DatabaseHelper - helper class for accessing SQLLite database
