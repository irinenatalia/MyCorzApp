package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SummaryMentorComplete extends AppCompatActivity {
    TextView category,className,classDescription,ageLevel,skillLevel,date,time,venue,location,mentor,total;
    String sID,sDate,sTime,sAgeLevel,sLocation,sLocationDetail,sClassName;
    String alstudent,alcategory,alclassDescription,alskillLevel,almentor,altotal;
    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";

    private static final String NOTIF_URL = "http://vidcom.click/admin/android/viewSummaryComplete.php?id=";
    User user;
    String email;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_mentor_complete);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(SummaryMentorComplete.this);
        if(user!=null){
            email = user.email;
        }

        category = (TextView)findViewById(R.id.summaryMCCategory);
        className = (TextView)findViewById(R.id.summaryMCClassName);
        classDescription = (TextView)findViewById(R.id.summaryMCClassDesc);
        ageLevel = (TextView)findViewById(R.id.summaryMCAgeLevel);
        skillLevel = (TextView)findViewById(R.id.summaryMCSkillLevel);
        date = (TextView)findViewById(R.id.summaryMCDate);
        time = (TextView)findViewById(R.id.summaryMCTime);
        venue = (TextView)findViewById(R.id.summaryMCVenue);
        location = (TextView)findViewById(R.id.summaryMCLocation);
        mentor = (TextView)findViewById(R.id.summaryMCMentor);
        total = (TextView)findViewById(R.id.summaryMCTotal);

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
        Log.v("mentorresult",sID);
        getSummaryJSON(NOTIF_URL+sID);

        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnMCHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnMCProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnMCSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnMCNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvMCHistory);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvMCProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvMCSetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvMCNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
    }
    private void getSummaryJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SummaryMentorComplete.this, "Loading...",null,true,true);
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
                    return "exception";
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                myJSONString = s;
                Log.v("mentorresult",s);
                if(s.equalsIgnoreCase("false")){
                    Toast.makeText(SummaryMentorComplete.this, "Invalid ID", Toast.LENGTH_LONG).show();
                }
                else if(s.equalsIgnoreCase("exception")){
                    Toast.makeText(SummaryMentorComplete.this, "Connection problem while loading summary detail", Toast.LENGTH_LONG).show();
                }
                else{
                    extractJSON(s);
                }
            }
        }
        getCategoryJSON gj = new getCategoryJSON();
        gj.execute(url);
    }
    private void extractJSON(String s){
        try {
            JSONObject jsonObject = new JSONObject(s);
            result = jsonObject.getJSONArray(JSON_ARRAY);
            showData();
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
            }
            category.setText(alcategory);
            Integer costPlainInt = Integer.parseInt(altotal);
            total.setText("Rp " + String.format("%,d", costPlainInt).replace(',','.'));
            classDescription.setText(alclassDescription);
            skillLevel.setText(alskillLevel);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
