package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MentorDetailTesti extends AppCompatActivity {
    String mentorUsername,pageSource,sProfile,sCompleteName;
    User user;
    ListView lvDetailTesti;
    ArrayList<String> studentUsername,studentTesti,studentRating;
    String email;

    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String TESTI_URL = "http://vidcom.click/admin/android/mentorDetailTestimonial.php?mentor=";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    CircleImageView displayProfile;
    TextView displayName;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mentor_detail_testi);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Log.v("mentordetail","get into mentor testi");

        studentUsername = new ArrayList<String>();
        studentTesti = new ArrayList<String>();
        studentRating = new ArrayList<String>();

        displayProfile = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.mentorDetailTestiPicture);
        displayName = (TextView)findViewById(R.id.nameMentorDetailTesti);

        user=PrefUtils.getCurrentUser(MentorDetailTesti.this);
        if(user != null){
            email = user.email;
        }
        Intent i = getIntent();
        if(i != null){
            mentorUsername = i.getStringExtra("mentorUsername");
            pageSource = i.getStringExtra("pageSource");
            sProfile = i.getStringExtra("profile");
            sCompleteName = i.getStringExtra("completeName");

            displayName.setText(sCompleteName);
            Log.v("mentordetail","username: "+mentorUsername);
            Log.v("mentordetail","name: "+sCompleteName);
            Log.v("mentordetail","profile: "+sProfile);
            Log.v("mentordetail","page source: "+pageSource);
            /*
            if(!sProfile.isEmpty() || !sProfile.equalsIgnoreCase("")){
                byte[] b = Base64.decode(user.picture, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                displayProfile.setImageBitmap(bitmap);
            }*/

            getJSON(TESTI_URL+mentorUsername);
        }

        ImageButton certificate = (ImageButton)findViewById(R.id.mentorDetailTestiData);
        certificate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MentorDetailData.class);
                i.putExtra("mentorUsername", mentorUsername);
                i.putExtra("pageSource", pageSource);
                i.putExtra("completeName", sCompleteName);
                i.putExtra("profile", sProfile);
                startActivity(i);
            }
        });
        ImageButton btnClass = (ImageButton)findViewById(R.id.mentorDetailTestiClass);
        btnClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mentorUsername.equalsIgnoreCase(email)) {
                    Intent i = new Intent(getApplicationContext(), MentorDetailClassAsMentor.class);
                    i.putExtra("mentorUsername", mentorUsername);
                    i.putExtra("pageSource", pageSource);
                    i.putExtra("completeName", sCompleteName);
                    i.putExtra("profile", sProfile);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(getApplicationContext(), MentorDetailClass.class);
                    i.putExtra("mentorUsername", mentorUsername);
                    i.putExtra("pageSource", pageSource);
                    i.putExtra("completeName", sCompleteName);
                    i.putExtra("profile", sProfile);
                    startActivity(i);
                }
            }
        });
        ImageButton chat = (ImageButton)findViewById(R.id.mentorDetailTestiChat);
        chat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MentorDetailChat.class);
                i.putExtra("mentorUsername", mentorUsername);
                i.putExtra("pageSource", pageSource);
                i.putExtra("completeName", sCompleteName);
                i.putExtra("profile", sProfile);
                startActivity(i);
            }
        });
        ImageButton back = (ImageButton)findViewById(R.id.btnBackMentorDetailTesti);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(pageSource.equalsIgnoreCase("Home")) {
                    Intent i = new Intent(getApplicationContext(), Home.class);
                    startActivity(i);
                }
                else if(pageSource.equalsIgnoreCase("MentorList")) {
                    Intent i = new Intent(getApplicationContext(), Home.class);
                    startActivity(i);
                }
                else if(pageSource.equalsIgnoreCase("Mentor")) {
                    Intent i = new Intent(getApplicationContext(), Mentor.class);
                    startActivity(i);
                }
            }
        });
    }

    private void getJSON(String url) {
        class getHomeJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MentorDetailTesti.this, "Loading...",null,true,true);
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
                        while((json = bufferedReader.readLine())!= null){
                            sb.append(json+"\n");
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
                Log.v("detailtestiresult", s);

                myJSONString = s;
                if(myJSONString.equalsIgnoreCase("") || myJSONString.equalsIgnoreCase("false")){
                    TextView warning = (TextView)findViewById(R.id.warningMentorDetailTesti);
                    warning.setText("This mentor doesn't have any testimonials yet.");
                }
                if(myJSONString.equalsIgnoreCase("unsuccessful") || myJSONString.equalsIgnoreCase("exception")){
                    Toast.makeText(MentorDetailTesti.this, "Can't load class list. Please check your internet connection", Toast.LENGTH_LONG).show();
                }
                else {
                    extractJSON(s);
                }
            }
        }
        getHomeJSON gj = new getHomeJSON();
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
            for(int i=0; i<result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);

                studentUsername.add(jsonObject.getString("name"));
                studentTesti.add(jsonObject.getString("review"));
                studentRating.add(jsonObject.getString("rating"));
            }

            lvDetailTesti =(ListView) findViewById(R.id.lvMentorDetailTesti);
            LayoutInflater inflater = getLayoutInflater();
            ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_mentor_detail_testi, lvDetailTesti,
                    false);
            lvDetailTesti.addHeaderView(header, null, false);

            lvDetailTesti.setAdapter(new ListMentorDetailTesti(this, studentUsername,studentTesti,studentRating));
            // Click event for single list row
            lvDetailTesti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //when listview is clicked, do nothing
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
