package com.kevin.contactgenerator.Utilities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kevin.contactgenerator.Activities.MainActivity;

/**
 * tells the current activity (UI) that the update running in the background
 * service has completed
 * 
 * @author kevin
 */
public class UpdateStatusBroadcast extends BroadcastReceiver {
    // http://stackoverflow.com/questions/20515966/broadcast-receiver-throughtout-the-application
    // want bcast rcvr available to entire app ... will toast complete no matter
    // where we are in the app
    // after starting service
    // http://www.grokkingandroid.com/android-tutorial-broadcastreceiver/
    
    Activity main = null;
    public void setMainActivityHandler(Activity main){
        this.main = main;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Broadcast indeed!");
        String message = intent.getExtras().getString("message");
        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        ((MainActivity) this.main).setComplete(true);
    }
}
