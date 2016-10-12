package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UpdateCertificate extends AppCompatActivity {
    String skill,email;
    String certificate = "";
    String yearCertificate = "";
    ArrayList<String> alSkill,alCertificate,alYearCertificate;
    ListView lvUpdateCertificate;
    ImageButton imgButtonSave;
    Button btnSave;
    User user;

    ProgressDialog loading;
    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String CERTIFICATE_URL = "http://vidcom.click/admin/android/viewUpdateCertificate.php?user=";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_certificate);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(UpdateCertificate.this);
        if(user!=null) {
            email = user.email;
        }

        alSkill = new ArrayList<String>();
        alCertificate = new ArrayList<String>();
        alYearCertificate = new ArrayList<String>();

        Intent intent = getIntent();
        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if(extras != null){
            skill = extras.getString("category");

            String urlSkill = skill.replace("&", "%26").replace(' ','_');
            getJSON(CERTIFICATE_URL+email+"&skill="+urlSkill);
        }

        lvUpdateCertificate = (ListView)findViewById(R.id.lvUpdateCert);
        ImageButton btnBack = (ImageButton)findViewById(R.id.btnBackUpdateCert);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UpdateCertificate.this.finish();
            }
        });
        imgButtonSave = (ImageButton)findViewById(R.id.btnUpdateCertSave);
        imgButtonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new UpdateCertificate.AsyncRequest().execute(skill,certificate,yearCertificate,email);
            }
        });
        btnSave = (Button)findViewById(R.id.btnUpdateCertSave2);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String joinedCertificate = TextUtils.join(";", alCertificate);
                String joinedYear = TextUtils.join(";", alYearCertificate);
                Log.v("certificatelog", "certificate: "+joinedCertificate);
                Log.v("certificatelog", "year: "+joinedYear);
                new UpdateCertificate.AsyncRequest().execute(skill,joinedCertificate,joinedYear,email);
            }
        });
    }
    private void getJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UpdateCertificate.this, "Loading...",null,true,true);
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
                loading.dismiss();

                Log.v("certificateresult", s);
                if(s.equalsIgnoreCase("") || s.equalsIgnoreCase("false")){
                }
                else{
                    // Parse JSON data to Listview
                    extractJSON(s);
                }
            }
        }
        getCategoryJSON gj = new getCategoryJSON();
        gj.execute(url);
    }
    //end of certificate loading

    private void extractJSON(String myJSONString){
        try {
            JSONObject jsonObject = new JSONObject(myJSONString);
            result = jsonObject.getJSONArray(JSON_ARRAY);
            showData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData() {
        try {
            for (int i = 0; i < result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);

                alSkill.add(jsonObject.getString("skill"));
                alCertificate.add(jsonObject.getString("certificate"));
                alYearCertificate.add(jsonObject.getString("year"));

                certificate += jsonObject.getString("certificate")+";";
                yearCertificate += jsonObject.getString("year")+";";
            }
            lvUpdateCertificate.setAdapter(new ListUpdateCertificate(UpdateCertificate.this, alSkill, alCertificate, alYearCertificate));
            // Click event for single list row
            lvUpdateCertificate.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                }
            });
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ASYNCTASK FOR SENDING DATA
    private class AsyncRequest extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(UpdateCertificate.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tSubmitting...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("http://vidcom.click/admin/android/updateCertificate.php");

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
            Log.v("certificateresult",result);

            if(result.equalsIgnoreCase("true"))
            {
                Toast.makeText(UpdateCertificate.this, "Your certificate has been updated", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), Certificate.class);
                startActivity(i);
            }else if (result.equalsIgnoreCase("false")){
                // If username and password does not match display a error message
                Toast.makeText(UpdateCertificate.this, "Can't update your certificate, please try again", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(UpdateCertificate.this, "Something went wrong, connection problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}
