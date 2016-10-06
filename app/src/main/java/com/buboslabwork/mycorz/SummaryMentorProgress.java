package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SummaryMentorProgress extends AppCompatActivity {
    TextView category,className,classDescription,ageLevel,skillLevel,date,time,venue,location,mentor,total;
    ImageButton accept,reject,completed; //Action Textview
    String sID,sDate,sTime,sAgeLevel,sLocation,sLocationDetail,sClassName;
    String alstudent,alcategory,alclassDescription,alskillLevel,almentor,altotal,alstatus;
    String alstudentName,almentorName,alstudentProfile,almentorProfile;
    String actionStatus;

    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private static final String NOTIF_URL = "http://vidcom.click/admin/android/viewSummaryProgress.php?id=";
    User user;
    String email;
    public ProgressBar spinner;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_mentor_progress);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(SummaryMentorProgress.this);
        if(user!=null){
            email = user.email;
        }

        spinner=(ProgressBar)findViewById(R.id.progressBarSummaryMP);
        spinner.setVisibility(View.GONE);
        category = (TextView)findViewById(R.id.summaryMPCategory);
        className = (TextView)findViewById(R.id.summaryMPClassName);
        classDescription = (TextView)findViewById(R.id.summaryMPClassDesc);
        ageLevel = (TextView)findViewById(R.id.summaryMPAgeLevel);
        skillLevel = (TextView)findViewById(R.id.summaryMPSkillLevel);
        date = (TextView)findViewById(R.id.summaryMPDate);
        time = (TextView)findViewById(R.id.summaryMPTime);
        venue = (TextView)findViewById(R.id.summaryMPVenue);
        location = (TextView)findViewById(R.id.summaryMPLocation);
        mentor = (TextView)findViewById(R.id.summaryMPMentor);
        total = (TextView)findViewById(R.id.summaryMPTotal);
        //ACTION TEXTVIEW
        accept = (ImageButton)findViewById(R.id.summaryMPAccept);
        reject = (ImageButton)findViewById(R.id.summaryMPReject);
        completed = (ImageButton)findViewById(R.id.summaryMPCompleted);

        //GET CONTENT FROM PREVIOUS INTENT
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sID = extras.getString("id");
            sDate = extras.getString("date");
            sTime = extras.getString("time");
            sAgeLevel = extras.getString("ageLevel");
            sLocation = extras.getString("location");
            sLocationDetail = extras.getString("locationDetail");
            sClassName = extras.getString("className");

            date.setText(sDate);
            time.setText(sTime);
            ageLevel.setText(sAgeLevel);
            venue.setText(sLocation);
            location.setText(sLocationDetail);
            className.setText(sClassName);
        }

        getSummaryJSON(NOTIF_URL+sID);

        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackSummaryMP);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnMPHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnMPProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnMPSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnMPNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvMPHistory);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvMPProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvMPSetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvMPNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });

        //ACTION TEXTVIEW
        accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!alstatus.isEmpty() && alstatus.equalsIgnoreCase("PENDING")) { //if status not accepted
                    Intent i = new Intent(getApplicationContext(), AcceptRequest.class);
                    i.putExtra("orderID", sID);
                    startActivity(i);
                }
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!alstatus.isEmpty() && alstatus.equalsIgnoreCase("PENDING")) { //if status not accepted
                    actionStatus = "REJECTED";
                    new SummaryMentorProgress.AsyncAction().execute(sID, "REJECTED");
                }
            }
        });
        completed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!alstatus.isEmpty() && alstatus.equalsIgnoreCase("ACCEPTED")) {
                    Intent i = new Intent(getApplicationContext(), CompletedRequest.class);
                    i.putExtra("orderID", sID);
                    i.putExtra("studentName", alstudentName);
                    i.putExtra("studentProfile", alstudentProfile);
                    startActivity(i);
                }
            }
        });
    }

    //loading summary asynchronously
    private void getSummaryJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                spinner.setVisibility(View.GONE);

                myJSONString = s;
                if(s.equalsIgnoreCase("") || s.equalsIgnoreCase("false")){
                    Toast.makeText(SummaryMentorProgress.this, "Connection problem while loading summary detail", Toast.LENGTH_LONG).show();
                }
                else{
                    // Parse JSON data to Listview
                    extractJSON();
                    showData();
                }
            }
        }
        getCategoryJSON gj = new getCategoryJSON();
        gj.execute(url);
    }
    private void extractJSON(){
        try {
            JSONObject jsonObject = new JSONObject(myJSONString);
            result = jsonObject.getJSONArray(JSON_ARRAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData(){
        try {
            //productList = new ArrayList<HashMap<String, String>>();
            for(int i=0; i<result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);

                alstudent = jsonObject.getString("student_username");
                altotal = jsonObject.getString("payment");
                almentor = jsonObject.getString("mentor_username");
                alclassDescription = jsonObject.getString("class_description");
                alcategory = jsonObject.getString("category");
                alskillLevel = jsonObject.getString("skill_level");
                alstatus = jsonObject.getString("status");
                alstudentName = jsonObject.getString("student_name");
                almentorName = jsonObject.getString("mentor_name");
                alstudentProfile = jsonObject.getString("student_profile");
                almentorProfile = jsonObject.getString("mentor_profile");
            }
            category.setText(alcategory);
            total.setText(altotal);
            classDescription.setText(alclassDescription);
            skillLevel.setText(alskillLevel);
            mentor.setText(alstudentName);

            if(alstatus.equalsIgnoreCase("PENDING")){
                completed.setImageResource(R.drawable.button_history_completed_grey);
                completed.setClickable(false);
                completed.setEnabled(false);
            }
            else if(alstatus.equalsIgnoreCase("ACCEPTED")){
                accept.setImageResource(R.drawable.button_accept_grey);
                accept.setClickable(false);
                accept.setEnabled(false);

                reject.setImageResource(R.drawable.button_history_reject_grey);
                reject.setClickable(false);
                reject.setEnabled(false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //action for accept, reject, completed, if necessary
    private class AsyncAction extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SummaryMentorProgress.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tProcessing data...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("http://vidcom.click/admin/android/reviewRequestAction.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("orderID", params[0])
                        .appendQueryParameter("action", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{
                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            if(result.equalsIgnoreCase("true"))
            {
                if(actionStatus.equalsIgnoreCase("ACCEPTED")) {
                    Intent i = new Intent(getApplicationContext(), AcceptRequest.class);
                    startActivity(i);
                }
                else if(actionStatus.equalsIgnoreCase("REJECTED")) {
                    Intent i = new Intent(getApplicationContext(), RejectRequest.class);
                    startActivity(i);
                }
                else if(actionStatus.equalsIgnoreCase("COMPLETED")) {
                    Intent i = new Intent(getApplicationContext(), CompletedRequest.class);
                    startActivity(i);
                }
            }else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("")){
                // If username and password does not match display a error message
                Toast.makeText(getApplicationContext(), "Something went wrong when processing your data, please try again.", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(getApplicationContext(), "Something went wrong, connection problem.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
