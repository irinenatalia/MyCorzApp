package com.buboslabwork.mycorz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddCertificate extends AppCompatActivity {
    EditText skill;
    String sSkill,email;
    String returnedCategory = "";
    EditText certificate,yearCertificate;
    private static final Integer CATEGORY_INTEGER_VALUE = 11;
    User user;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificate);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(AddCertificate.this);
        if(user != null){
            email = user.email;
        }

        skill = (EditText)findViewById(R.id.addSkillCategory);
        certificate = (EditText) findViewById(R.id.addCertName);
        yearCertificate = (EditText) findViewById(R.id.addCertYear);

        Intent i = getIntent();
        if(i!=null){
            sSkill = i.getStringExtra("category");
            if(!TextUtils.isEmpty(sSkill)){
                skill.setText(sSkill);
            }
        }

        skill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TempCategory.class);
                startActivityForResult(i, CATEGORY_INTEGER_VALUE);
            }
        });

        ImageButton btnBack = (ImageButton)findViewById(R.id.btnBackAddCert);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddCertificate.this.finish();
            }
        });

        ImageButton btnSave = (ImageButton)findViewById(R.id.btnAddCertSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!skill.getText().toString().isEmpty() && !certificate.getText().toString().isEmpty() && !yearCertificate.getText().toString().isEmpty()){
                    new AddCertificate.AsyncSave().execute(skill.getText().toString(),certificate.getText().toString(),yearCertificate.getText().toString(),email);
                }
                else{
                    Toast.makeText(AddCertificate.this, "Please fill the empty field", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(AddCertificate.this, "Please fill the empty field", Toast.LENGTH_SHORT).show();
            }
        });
        Button btnSave2 = (Button)findViewById(R.id.btnAddCertSave2);
        btnSave2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!skill.getText().toString().isEmpty() && !certificate.getText().toString().isEmpty() && !yearCertificate.getText().toString().isEmpty()){
                    new AddCertificate.AsyncSave().execute(skill.getText().toString(),certificate.getText().toString(),yearCertificate.getText().toString(),email);
                }
                else{
                    Toast.makeText(AddCertificate.this, "Please fill the empty field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATEGORY_INTEGER_VALUE && resultCode == SetClass.RESULT_OK) {
            returnedCategory = data.getStringExtra("returnedCategoryParam");
            skill.setText(returnedCategory);
        }
    }
    private class AsyncSave extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(AddCertificate.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://vidcom.click/admin/android/addCertificate.php");

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
                        .appendQueryParameter("skill", params[0])
                        .appendQueryParameter("certificate", params[1])
                        .appendQueryParameter("year", params[2])
                        .appendQueryParameter("email", params[3]);
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

            Log.v("certificateresult", result);
            if(result.equalsIgnoreCase("true"))
            {
                Intent i = new Intent(getApplicationContext(), Certificate.class);
                startActivity(i);
            }else if (result.equalsIgnoreCase("false")){
                // If username and password does not match display a error message
                Toast.makeText(AddCertificate.this, "Error when saving your certificate, please try again.", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(AddCertificate.this, "Connection problem. Please try again.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
