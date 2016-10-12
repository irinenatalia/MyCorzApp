package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SummaryStudentProgress extends AppCompatActivity {
    TextView category,className,classDescription,ageLevel,skillLevel,date,time,venue,location,mentor,total;
    ImageButton payment,cancel,completed; //Action Textview
    ImageButton btnBack;
    String sID,sDate,sTime,sAgeLevel,sLocation,sLocationDetail,sClassName,sPaymentTime;
    String alstudent,alcategory,alclassDescription,alskillLevel,almentor,alstatus,altotal,altype;
    String alstudentName,almentorName,alstudentProfile,almentorProfile;

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
        setContentView(R.layout.summary_student_progress);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(SummaryStudentProgress.this);
        if(user!=null){
            email = user.email;
        }

        spinner=(ProgressBar)findViewById(R.id.progressBarSummarySP);
        spinner.setVisibility(View.GONE);
        category = (TextView)findViewById(R.id.summarySPCategory);
        className = (TextView)findViewById(R.id.summarySPClassName);
        classDescription = (TextView)findViewById(R.id.summarySPClassDesc);
        ageLevel = (TextView)findViewById(R.id.summarySPAgeLevel);
        skillLevel = (TextView)findViewById(R.id.summarySPSkillLevel);
        date = (TextView)findViewById(R.id.summarySPDate);
        time = (TextView)findViewById(R.id.summarySPTime);
        venue = (TextView)findViewById(R.id.summarySPVenue);
        location = (TextView)findViewById(R.id.summarySPLocation);
        mentor = (TextView)findViewById(R.id.summarySPMentor);
        total = (TextView)findViewById(R.id.summarySPTotal);
        btnBack = (ImageButton)findViewById(R.id.btnBackSummarySP);
        //ACTION TEXTVIEW
        payment = (ImageButton)findViewById(R.id.summarySPPayment);
        cancel = (ImageButton)findViewById(R.id.summarySPCancel);
        completed = (ImageButton)findViewById(R.id.summarySPCompleted);

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
            sPaymentTime = extras.getString("paymentTime");

            date.setText(sDate);
            time.setText(sTime);
            ageLevel.setText(sAgeLevel);
            venue.setText(sLocation);
            location.setText(sLocationDetail);
            className.setText(sClassName);

            //disable button payment already done payment
            if(sPaymentTime.equalsIgnoreCase("") || sPaymentTime.isEmpty()){
                payment.setClickable(true);
                payment.setEnabled(true);
            }
            else{
                payment.setClickable(false);
                payment.setEnabled(false);
            }
        }

        getSummaryJSON(NOTIF_URL+sID);

        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SummaryStudentProgress.this.finish();
            }
        });
        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnSPHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnSPProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnSPSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnSPNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvSPHistory);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvSPProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvSPSetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvSPNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });

        //ACTION TEXTVIEW
        payment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(sPaymentTime.equalsIgnoreCase("") || sPaymentTime.isEmpty()) {
                    Intent i = new Intent(getApplicationContext(), PaymentConfirmation.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("orderID", sID);
                    mBundle.putString("className", sClassName);
                    mBundle.putString("date", sDate);
                    mBundle.putString("location", sLocation);
                    mBundle.putString("time", sTime);
                    mBundle.putString("type", altype);
                    mBundle.putString("age", sAgeLevel);
                    mBundle.putString("skill", alskillLevel);
                    mBundle.putString("cost", altotal);
                    i.putExtras(mBundle);
                    startActivity(i);
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(SummaryStudentProgress.this)
                        .setTitle("Cancel class")
                        .setMessage("Are you sure you want to cancel this class? (Max 2 days before class started)")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new SummaryStudentProgress.AsyncAction().execute(sID, "CANCELED");
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        completed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(alstatus.equalsIgnoreCase("ACCEPTED")) {
                    Intent i = new Intent(getApplicationContext(), CompletedClassStudent.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("orderID", sID);
                    mBundle.putString("className", sClassName);
                    mBundle.putString("date", sDate);
                    mBundle.putString("location", sLocation);
                    mBundle.putString("time", sTime);
                    mBundle.putString("type", altype);
                    mBundle.putString("age", sAgeLevel);
                    mBundle.putString("skill", alskillLevel);
                    mBundle.putString("cost", altotal);
                    mBundle.putString("mentorName", almentorName);
                    mBundle.putString("mentorProfile", almentorProfile);
                    i.putExtras(mBundle);
                    startActivity(i);
                }

            }
        });
    }
    private void getSummaryJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

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
                    Toast.makeText(SummaryStudentProgress.this, "Connection problem while loading summary detail", Toast.LENGTH_LONG).show();
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
            spinner.setVisibility(View.GONE);
            for(int i=0; i<result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);

                alstudent = jsonObject.getString("student_username");
                alstatus = jsonObject.getString("status");
                altotal = jsonObject.getString("payment");
                almentor = jsonObject.getString("mentor_username");
                alclassDescription = jsonObject.getString("class_description");
                alcategory = jsonObject.getString("category");
                alskillLevel = jsonObject.getString("skill_level");
                altype = jsonObject.getString("class_type");
                alstudentName = jsonObject.getString("student_name");
                almentorName = jsonObject.getString("mentor_name");
                alstudentProfile = jsonObject.getString("student_profile");
                almentorProfile = jsonObject.getString("mentor_profile");
            }
            category.setText(alcategory);
            Integer costPlainInt = Integer.parseInt(altotal);
            total.setText("Rp " + String.format("%,d", costPlainInt).replace(',','.'));
            classDescription.setText(alclassDescription);
            skillLevel.setText(alskillLevel);
            mentor.setText(almentorName);

            if(sPaymentTime.equalsIgnoreCase("") || sPaymentTime.isEmpty()) {
                payment.setImageResource(R.drawable.button_history_payment);
                payment.setEnabled(true);
                payment.setClickable(true);
            }
            else{
                payment.setImageResource(R.drawable.button_history_payment_grey);
                payment.setClickable(false);
                payment.setEnabled(false);
            }
            //disable button complete if class status is pending
            if(alstatus.equalsIgnoreCase("PENDING")){
                completed.setImageResource(R.drawable.button_history_completed_grey);
                completed.setClickable(false);
                completed.setEnabled(false);
            }
            else{
                completed.setClickable(true);
                completed.setEnabled(true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class AsyncAction extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SummaryStudentProgress.this);
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
            Log.v("summarystudent",""+result);
            if(result.equalsIgnoreCase("true"))
            {
                Intent i = new Intent(getApplicationContext(), StudentCancelClass.class);
                startActivity(i);
            }else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("")){
                Toast.makeText(getApplicationContext(), "Something went wrong when processing your data, please try again.", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(getApplicationContext(), "Connection problem. Please try again.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
