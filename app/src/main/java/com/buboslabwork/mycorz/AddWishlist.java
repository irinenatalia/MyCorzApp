package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddWishlist extends AppCompatActivity {
    EditText category,detail;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    String paramCategory;
    User user;
    String username;
    private static final String CATEGORY_URL = "http://vidcom.click/admin/android/viewCategory.php";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wishlist);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(AddWishlist.this);
        if(user != null){
            username = user.email;
        }

        category = (EditText)findViewById(R.id.addWishlistCategory);
        detail = (EditText)findViewById(R.id.addWishlistDetail);

        Intent i = new Intent();
        if(i!=null && i.getStringExtra("category") != null){
            paramCategory = i.getStringExtra("category");
            category.setText(paramCategory);
        }

        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackAddWishlist);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ClassEmpty.class);
                startActivity(i);
            }
        });
        ImageButton addWishlist = (ImageButton)findViewById(R.id.btnAddWishlist);
        addWishlist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TextUtils.isEmpty(category.getText().toString()) || TextUtils.isEmpty(detail.getText().toString())) {
                    Toast.makeText(AddWishlist.this, "Please fill in the field", Toast.LENGTH_LONG).show();
                }
                else {
                    new AddWishlist.AsyncLogin().execute(username, category.getText().toString(), detail.getText().toString());
                }
            }
        });
    }
    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(AddWishlist.this);
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
                url = new URL("http://vidcom.click/admin/android/addWishlist.php");

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
                        .appendQueryParameter("email", params[0])
                        .appendQueryParameter("category", params[1])
                        .appendQueryParameter("detail", params[2]);
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

            if(result.equalsIgnoreCase("true"))
            {
                Toast.makeText(AddWishlist.this, "You wishlist has been submitted", Toast.LENGTH_LONG).show();
                getCategoryJSON(CATEGORY_URL);

            }else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("")){
                Toast.makeText(AddWishlist.this, "Error in querying your wishlist", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(AddWishlist.this, "Something went wrong, connection problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
    private void getCategoryJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddWishlist.this, "Loading...",null,true,true);
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
                //Toast.makeText(PreMainActivity.this, s, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), Category.class);
                i.putExtra("CATEGORY_JSON", s);
                startActivity(i);
            }
        }
        getCategoryJSON gj = new getCategoryJSON();
        gj.execute(url);
    }
}
