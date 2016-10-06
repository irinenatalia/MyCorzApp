package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    CallbackManager callbackManager;
    LoginButton loginButton;
    ImageButton fbImageButton;
    User user;

    EditText email,password;
    String sFacebookID,sEmail,sPassword,sCompleteName,sProfile,sBirthDate,sAddress,sSummary;

    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(Login.this);
        if(user != null){
            Intent homeIntent = new Intent(Login.this, Home.class);
            startActivity(homeIntent);

            finish();
        }

        email = (EditText) findViewById(R.id.loginEmail);
        password = (EditText) findViewById(R.id.loginPassword);

        fbImageButton = (ImageButton)findViewById(R.id.fbImageButton);
        loginButton = (LoginButton) findViewById(R.id.login_facebook);
        //loginButton.setBackgroundResource(R.drawable.button_facebook_login);
        //loginButton.setText("");
        //loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,GraphResponse response) {

                                Log.e("response: ", response + "");
                                try {
                                    sFacebookID = object.getString("id").toString();
                                    sEmail = object.getString("email").toString();
                                    sCompleteName = object.getString("name").toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new Login.AsyncFBLogin().execute();
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this, "Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(Login.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        });

        fbImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        ImageButton btnLoginNormal = (ImageButton)findViewById(R.id.btnLogin);
        btnLoginNormal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(Login.this, "Please fill in the field", Toast.LENGTH_LONG).show();
                }
                else {
                    new Login.AsyncLogin().execute(email.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
    public void forgotPassword(View v){
        Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
        startActivity(i);
    }
    public void register(View v){
        Intent i = new Intent(getApplicationContext(), Register.class);
        startActivity(i);
    }
    // fetching facebook's profile picture
    private class AsyncFBLogin extends AsyncTask<Void,Void,Void>{
        Bitmap bitmap;
        @Override
        protected Void doInBackground(Void... params) {
            URL imageURL = null;
            try {
                imageURL = new URL("https://graph.facebook.com/" + sFacebookID + "/picture?type=large");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                bitmap  = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteImage = baos.toByteArray();

            String encodedImage = Base64.encodeToString(byteImage, Base64.DEFAULT);
            user = new User();
            user.facebookID = sFacebookID;
            user.email = sEmail;
            user.name = sCompleteName;
            user.picture = encodedImage;
            user.method = "facebook";
            PrefUtils.setCurrentUser(user, Login.this);

            new AsyncRegister().execute(sCompleteName,sEmail,"");
            /*
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
            finish();
            */
        }
    }
    //register via MyCorz
    private class AsyncRegister extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Login.this);
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
                url = new URL("http://vidcom.click/admin/android/register.php");

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
                        .appendQueryParameter("name", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("password", params[2]);
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

            Log.v("registerresult", result);
            if(result.equalsIgnoreCase("true"))
            {
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }else if (result.equalsIgnoreCase("false")){
                // If username and password does not match display a error message
                Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("registered")) {
                //if email is already registered, continue log in
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(Login.this, "Connection problem. Please try to log in via again.", Toast.LENGTH_LONG).show();
            }
        }

    }
    //LOGIN BY MYCORZ
    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Login.this);
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
                url = new URL("http://vidcom.click/admin/android/login.php");

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
                        .appendQueryParameter("password", params[1]);
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
                    StringBuilder sb = new StringBuilder();
                    String json;
                    while((json = reader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

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
            Log.v("loginresult",result);

            if (result.equalsIgnoreCase("false")){
                // If username and password does not match display a error message
                Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(Login.this, "Connection problem. Please try login again", Toast.LENGTH_LONG).show();
            }
            else {
                extractJSON(result);
            }
        }
    }
    private void extractJSON(String myJSONString){
        try {
            JSONObject jsonObject = new JSONObject(myJSONString);
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

                sCompleteName = jsonObject.getString("complete_name");
                sProfile = jsonObject.getString("profile");
                sBirthDate = jsonObject.getString("birth_date");
                sSummary = jsonObject.getString("summary");
                sAddress = jsonObject.getString("address");
            }

            if(sProfile.isEmpty() || sProfile.equalsIgnoreCase("") || sProfile == null) {
                Bitmap profileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_avatar_upload);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                profileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteImage = baos.toByteArray();

                String encodedImage = Base64.encodeToString(byteImage, Base64.DEFAULT);
                user = new User();
                user.email = email.getText().toString();
                user.password = password.getText().toString();
                user.name = sCompleteName;
                user.birthDate = sBirthDate;
                user.summary = sSummary;
                user.address = sAddress;
                user.picture = encodedImage;
                user.method = "mycorz";
                PrefUtils.setCurrentUser(user,Login.this);

                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }
            else if(sProfile.length() > 0){
                Log.v("loginresult",sProfile);
                getImage(sProfile);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void getImage(String profile) {
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Login.this, "", null,true,true);
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
                //imageView.setImageBitmap(b);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteImage = baos.toByteArray();

                String encodedImage = Base64.encodeToString(byteImage, Base64.DEFAULT);

                user = new User();
                user.email = email.getText().toString();
                user.password = password.getText().toString();
                user.name = sCompleteName;
                user.birthDate = sBirthDate;
                user.summary = sSummary;
                user.address = sAddress;
                user.picture = encodedImage;
                user.method = "mycorz";
                PrefUtils.setCurrentUser(user,Login.this);

                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }
        }

        GetImage gi = new GetImage();
        gi.execute(profile);
    }
}
