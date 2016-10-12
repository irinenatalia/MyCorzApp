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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Register extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    EditText name,email,password;
    String sName, sEmail, sPassword, sFacebookID, sCompleteName, sProfile;
    ImageView viewPassword;
    Boolean isChecked = false;
    User user;
    String loginMethod = "";

    CallbackManager callbackManager;
    LoginButton loginButton;
    ImageButton fbImageButton;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_register);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        name = (EditText)findViewById(R.id.registerName);
        email = (EditText)findViewById(R.id.registerEmail);
        password = (EditText)findViewById(R.id.registerPass);
        viewPassword = (ImageView) findViewById(R.id.registerViewPassword);

        sName = name.getText().toString();
        sEmail = email.getText().toString();
        sPassword = password.getText().toString();

        fbImageButton = (ImageButton)findViewById(R.id.fbSigninImageButton);
        loginButton = (LoginButton) findViewById(R.id.register_login_facebook);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                Log.e("response: ", response + "");
                                try {
                                    sFacebookID = object.getString("id").toString();
                                    sEmail = object.getString("email").toString();
                                    sCompleteName = object.getString("name").toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.v("registerresult",sFacebookID);
                                new Register.AsyncFBLogin().execute();
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(Register.this, "Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(Register.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        });

        fbImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        viewPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isChecked == false) {
                    // show password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    viewPassword.setImageResource(R.drawable.eye);
                    isChecked = true;
                } else {
                    // hide password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    viewPassword.setImageResource(R.drawable.hide_eye);
                    isChecked = false;
                }
            }
        });

        ImageButton register = (ImageButton) findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(name.getText().toString()) && TextUtils.isEmpty(email.getText().toString()) && TextUtils.isEmpty(password.getText().toString()))
                    Toast.makeText(Register.this, "Please fill in the empty field", Toast.LENGTH_SHORT).show();
                else {
                    Bitmap profileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_avatar_upload);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    profileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteImage = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(byteImage, Base64.DEFAULT);
                    sProfile = encodedImage;
                    new AsyncRegister().execute(name.getText().toString(), email.getText().toString(), password.getText().toString(),sProfile);
                }
            }
        });
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
            PrefUtils.setCurrentUser(user, Register.this);

            loginMethod = "facebook";
            Log.v("registerresult",loginMethod);
            new AsyncRegister().execute(sCompleteName,sEmail,"");
        }
    }

    //register via MyCorz
    private class AsyncRegister extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Register.this);
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
                        .appendQueryParameter("password", params[2])
                        .appendQueryParameter("profile", params[3]);
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
                if(loginMethod.equalsIgnoreCase("facebook")){
                    Intent i = new Intent(getApplicationContext(), Home.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(Register.this, "You have successfully registered to MyCorz", Toast.LENGTH_LONG).show();

                    user = new User();
                    user.email = email.getText().toString();
                    user.password = password.getText().toString();
                    user.name = name.getText().toString();
                    user.picture = sProfile;
                    user.method = "mycorz";
                    PrefUtils.setCurrentUser(user,Register.this);

                    Intent i = new Intent(getApplicationContext(), Home.class);
                    startActivity(i);
                    finish();
                }
            }else if (result.equalsIgnoreCase("false")){
                // If username and password does not match display a error message
                Toast.makeText(Register.this, "Fail to register to our database, please try again.", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("registered")) {
                //if email is already registered
                if(loginMethod.equalsIgnoreCase("facebook")){
                    Intent i = new Intent(getApplicationContext(), Home.class);
                    startActivity(i);
                    finish();
                }
                else
                    Toast.makeText(Register.this, "Your email is a registered MyCorz account, please register with another email address", Toast.LENGTH_LONG).show();
            }else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(Register.this, "Connection problem. Please try to register again.", Toast.LENGTH_LONG).show();

            }
        }

    }
}
