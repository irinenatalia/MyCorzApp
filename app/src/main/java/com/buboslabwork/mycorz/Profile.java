package com.buboslabwork.mycorz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

public class Profile extends AppCompatActivity implements PicModeSelectDialogFragment.IPicModeSelectListener{
    public static final String TAG = "ImageViewActivity";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;
    private String imgUri;

    private Button mBtnUpdatePic;
    private ImageView mImageView;
    private CardView mCardView;
    TextView hintAddress;
    EditText name,password,birthDate,address,summary;
    String sProfile,sName,sPassword,sBirthDate,sAddress,sSummary,email,loginMethod;
    ImageView viewPassword;
    Boolean isChecked = false;
    Boolean isAddressChecked = false;

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
        setContentView(R.layout.activity_profile);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(Profile.this);

        name = (EditText)findViewById(R.id.profileName);
        password = (EditText)findViewById(R.id.profilePassword);
        birthDate = (EditText)findViewById(R.id.profileBirthDate);
        address = (EditText)findViewById(R.id.profileAddress);
        summary = (EditText)findViewById(R.id.profileSummary);
        mImageView = (ImageView) findViewById(R.id.iv_user_pic);
        mCardView = (CardView) findViewById(R.id.cv_image_container);
        viewPassword = (ImageView) findViewById(R.id.profileViewPassword);
        hintAddress = (TextView) findViewById(R.id.profileHintAddress);

