package com.crossobamon.rover;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    public String ip;       //This is the IP address of the LinkIt ONE.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setTitle("Choose your controls");
        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.

        //This gets the saved IP address, and puts it in the variable, if not set, then it starts the Setting activity.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ip = preferences.getString("IP", "NONE");
        if (ip.equals("NONE")) {
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
        } else {

            //requestAll();
            //This is when the app is started, you can already retrieve data.
        }

        View buttonControls = (View) findViewById(R.id.buttonControls);
        buttonControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ButtonControls.class));
            }
        });
        View sliderControls = (View) findViewById(R.id.sliderControls);
        sliderControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SliderControls.class));
            }
        });

        SeekBar speed1 = (SeekBar) findViewById(R.id.seekBar2);

    }
}
