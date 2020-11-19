package com.crossobamon.rover;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Setting extends AppCompatActivity {
    public ProgressDialog dialog; //The dialog, used when trying to get the data from a IP.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Configure IP address");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final TextView input = findViewById(R.id.ipTF);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String IP = preferences.getString("IP", "");
        input.setText(IP.replace("http://", "").replace("/", "")); //Fill in the previous set IP in the label.

        //When the ok button is clicked, show the dialog and fire of a request.
        Button ok = findViewById(R.id.okB);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(Setting.this, "Checking", "Checking the IP.", false);
                String IP = input.getText().toString();
                IP = "http://" + IP + "/";
                new RequestTask().execute(IP);//Checking if the thing is up
            }
        });

        TextView status = findViewById(R.id.status);
        status.setText("Enter an IP address to connect to:");
    }

    //The request class.
    class RequestTask extends AsyncTask<String, String, String> {
        private String IP = "";//the variable to store the IP address to store in the preferences.
        private boolean error = false;
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... uri) {

            IP = uri[0];    //Store the IP
            String responseString;
            String inputLine;

            try {
                //Create new URL object and open the connection channel
                URL url = new URL(uri[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                connection.connect();


                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }

                reader.close();
                streamReader.close();

                responseString = stringBuilder.toString();
            }
            //Oops, something  went wrong.
            catch (IOException e) {
                e.printStackTrace();
                responseString = null;
            }

            return responseString;

        }
        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();

            //If there isn't an exception.
            if(!error){
                    //Save the IP address.
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Setting.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("IP", IP);
                    editor.apply();
                    Setting.this.finish();
            }
            else
                Toast.makeText(Setting.this, "Oops, something went wrong. Error message: " + result,Toast.LENGTH_LONG).show();
        }
    }
}
