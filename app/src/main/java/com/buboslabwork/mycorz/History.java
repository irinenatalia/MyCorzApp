package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.buboslabwork.mycorz.FragmentAdapter.SubTabAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class History extends FragmentActivity {
    private static final String ONGOING_URL = "http://vidcom.click/admin/android/viewOngoingClass.php?mentor=";
    private static final String COMPLETED_URL = "http://vidcom.click/admin/android/viewCompletedClass.php?mentor=";

    public ProgressBar spinner;
    String ongoingJSON;
    String completedJSON;
    User user;
    String email;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(History.this);
        if(user!=null){
            email = user.email;
            getOngoingClassJSON(ONGOING_URL+email);
        }

        spinner=(ProgressBar)findViewById(R.id.progressBarHistory);
        spinner.setVisibility(View.GONE);

        ImageButton btnHome = (ImageButton)findViewById(R.id.btnHistoryBackToHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnHistoryProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnHistorySetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnHistoryNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvHistoryProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvHistorySetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvHistoryNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
    }
    private void getOngoingClassJSON(String url) {
        class getReviewRequestJSON extends AsyncTask<String, String, String> {
            ProgressDialog pdLoading = new ProgressDialog(History.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading.setMessage("\tProcessing data...");
                pdLoading.setCancelable(false);
                pdLoading.show();
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
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();
                ongoingJSON = s;
                getCompletedClassJSON(COMPLETED_URL+email);
            }
        }
        getReviewRequestJSON gj = new getReviewRequestJSON();
        gj.execute(url);
    }
    private void getCompletedClassJSON(String url) {
        class getReviewRequestJSON extends AsyncTask<String, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(History.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading.setMessage("\tProcessing data...");
                pdLoading.setCancelable(false);
                pdLoading.show();
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
                pdLoading.dismiss();
                completedJSON = s;

                if(ongoingJSON.isEmpty() || completedJSON.isEmpty()){
                    Toast.makeText(History.this, "No content available", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Get the ViewPager and set it's PagerAdapter so that it can display items
                    ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
                    viewPager.setAdapter(new SubTabAdapter(getSupportFragmentManager(),
                            History.this, ongoingJSON, completedJSON, email));

                    // Give the TabLayout the ViewPager
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
                    tabLayout.setupWithViewPager(viewPager);
                }
            }
        }
        getReviewRequestJSON gj = new getReviewRequestJSON();
        gj.execute(url);
    }
}
