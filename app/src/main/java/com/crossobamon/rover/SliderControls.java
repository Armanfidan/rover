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

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class SliderControls extends AppCompatActivity {
    public String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_controls);
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
        }
/*
        TextView textView = (TextView) findViewById(R.id.ipfield);
        textView.setText(ip);
*/

       /* final Button reload = (Button) findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When coming from the settings page, you need to press the reload button.
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SliderControls.this);
                ip = preferences.getString("IP", "");
                //requestAll();
            }
        });*/

        final Button scan = (Button) findViewById(R.id.scan);
        scan.setOnTouchListener(new RepeatListener(250, 250, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SliderControls.RequestTask().execute(ip + "SCAN", "1");
            }
        }));

        final SeekBar leftMotor = (SeekBar) findViewById(R.id.leftMotor);
        leftMotor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar speed, int progress, boolean fromUser) {
                new SliderControls.RequestTask().execute(ip + "J" + String.valueOf(progress), "1");
            }

            @Override
            public void onStartTrackingTouch(SeekBar speed) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar speed) {
                leftMotor.setProgress(4);
            }
        });

        final SeekBar rightMotor = (SeekBar) findViewById(R.id.rightMotor);
        rightMotor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar speed, int progress, boolean fromUser) {
                new SliderControls.RequestTask().execute(ip + "K" + String.valueOf(progress), "1");
            }

            @Override
            public void onStartTrackingTouch(SeekBar speed) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar speed) {
                rightMotor.setProgress(4);
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //some blabla for creating the menu
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
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
    class RequestTask extends AsyncTask<String, String, String> {
        private int MODE = 0;
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
                String lastLine = "";
                while ((output = br.readLine()) != null) {
                    sb.append(output + "\n");
                    lastLine = output;


                }
//                responseString = sb.toString();
                    responseString = lastLine;
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
            int hasB = result.indexOf("B");
            int hasA = result.indexOf("A");
            int hasN = result.indexOf("N");
            int hasC = result.indexOf("C");
            int hasD = result.indexOf("D");
            int hasP = result.indexOf("P");
            int hasE = result.indexOf("E");
            int has7 = result.indexOf("7");
            int has3 = result.indexOf("3");

            String lizardName = "";

            int type = 0;

            if(hasG != -1 || hasA != -1 || hasB != -1 ) {
                result = "#GAB";
                type = 1;
                lizardName = "Gaborus";
            }
            if(hasN != -1 || hasC != -1) {
                result = "#NUC";
                type = 1;
                lizardName = "Nucinkius";
            }
            if(hasD != -1) {
                result = "#DUR";
                type = 1;
                lizardName = "Durranis";
            }
            if(hasP != -1 || hasE != -1) {
                result = "#PER";
                type = 1;
                lizardName = "Pereai";
            }
            if(has7 != -1) {
                result = "571Hz";
                type = 2;
                lizardName = "Yeatmana";
            }
            if(has3 != -1) {
                result = "353Hz";
                type = 2;
                lizardName = "Cheungus";
            }

            TextView scanType = (TextView) findViewById(R.id.scanType);
            TextView scanResult = (TextView) findViewById(R.id.scanResult);
            TextView lizardname = (TextView) findViewById(R.id.lizardName);

            if(type == 0) {
                scanType.setText("No signal received.");
                scanResult.setText("");
                lizardname.setText("");
            }
            else if (type == 1) {
                    scanType.setText("Radio signal received.");
                    scanResult.setText("Modulated message: " + result);
                    lizardname.setText("Lizard: " + lizardName);

            } else {
                    scanType.setText("Infrared signal received.");
                    scanResult.setText("Frequency: " + result);
                    lizardname.setText("Lizard: " + lizardName);
            }

            if (!error) {
                //Everything OK
                if (MODE == 0) {
                    //The MODE is something that can't be.
                    Toast.makeText(SliderControls.this, "Starting the Async went wrong", Toast.LENGTH_LONG).show();
                }
                if (MODE == 1) {
                }

            } else {
                //A catch method caught an error.
                //Toast.makeText(MainActivity.this, "Oops, something went wrong.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
