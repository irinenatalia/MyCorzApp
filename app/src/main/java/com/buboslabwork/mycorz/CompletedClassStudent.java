package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CompletedClassStudent extends AppCompatActivity {
    RatingBar ratingBar;
    Float ratingNum;
    EditText feedback;
    CircleImageView mentorBitmap;

    String orderID,className,date,location,time,ageLevel,skillLevel,cost,mentorName,mentorProfile;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_class_student);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        TextView tvMentorName = (TextView)findViewById(R.id.studentCompleteName);

        Intent i = getIntent();
        if(i != null){
            orderID = i.getStringExtra("orderID");
            className = i.getStringExtra("className");
            date = i.getStringExtra("date");
            location = i.getStringExtra("location");
            time = i.getStringExtra("time");
            ageLevel = i.getStringExtra("age");
            skillLevel = i.getStringExtra("skill");
            cost = i.getStringExtra("cost");
            mentorName = i.getStringExtra("mentorName");
            mentorProfile = i.getStringExtra("mentorProfile");

            tvMentorName.setText(mentorName);
        }

        mentorBitmap = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.studentCompleteMentorImage);
        if(mentorProfile.isEmpty() || mentorProfile.equalsIgnoreCase("")){
            Bitmap profileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_avatar_upload);
            mentorBitmap.setImageBitmap(profileBitmap);
        }
        else{
            getImage(mentorProfile);
        }

        feedback = (EditText)findViewById(R.id.studentCompleteFeedback);

        ratingBar = (RatingBar) findViewById(R.id.studentCompleteRatingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                ratingNum = rating;
                Log.v("RATING",ratingNum.toString());
            }
        });
        TextView complaint = (TextView)findViewById(R.id.studentCompleteComplaint);
        complaint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Complaint.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("orderID", orderID);
                mBundle.putString("className", className);
                mBundle.putString("date", date);
                mBundle.putString("location", location);
                mBundle.putString("time", time);
                mBundle.putString("age", ageLevel);
                mBundle.putString("skill", skillLevel);
                mBundle.putString("cost", cost);
                i.putExtras(mBundle);
                startActivity(i);
            }
        });
        ImageButton submit = (ImageButton)findViewById(R.id.btnStudentComplete);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(feedback.getText().toString().isEmpty())
                    Toast.makeText(CompletedClassStudent.this, "please fill the empty feedback", Toast.LENGTH_SHORT).show();
                else
                    new CompletedClassStudent.AsyncAction().execute(orderID, ratingNum.toString(), feedback.getText().toString());
            }
        });
    }
    //get mentor profile image
    private void getImage(String profile) {
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CompletedClassStudent.this, "", null,true,true);
            }
            @Override
            protected Bitmap doInBackground(String... params) {
                String profile = params[0];
                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(profile);
                    image = BitmapFactory.decodeStream((InputStream)url.getContent());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }
            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                loading.dismiss();

                mentorBitmap.setImageBitmap(b);
            }
        }

        GetImage gi = new GetImage();
        gi.execute(profile);
    }

    //submit complete feedback
    private class AsyncAction extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(CompletedClassStudent.this);
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
                url = new URL("http://vidcom.click/admin/android/studentCompletedClass.php");

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
                        .appendQueryParameter("rating", params[1])
                        .appendQueryParameter("feedback", params[2]);
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
            Log.v("actionresult",result);
            if(result.equalsIgnoreCase("true"))
            {
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("")){
                // If username and password does not match display a error message
                Toast.makeText(getApplicationContext(), "Something went wrong when processing your data, please try again.", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(getApplicationContext(), "Something went wrong, connection problem.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
