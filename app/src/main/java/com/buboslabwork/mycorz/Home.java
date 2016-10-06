package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {
    private static final String HOME_URL = "http://vidcom.click/admin/android/classList.php?email=";
    ListView lvClass;
    public ProgressBar spinner;
    private SwipeRefreshLayout swipeRefreshLayout;

    String email;
    ArrayList<String> classDate,className,category,ageLevel,mentorUsername,alPicture;
    public JSONArray result = null;
    public String myJSONString,mentor_username;
    private static final String JSON_ARRAY ="result";
    Bitmap bitmap;

    User user;
    TextView username;
    String sUsername;
    CircleImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        picture = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.pictureHome);
        username = (TextView)findViewById(R.id.home_name);

        user=PrefUtils.getCurrentUser(Home.this);
        if(user != null){
            String[] splitUsername = user.name.split("\\s+");
            if(splitUsername.length > 2){
                sUsername = splitUsername[0] + " " + splitUsername[1] + " ";
                for(int i=2; i<splitUsername.length; i++){
                    sUsername += splitUsername[i].substring(0,1);
                }
            }
            else{
                sUsername = user.name;
            }
            username.setText(sUsername);
            email = user.email;

            byte[] b = Base64.decode(user.picture, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            picture.setImageBitmap(bitmap);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.homeSwipeRefreshLayout);
        spinner=(ProgressBar)findViewById(R.id.progressBarHome);
        spinner.setVisibility(View.VISIBLE);

        classDate = new ArrayList<String>();
        className = new ArrayList<String>();
        category = new ArrayList<String>();
        ageLevel = new ArrayList<String>();
        mentorUsername = new ArrayList<String>();
        alPicture = new ArrayList<String>();

        lvClass=(ListView) findViewById(R.id.home_listview); //listview for displaying class list

        //set refresh gesture when user swipe down
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("swipehomeresult", "onRefresh called from SwipeRefreshLayout");

                        // Signal SwipeRefreshLayout to start the progress indicator
                        swipeRefreshLayout.setRefreshing(true);

                        classDate.clear();
                        className.clear();
                        ageLevel.clear();
                        alPicture.clear();
                        mentorUsername.clear();
                        category.clear();
                        fetchClass();
                    }
                }
        );

        //fetch class list asynchronously with volley
        fetchClass();

        Button btnMainMenu = (Button)findViewById(R.id.btnToMainMenu);
        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnHomeHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnHomeProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnHomeSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnHomeNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvHomeHistory);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvHomeProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvHomeSetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvHomeNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
    }

    private void fetchClass(){
        // Creating volley request obj
        StringRequest movieReq = new StringRequest(HOME_URL+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("homevolleyresult", response);

                        if(response.equalsIgnoreCase("") || response.equalsIgnoreCase("false")){
                            spinner.setVisibility(View.GONE);
                            TextView warning = (TextView)findViewById(R.id.warningHome);
                            warning.setText("There is no available class right now");
                        }
                        else{
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                result = jsonObject.getJSONArray(JSON_ARRAY);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            spinner.setVisibility(View.GONE);

                            Log.d("homevolleyresult", String.valueOf(result.length()));
                            // Parsing json
                            for (int i = 0; i < result.length(); i++) {
                                try {
                                    JSONObject jsonObject = result.getJSONObject(i);
                                    category.add(jsonObject.getString("category"));
                                    ageLevel.add(jsonObject.getString("age_level"));
                                    className.add(jsonObject.getString("class_name"));
                                    classDate.add(jsonObject.getString("date"));
                                    mentorUsername.add(jsonObject.getString("mentor_username"));
                                    alPicture.add(jsonObject.getString("picture"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            // stopping swipe refresh
                            swipeRefreshLayout.setRefreshing(false);
                            lvClass.setAdapter(new ListClassHome(Home.this, classDate, className, category, ageLevel, alPicture));
                            // Click event for single list row
                            lvClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    Log.v("homeresult mentor", mentorUsername.get(position));
                                    Log.v("homeresult email", email);

                                    if(mentorUsername.get(position).equalsIgnoreCase(email)){
                                        Intent i = new Intent(getApplicationContext(), MentorDetailClassAsMentor.class);
                                        i.putExtra("mentorUsername",mentorUsername.get(position));
                                        i.putExtra("pageSource","Home");
                                        startActivity(i);
                                    }
                                    else{
                                        Intent i = new Intent(getApplicationContext(), MentorDetailClass.class);
                                        i.putExtra("mentorUsername",mentorUsername.get(position));
                                        i.putExtra("pageSource","Home");
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
                    Log.e("homeerror","connection problem");
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
