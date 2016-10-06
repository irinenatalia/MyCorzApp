package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Notification extends AppCompatActivity {
    ListView lvNotification;
    ArrayList<String> picture,message,time,targetEntity,appAction;
    public ProgressBar spinner;
    private SwipeRefreshLayout swipeRefreshLayout;

    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";

    private static final String NOTIF_URL = "http://vidcom.click/admin/android/notification.php?username=";
    User user;
    String email, sProfile;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(Notification.this);
        if(user!=null){
            email = user.email;

            if(user.picture != null)
                sProfile = user.picture;
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.notifSwipeRefreshLayout);
        spinner=(ProgressBar)findViewById(R.id.progressBarNotif);
        spinner.setVisibility(View.VISIBLE);

        picture = new ArrayList<String>();
        message = new ArrayList<String>();
        time = new ArrayList<String>();
        targetEntity = new ArrayList<String>();
        appAction = new ArrayList<String>();

        //set refresh gesture when user swipe down
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("swipehomeresult", "onRefresh called from SwipeRefreshLayout");

                        // Signal SwipeRefreshLayout to start the progress indicator
                        swipeRefreshLayout.setRefreshing(true);

                        message.clear();
                        fetchClass();
                    }
                }
        );

        //fetch class list asynchronously with volley
        fetchClass();

        ImageButton btnHome = (ImageButton)findViewById(R.id.btnNotifBackToHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }
        });
    }
    private void fetchClass(){
        // Creating volley request obj
        StringRequest movieReq = new StringRequest(NOTIF_URL+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("volleyresult", response);
                        if(response.equalsIgnoreCase("") || response.equalsIgnoreCase("false")){
                            spinner.setVisibility(View.GONE);
                            TextView warning = (TextView)findViewById(R.id.warningNotification);
                            warning.setText("There is no available notification right now");
                        }
                        else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                result = jsonObject.getJSONArray(JSON_ARRAY);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            spinner.setVisibility(View.GONE);

                            Log.d("volleyresult", String.valueOf(result.length()));
                            // Parsing json
                            for (int i = 0; i < result.length(); i++) {
                                try {
                                    JSONObject jsonObject = result.getJSONObject(i);
                                    picture.add(sProfile);
                                    message.add(jsonObject.getString("message"));
                                    time.add(jsonObject.getString("time"));
                                    targetEntity.add(jsonObject.getString("targetEntity"));
                                    appAction.add(jsonObject.getString("appAction"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            // stopping swipe refresh
                            swipeRefreshLayout.setRefreshing(false);
                            lvNotification = (ListView) findViewById(R.id.lvNotification);
                            lvNotification.setAdapter(new ListNotification(Notification.this, picture,message,time));
                            // Click event for single list row
                            lvNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    if(appAction.get(position).equalsIgnoreCase("ReviewRequest")){
                                        Intent i = new Intent(Notification.this, ReviewRequest.class);
                                        startActivity(i);
                                    }
                                    if(appAction.get(position).equalsIgnoreCase("RequestClass")){
                                        Intent i = new Intent(Notification.this, RequestClass.class);
                                        startActivity(i);
                                    }
                                    if(appAction.get(position).equalsIgnoreCase("History")){
                                        Intent i = new Intent(Notification.this, History.class);
                                        startActivity(i);
                                    }
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse errorRes = error.networkResponse;
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                //Log.e("VolleyError",errorRes.toString());
                if(errorRes != null && errorRes.data != null){
                    //Log.e("VolleyError",errorRes.toString());
                }

                spinner.setVisibility(View.GONE);
            }
        });

        // Adding request to request queue
        VolleyAppController.getInstance().addToRequestQueue(movieReq);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        spinner.setVisibility(View.GONE);
    }
}
