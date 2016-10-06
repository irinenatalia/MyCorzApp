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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CompletedRequest extends AppCompatActivity {
    ImageButton like,dislike,submit;
    EditText feedback;
    CircleImageView studentBitmap;
    Integer intLike,intDislike;
    String orderID,studentName,profilePicture;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_request);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        TextView tvStudentName = (TextView)findViewById(R.id.completeRequestName);
        studentBitmap = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.completeRequestMentorImage);

        Intent i = getIntent();
        if(i != null) {
            orderID = i.getStringExtra("orderID"); //get orderID to process completed request
            studentName = i.getStringExtra("studentName");
            profilePicture = i.getStringExtra("studentProfile");

            tvStudentName.setText(studentName);

            //check user profile picture is empty or not, if not, retrieve the bitmap
            if(profilePicture.isEmpty() || profilePicture.equalsIgnoreCase("")){
                Bitmap profileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_avatar_upload);
                studentBitmap.setImageBitmap(profileBitmap);
            }
            else{
                getImage(profilePicture);
            }
        }

        feedback = (EditText)findViewById(R.id.completeRequestFeedback); //get feedback
        like = (ImageButton)findViewById(R.id.btnCompletedLike);
        like.setOnClickListener(new View.OnClickListener() { //if like button is pressed
            public void onClick(View v) {
                like.setImageResource(R.drawable.icon_like_pressed);
                dislike.setImageResource(R.drawable.icon_dislike);
                intLike = 1;
                intDislike = 0;
            }
        });
        dislike = (ImageButton)findViewById(R.id.btnCompletedDislike);
        dislike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //if dislike button is pressed
                like.setImageResource(R.drawable.icon_like);
                dislike.setImageResource(R.drawable.icon_dislike_pressed);
                intLike = 0;
                intDislike = 1;
            }
        });
        submit = (ImageButton)findViewById(R.id.btnCompletedRequest);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //if submit button is pressed
                if(TextUtils.isEmpty(feedback.getText().toString()))
                    Toast.makeText(CompletedRequest.this, "Please fill your feedback", Toast.LENGTH_LONG).show();
                else
                    new CompletedRequest.AsyncAction().execute(orderID, "COMPLETED", intLike.toString(), intDislike.toString(), feedback.getText().toString());
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
                loading = ProgressDialog.show(CompletedRequest.this, "", null,true,true);
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

                studentBitmap.setImageBitmap(b);
            }
        }

        GetImage gi = new GetImage();
        gi.execute(profile);
    }

    //submit complete feedback
    private class AsyncAction extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(CompletedRequest.this);
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
                url = new URL("http://vidcom.click/admin/android/reviewRequestActionComplete.php");

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
                        .appendQueryParameter("action", params[1])
                        .appendQueryParameter("like", params[2])
                        .appendQueryParameter("dislike", params[3])
                        .appendQueryParameter("feedback", params[4]);
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
                    Log.v("actionresult","HTTP OK");
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.v("actionresult","result returned");
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
