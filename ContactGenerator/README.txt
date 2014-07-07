Make a class/classes "build database"
Need a table of text messages, phone log (this can be 
	should i have a separate class for each "model" (database object/table methods)
Have a class for displaying all existing contacts in a 
...
figure out how to properly organize packages...
one package with just the activities (?)
one helper class for each of the tables/associated methods in its own package
one class for each of the models in their own package
-----------------------------------------------------------------------------
in terms of actually creating tables:
ONCREATION: and after any contact updates: i want each table to refresh/be re-pulled
so instead of deleting records from text call table after insertion, just make new contact and re-pull everything
part of the pull process needs to be excluding text/info for those that are already contacts
	IN FACT, ONLY NEED TO DELETE NUM FROM ALLNUMS TABLE ... SINCE TEXTS/CALLS LOGS HAVE EVERYTHING BY DEFAULT
	(either do joins or have a filter on the listview...)
	(might still want a way to see contacts that are just #s because they almost are still not contacts)
pulling process: accessing providers: should thus be part of -- (which class?)
design concerns:
	should i have/not have the fill table methods in the model classes themselves?
	unique scenario since the tables are just mirrors of whats on my phone.  never adhoc insertions etc.
	that being said could they just be in the helper class itself? as it is initiated it should be filled?
	or have another helper class, PopulateTables which goes to the content providers etc... with each table having its method in that class		

current decision (can check later...): own class called refresh tables.  see that class for details
make phone number the primary key for joining etc???? is that a bad idea??? 

have all texts and calls in their own tables; exclude none
ONLY SHOW NUMBERS of those that arent contacts on the add contacts tab...
have another tab with numbers of existing contacts...
get text/call info when select a number by "select * where number =..."

in future, rather than recreate tables every startup to get new texts/calls, use
http://stackoverflow.com/questions/2735571/detecting-sms-incoming-and-outgoing
to detect... broadcast receiver.  
maybe not possible though since most be received when app not being used

need to have description at top of every class saying purpose etc... unifying theme somewhere etc...
DID THIS EVEN NEED ALL THE SQLITE STUFF??? JUST USE CONTENT PROVIDERS TO GET IN ARRAYLIST, POPULATE LISTVIEW
CAN THEN GET NONCONTACTS BY CHECKING CONTACT ARRAYLIST... 
MIGHT BE OTHER FEATURES IN FUTURE THAT NEED SQLITE THOUGH...?

***WHAT ACTUAL IMPLEMENTATION AM I TECHNICALLY USING NOW??? ITS NOT SINGLETON.  
ITS NOT THE CONTENT PROVIDER APPROACH... WHAT IS IT ACTUALLY CONSIDERED???
******LEARN MORE ABOUT THIS AND CONTENT PROVIDERS ETC... how to properly use them/declare in manifest
	!!! see www.vogella.com/tutorials/AndroidSQLite/article.html#contentprovider_overview
------------------------------------------------------------------------

Database implementation aside, once have it, concept:
first activity is a menu type screen (figure out how to use that menu thing next to oncreate...)
	either see listview of existing contacts for editing (somehow able to incremental search/regex search
														  as well for contacts that have numbers as names)
	other option is "new contact addition explorer" type thing
		launches as a listview which has just numbers as each entry in a listview	
		you click on the number in the listview and it can give options (like a bubble select menu)
		options: see call activity
				 see text activity
				 	(for texts, be able to guess name based on texts...)
				 	if select either of these options, deploy a new activity via intent
				 	with own listview showing each call/text in history
				 	can click each of these items and see all of that associated info
				 add contact 
				 	brings up a form to enter the relevant fields and can be created after doing so...
		this view only has those numbers which arent existing contacts by comparing #s so after adding 
		a contact from that list it needs to refresh so it disappears...
		
**then make it aesthetically pleasing. 
ALSO: for the text msg etc, make it so you can actually delete them from phone if want on the listview.
have another feature just in general where i can incremental search and delete calls/texts from the log in batches (delete from one sender all at once...)
		

------------------------------------------------------------------------------------------------------------------
ideas for future expansion:
stuff aside from listview etc... like fragments or more complex stuffs
learn about APPLICATION CONTEXT ... 
find way to incorporate FRAGMENTS or other UI features i havent explored??? 
maps api integration.  add fields to contact list such as address and can launch
maps of my location and show contacts that are near me.
camera api: take a picture to associate with each contact. 
start using github with this/some type of SVN version control.
how could i use webservices or location-based services???
multithreading, xml, exception handling...	

WHY DO BUTTONS ETC NEED INNER CLASSES.  WHY NOT JUST HAVE THE FUNCTIONALITY ANOTHER WAY
ANONYMOUS INNER CLASSES ETC RESEARCH...

LEARN TO USE TEST PROJECTS??? ASK SANJAY IF USED?

read these links for all the different components and stuff about different library types
also library projects for android ...
http://en.wikipedia.org/wiki/Library_%28computing%29
http://developer.android.com/tools/projects/index.html
http://developer.android.com/guide/topics/manifest/uses-library-element.html
http://stackoverflow.com/questions/2464725/does-the-adt-plugin-automatically-create-an-ant-build-file
_________________________________
scrap notes
--
//next piece: query phone log, texts, etc... 
		//http://android2011dev.blogspot.com/2011/08/get-android-phone-call-historylog.html
		//http://stackoverflow.com/questions/848728/how-can-i-read-sms-messages-from-the-inbox-programmatically-in-android
		//also need to figure out how to get a list of the existing contacts
		
		//once i have those 3 pieces ^, i can look at all possible contacts to add
		//and compare them to what i currently have to decide what needs to be added
		//then will come the part of adding clues like phone call details and text convos 
		//to remind/figure out who the hell it is ... 
		
		//use a sqlite database!!! store stuff and do joins and stuff.
		//figure out the best practices on that.
		
		/********************************************/
		//addContact();
		//getCallDetails();
		//getTextDetails();
		//getContacts();		
-- 	