        if(user!=null){
            email = user.email;
            loginMethod = user.method;
            if(user.name != null){
                name.setText(user.name);
                sName = user.name;
            }
            password.setText(user.password);
            if(user.birthDate != null){
                birthDate.setText(user.birthDate);
                sBirthDate = user.birthDate;
                Log.v("profileresultpref", sBirthDate);
            }
            if(user.address != null){
                address.setText(user.address);
                sAddress = user.address;
                Log.v("profileresultpref", sAddress);
            }
            if(user.summary != null){
                summary.setText(user.summary);
                sSummary = user.summary;
                Log.v("profileresultpref", sSummary);
            }
            if(user.picture != null){
                byte[] b = Base64.decode(user.picture, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                mImageView.setImageBitmap(bitmap);
                sProfile = user.picture;
            }
        }
        initCardView(); //Resize card view according to activity dimension
        /*
        mBtnUpdatePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProfilePicDialog();
            }
        });
        */
        checkPermissions();

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

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAddressChecked) {
                    hintAddress.setText("");
                    isAddressChecked = false;
                }
                else{
                    hintAddress.setText("Address based on ID Card");
                    isAddressChecked = true;
                }
            }
        });

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        ImageButton updateCert = (ImageButton)findViewById(R.id.profileImgBtnCertificate);
        updateCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                sName = name.getText().toString();
                sPassword = password.getText().toString();
                sBirthDate = birthDate.getText().toString();
                sAddress = address.getText().toString();
                sSummary = summary.getText().toString(); */
                Intent i = new Intent(getApplicationContext(), Certificate.class);
                /*
                i.putExtra("name", sName);
                i.putExtra("password", sPassword);
                i.putExtra("birthdate", sBirthDate);
                i.putExtra("address", sAddress);
                i.putExtra("summary", sSummary);
                */
                startActivity(i);
            }
        });
        ImageButton btnHome = (ImageButton)findViewById(R.id.btnProfileBackToHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }
        });
        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnProfileHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnProfileSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnProfileNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        ImageButton imgBtnSave = (ImageButton)findViewById(R.id.profileImgBtnSave);
        imgBtnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sName = name.getText().toString();
                sPassword = password.getText().toString();
                if(birthDate.getText().toString().isEmpty())
                    sBirthDate = "";
                else
                    sBirthDate = birthDate.getText().toString();
                if(address.getText().toString().isEmpty())
                    sAddress = "";
                else
                    sAddress = address.getText().toString();
                if(summary.getText().toString().isEmpty())
                    sSummary = "";
                else
                    sSummary = summary.getText().toString();

                Log.v("profileresult", sProfile);
                Log.v("profileresult", sName);
                Log.v("profileresultpass", sPassword);
                Log.v("profileresultbd", sBirthDate);
                Log.v("profileresultaddress", sAddress);
                Log.v("profileresultsummary", sSummary);
                Log.v("profileresultemail", email);

                new AsyncProfile().execute(sProfile,sName,sPassword,sBirthDate,sAddress,sSummary,email);
            }
        });
        Button btnSave = (Button)findViewById(R.id.profileBtnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sName = name.getText().toString();
                sPassword = password.getText().toString();
                if(birthDate.getText().toString().isEmpty())
                    sBirthDate = "null";
                else
                    sBirthDate = birthDate.getText().toString();
                if(address.getText().toString().isEmpty())
                    sAddress = "null";
                else
                    sAddress = address.getText().toString();
                if(summary.getText().toString().isEmpty())
                    sSummary = "null";
                else
                    sSummary = summary.getText().toString();

                Log.v("profileresult", sProfile);
                Log.v("profileresult", sName);
                Log.v("profileresultpass", sPassword);
                Log.v("profileresultbd", sBirthDate);
                Log.v("profileresultaddress", sAddress);
                Log.v("profileresultsummary", sSummary);
                Log.v("profileresultemail", email);
                new AsyncProfile().execute(sProfile,sName,sPassword,sBirthDate,sAddress,sSummary,email);
            }
        });
    }
    public void showDatePickerDialog() { //set birthdate dialog
        DialogFragment newFragment = new BirthdatePicker();
        newFragment.show(getSupportFragmentManager(), "Set Birthdate");
    }
    public void changePicture(View v){
        showAddProfilePicDialog();
    }
    @SuppressLint("InlinedApi")
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1234);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_CODE_UPDATE_PIC) {
            if (resultCode == RESULT_OK) {
                String imagePath = result.getStringExtra(GOTOConstants.IntentExtras.IMAGE_PATH);
                showCroppedImage(imagePath);
            } else if (resultCode == RESULT_CANCELED) {
                //TODO : Handle case
            } else {
                String errorMsg = result.getStringExtra(ImageCrop.ERROR_MSG);
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            mImageView.setImageBitmap(myBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteImage = baos.toByteArray();
            String encodedImage = Base64.encodeToString(byteImage, Base64.DEFAULT);
            sProfile = encodedImage;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //--------Private methods --------

    private void initCardView() {
        mCardView.setPreventCornerOverlap(false);
        /*
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        //We are implementing this only for portrait mode so width will be always less
        int w = displayMetrics.widthPixels;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mCardView.getLayoutParams();
        int leftMargin = lp.leftMargin;
        int topMargin = lp.topMargin;
        int rightMargin = lp.rightMargin;
        int paddingLeft = mCardView.getPaddingLeft();
        int paddingRight = mCardView.getPaddingLeft();
        int ch = w - leftMargin - rightMargin + paddingLeft + paddingRight;
        mCardView.getLayoutParams().height = ch;
        */
    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    private void actionProfilePic(String action) {
        Intent intent = new Intent(this, ImageCrop.class);
        intent.putExtra("ACTION", action);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }


    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(GOTOConstants.PicModes.CAMERA) ? GOTOConstants.IntentExtras.ACTION_CAMERA : GOTOConstants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }

    private class AsyncProfile extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Profile.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tUpdating your profile...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("http://vidcom.click/admin/android/saveProfile.php");

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
                        .appendQueryParameter("profile", params[0])
                        .appendQueryParameter("name", params[1])
                        .appendQueryParameter("password", params[2])
                        .appendQueryParameter("birthdate", params[3])
                        .appendQueryParameter("address", params[4])
                        .appendQueryParameter("profileSummary", params[5])
                        .appendQueryParameter("email", params[6]);
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
            Log.v("profileresultfinal",result);
            if(result.equalsIgnoreCase("true"))
            {
                //should delete user prefs
                PrefUtils.clearCurrentUser(Profile.this);
                user = new User();
                user.email = email;
                user.method = loginMethod;
                user.picture = sProfile;
                user.name = sName;
                user.password = sPassword;
                user.birthDate = sBirthDate;
                user.address = sAddress;
                user.summary = sSummary;
                PrefUtils.setCurrentUser(user, Profile.this);

                Toast.makeText(Profile.this, "You have successfully updated your profile", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("")){
                // If there is something wrong with the query
                Toast.makeText(Profile.this, "Your profile couldn't be updated into database, please try again.", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(Profile.this, "Something went wrong, connection problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}
