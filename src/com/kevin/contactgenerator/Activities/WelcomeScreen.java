package com.kevin.contactgenerator.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import com.example.contactgenerator.R;

/**
 * intro to the app - display logo and then 
 * switch to main activity
 * 
 * @author kevin
 * @version 1.0 7/8/2014
 */
public class WelcomeScreen extends Activity {

    TextView welcomeLabel;
    Intent i;

    /**
     * onCreate - display welcome message and call method to change to main
     * activity after a short pause
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        welcomeLabel = (TextView) findViewById(R.id.tv);
        welcomeLabel.setTextColor(Color.parseColor("#2F4F2F"));

        change();
    }

    /**
     * start main activity after a short pause (~5 secs) (displays logo)
     */
    void change() {

        i = new Intent(WelcomeScreen.this, MainActivity.class);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }

}
