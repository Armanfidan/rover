package com.crossobamon.rover;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ButtonControls extends AppCompatActivity {
    public String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_controls);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //This gets the saved IP address, and puts it in the variable, if not set, then it starts the Setting activity.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ip = preferences.getString("IP", "NONE");
        if (ip.equals("NONE")) {
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
        } else {

            //requestAll();
            //This is when the app is started, you can already retrieve data.
        }/*
        TextView textView = (TextView) findViewById(R.id.ipfield);
        textView.setText(ip);*/
/*
        final Button reload = (Button) findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When coming from the settings page, you need to press the reload button.
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ButtonControls.this);
                ip = preferences.getString("IP", "");
                //requestAll();

            }
        });*/
        final Button forwards = (Button) findViewById(R.id.forwards);
        forwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ButtonControls.RequestTask().execute(ip + "F", "1"); //Change the "GET" string to a string that is checked by the Arduino.
                //The "1" is for the correct textview, if you have more than 1,
                // you can set the correct destination of the output, or what has to be done with it.
            }
        });
        final Button backwards = (Button) findViewById(R.id.backwards);
        backwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ButtonControls.RequestTask().execute(ip + "B", "1"); //Change the "GET" string to a string that is checked by the Arduino.
                //The "1" is for the correct textview, if you have more than 1,
                // you can set the correct destination of the output, or what has to be done with it.
            }
        });
        final Button left = (Button) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ButtonControls.RequestTask().execute(ip + "L", "1"); //Request the volts, put it in the other textview.
                //The "1" is for the correct textview, if you have more than 1,
                // you can set the correct destination of the output, or what has to be done with it.
            }
        });
        final Button right = (Button) findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ButtonControls.RequestTask().execute(ip + "R", "1"); //Request the volts, put it in the other textview.
                //The "1" is for the correct textview, if you have more than 1,
                // you can set the correct destination of the output, or what has to be done with it.
            }
        });
        final Button scan = (Button) findViewById(R.id.scan);
        scan.setOnTouchListener(new RepeatListener(200, 200, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ButtonControls.RequestTask().execute(ip + "SCAN", "1");
            }
        }));

            SeekBar speed = (SeekBar) findViewById(R.id.speed);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar speed, int progress, boolean fromUser) {
                new ButtonControls.RequestTask().execute(ip + "H" + String.valueOf(progress), "1");
            }

            @Override
            public void onStartTrackingTouch(SeekBar speed) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar speed) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //some blabla for creating the menu
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override //Some blablabla for a pressed menu item.
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {//This starts the Setting class.
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //The actual task which requests the data from the Arduino.
//Can be fired with: new RequestTask().execute(String url, String MODE(1 for main label, rest you can change/add));
    class RequestTask extends AsyncTask<String, String, String> {
        private int MODE = 0; //1 == title of song , 2 == volume
        private boolean error = false;

        @Override
        protected String doInBackground(String... uri) {

            MODE = Integer.parseInt(uri[1]);    //Set the mode.

            String responseString;

            //Try to get the data from the LinkIt ONE. Do not change this unless you know what this code does!
            try {
                URLConnection connection = new URL(uri[0]).openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);

                }
                responseString = sb.toString();

                //If something goes wrong.
            } catch (Exception e) {
                error = true;
                responseString = e.getLocalizedMessage();
            }

            return responseString;
        }

        //After requesting the data.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            int hasG = result.indexOf("G");
            int hasN = result.indexOf("N");
            int hasD = result.indexOf("D");
            int hasP = result.indexOf("P");
            int has7 = result.indexOf("7");
            int has3 = result.indexOf("3");

            int type = 0;

            if(hasG != -1) {
                result = "#GAB";
                type = 1;
            }
            if(hasN != -1) {
                result = "#NUC";
                type = 1;
            }
            if(hasD != -1) {
                result = "#DUR";
                type = 1;
            }
            if(has7 != -1) {
                result = "571Hz";
                type = 2;
            }
            if(has3 != -1) {
                result = "#353Hz";
                type = 2;
            }


            TextView scanType = (TextView) findViewById(R.id.scanType);
            TextView scanResult = (TextView) findViewById(R.id.scanResult);

            if(type == 0) {
                scanType.setText("No signal received.");
                scanResult.setText("");
            }
            else if (type == 1) {
                scanType.setText("Radio signal received.");
                scanResult.setText("Modulated message: " + result);
            } else {
                scanType.setText("Infrared signal received.");
                scanResult.setText("Frequency: " + result);
            }

            if (!error) {
                //Everything OK
                if (MODE == 0) {
                    //Fuck, something is not ok, because the MODE is something that can't be.
                    Toast.makeText(ButtonControls.this, "Starting the Async went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                //A catch method caught an error.
                //Toast.makeText(MainActivity.this, "Oops, something went wrong.", Toast.LENGTH_LONG).show();
                //Toast.makeText(MainActivity.this, "Oops, something went wrong. The error is:" + result, Toast.LENGTH_LONG).show();//Use this to see the error.
            }
        }
    }
}
