package com.kevin.contactgenerator.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kevin.contactgenerator.Activities.MainActivity;

//http://stackoverflow.com/questions/20515966/broadcast-receiver-throughtout-the-application
//want bcast rcvr available to entire app ... will toast complete no matter where we are in the app 
//after starting service

//http://www.grokkingandroid.com/android-tutorial-broadcastreceiver/
public class UpdateStatusBroadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Broadcast indeed!");
        String message = intent.getExtras().getString("message");
        Toast.makeText(context, message, Toast.LENGTH_LONG)
                .show();
    }
}
