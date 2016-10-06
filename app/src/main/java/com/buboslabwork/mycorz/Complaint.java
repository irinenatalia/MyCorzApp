package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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

public class Complaint extends AppCompatActivity {
    String orderID,className,date,location,time,ageLevel,skillLevel,cost;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    User user;

    TextView claimNo,claimYes;
    TextView tvName,tvDate,tvTime,tvLocation,tvAgeLevel,tvSkillLevel,tvCost;
    EditText editReason;
    Integer claimFlag; // 0 = no claim, 1 = claim

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        claimFlag = 0;
        editReason = (EditText)findViewById(R.id.complaintReason);
        tvName = (TextView)findViewById(R.id.tvComplaintClassName);
        tvDate = (TextView)findViewById(R.id.tvComplaintDate);
        tvTime = (TextView)findViewById(R.id.tvComplaintTime);
        tvLocation = (TextView)findViewById(R.id.tvComplaintLocation);
        tvAgeLevel = (TextView)findViewById(R.id.tvComplaintAgeLevel);
        tvSkillLevel = (TextView)findViewById(R.id.tvComplaintSkillLevel);
        tvCost = (TextView)findViewById(R.id.tvComplaintCost);

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

            tvName.setText(className);
            tvDate.setText(date);
            tvTime.setText(time);
            tvLocation.setText(location);
            tvAgeLevel.setText(ageLevel);
            tvSkillLevel.setText(skillLevel);
            tvCost.setText(cost);
        }

        claimNo = (TextView)findViewById(R.id.complaintClaimNo);
        claimYes = (TextView)findViewById(R.id.complaintClaimYes);
        claimNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                claimNo.setText("√ No");
                claimYes.setText("○ Yes (50%)");
                claimFlag = 0;
            }
        });
        claimYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                claimNo.setText("○ No");
                claimYes.setText("√ Yes (50%)");
                claimFlag = 1;
            }
        });
        Button submit = (Button)findViewById(R.id.btnComplaintSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(editReason.getText().toString().isEmpty())
                    Toast.makeText(Complaint.this, "please fill the empty reason", Toast.LENGTH_SHORT).show();
                else
                    new Complaint.AsyncAction().execute(orderID, claimFlag.toString(), editReason.getText().toString());
            }
        });
        ImageButton btnBack = (ImageButton)findViewById(R.id.btnBackComplaint);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Complaint.this.finish();
            }
        });
        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnComplaintHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnComplaintProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnComplaintSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnComplaintNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvComplaintHistory);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvComplaintProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvComplaintSetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvComplaintNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
    }

    private class AsyncAction extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Complaint.this);
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
                url = new URL("http://vidcom.click/admin/android/studentComplain.php");

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
                        .appendQueryParameter("claimFlag", params[1])
                        .appendQueryParameter("reason", params[2]);
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
