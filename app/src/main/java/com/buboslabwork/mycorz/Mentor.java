package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Mentor extends AppCompatActivity {
    private static final String WISHLIST_URL = "http://vidcom.click/admin/android/viewWishlist.php";

    User user;
    String email;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(Mentor.this);
        if(user!=null){
            email = user.email;
        }

        ImageButton backtohome = (ImageButton)findViewById(R.id.mentorBackToMain);
        backtohome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        ImageButton btnSetClass = (ImageButton)findViewById(R.id.btnSetClass);
        btnSetClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SetClass.class);
                i.putExtra("pageSource","");
                startActivity(i);
            }
        });
        ImageButton btnMyClass = (ImageButton)findViewById(R.id.btnMyClass);
        btnMyClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MentorDetailClassAsMentor.class);
                i.putExtra("mentorUsername",email);
                i.putExtra("pageSource","Mentor");
                startActivity(i);
            }
        });
        ImageButton btnReviewRequest = (ImageButton)findViewById(R.id.btnReviewRequest);
        btnReviewRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //getReviewRequestJSON(REVIEW_URL+email);
                Intent i = new Intent(getApplicationContext(), ReviewRequest.class);
                startActivity(i);
            }
        });
        ImageButton btnWishlist = (ImageButton)findViewById(R.id.btnWishlist);
        btnWishlist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getWishlistJSON(WISHLIST_URL);
                //Intent i = new Intent(getApplicationContext(), Wishlist.class);
                //startActivity(i);
            }
        });
        ImageButton btnRequestClass = (ImageButton)findViewById(R.id.btnRequestClass);
        btnRequestClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //getRequestClassJSON(REQUEST_URL+email);
                Intent i = new Intent(getApplicationContext(), RequestClass.class);
                startActivity(i);
            }
        });
        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnMentorHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnMentorProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnMentorSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnMentorNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvMentorHistory);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvMentorProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvMentorSetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvMentorNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
    }
    private void getReviewRequestJSON(String url) {
        class getReviewRequestJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Mentor.this, "Loading...",null,true,true);
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                URL url = null;

                try {
                    url = new URL(uri);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "exception";
                }
                try {
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    int response_code = con.getResponseCode();

                    // Check if successful connection made
                    if (response_code == HttpURLConnection.HTTP_OK) {
                        StringBuilder sb = new StringBuilder();
                        bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                        String json;
                        while ((json = bufferedReader.readLine()) != null) {
                            sb.append(json + "\n");
                        }
                        return sb.toString().trim();
                    }else{
                        return("unsuccessful");
                    }
                }catch(Exception e){
                    return "exception";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Log.v("mentorresult", s);
                if(s.equalsIgnoreCase("unsuccessful") || s.equalsIgnoreCase("exception")){
                    Toast.makeText(Mentor.this, "Can't load review request. Please check your internet connection", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(getApplicationContext(), ReviewRequest.class);
                    i.putExtra("REVIEW_JSON", s);
                    startActivity(i);
                }
            }
        }
        getReviewRequestJSON gj = new getReviewRequestJSON();
        gj.execute(url);
    }
    private void getWishlistJSON(String url) {
        class GetWishlistJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Mentor.this, "Loading...",null,true,true);
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    int response_code = con.getResponseCode();

                    // Check if successful connection made
                    if (response_code == HttpURLConnection.HTTP_OK) {
                        StringBuilder sb = new StringBuilder();
                        bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                        String json;
                        while ((json = bufferedReader.readLine()) != null) {
                            sb.append(json + "\n");
                        }
                        return sb.toString().trim();
                    }else{
                        return("unsuccessful");
                    }
                }catch(Exception e){
                    return("exception");
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Log.v("mentorresult", s);

                if(s.equalsIgnoreCase("unsuccessful") || s.equalsIgnoreCase("exception")){
                    Toast.makeText(Mentor.this, "Can't load wishlist. Please check your internet connection", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(getApplicationContext(), Wishlist.class);
                    i.putExtra("WISHLIST_JSON", s);
                    startActivity(i);
                }
            }
        }
        GetWishlistJSON gj = new GetWishlistJSON();
        gj.execute(url);
    }

    private void getRequestClassJSON(String url) {
        class getRequestClassJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Mentor.this, "Loading...",null,true,true);
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    int response_code = con.getResponseCode();

                    // Check if successful connection made
                    if (response_code == HttpURLConnection.HTTP_OK) {
                        StringBuilder sb = new StringBuilder();
                        bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                        String json;
                        while ((json = bufferedReader.readLine()) != null) {
                            sb.append(json + "\n");
                        }
                        return sb.toString().trim();
                    }else{
                        return("unsuccessful");
                    }
                }catch(Exception e){
                    return("exception");
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Log.v("mentorresult", s);

                if(s.equalsIgnoreCase("unsuccessful") || s.equalsIgnoreCase("exception")){
                    Toast.makeText(Mentor.this, "Can't load class request. Please check your internet connection", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(getApplicationContext(), RequestClass.class);
                    i.putExtra("REQUEST_JSON", s);
                    startActivity(i);
                }
            }
        }
        getRequestClassJSON gj = new getRequestClassJSON();
        gj.execute(url);
    }

}
