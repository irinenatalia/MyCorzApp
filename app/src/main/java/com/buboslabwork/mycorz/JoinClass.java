package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
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

public class JoinClass extends AppCompatActivity {
    String classID,className,classDescription,classSize,date,location,locationDetail,time,ageLevel,skillLevel,cost,privateCost,additionalCost,latitude,longitude;
    String email;
    Integer typeFlag; // 0 for workshop, 1 for private
    Integer additionalFlag; // 0 for no, 1 for yes
    Integer totalPayment;
    TextView tvName,tvDescription,tvDate,tvTime,tvLocation,tvLocation2,tvAge,tvSkill,tvType,tvWorkshopCost,tvPrivateCost,tvAdditionalYesCost;
    TextView workshopIcon,workshopIcon2,privateIcon,privateIcon2,additionalNoIcon,additionalYesIcon,additionalNo,additionalYes;
    TextView tvTotalPayment;
    EditText editLocationDetail;
    ImageButton btnLocation;

    User user;

    private static final Integer ADDRESS_INTEGER_VALUE = 0;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(JoinClass.this); //get user preference
        if(user != null) {
            email = user.email;
        }

        tvName = (TextView)findViewById(R.id.joinClassName);
        tvDescription = (TextView)findViewById(R.id.joinClassDescription);
        tvDate = (TextView)findViewById(R.id.joinClassDate);
        tvTime = (TextView)findViewById(R.id.joinClassTime);
        tvLocation = (TextView)findViewById(R.id.joinClassLocation);
        tvLocation2 = (TextView)findViewById(R.id.joinClassLocation2);
        tvAge = (TextView)findViewById(R.id.joinClassAge);
        tvSkill = (TextView)findViewById(R.id.joinClassSkill);
        tvWorkshopCost = (TextView)findViewById(R.id.joinClassWorkshopCost);
        tvPrivateCost = (TextView)findViewById(R.id.joinClassPrivateCost);
        tvType = (TextView)findViewById(R.id.joinClassType);
        workshopIcon = (TextView)findViewById(R.id.joinClassWorkshopIcon);
        privateIcon = (TextView)findViewById(R.id.joinClassPrivateIcon);
        workshopIcon2 = (TextView)findViewById(R.id.joinClassWorkshopIcon2);
        privateIcon2 = (TextView)findViewById(R.id.joinClassPrivateIcon2);
        btnLocation = (ImageButton)findViewById(R.id.joinClassLocationBtn);
        editLocationDetail = (EditText) findViewById(R.id.joinClassLocationDetail);
        additionalNoIcon = (TextView)findViewById(R.id.joinClassAdditionalNoIcon);
        additionalYesIcon = (TextView)findViewById(R.id.joinClassAdditionalYesIcon);
        additionalNo = (TextView)findViewById(R.id.joinClassAdditionalNo);
        additionalYes = (TextView)findViewById(R.id.joinClassAdditionalYes);
        tvAdditionalYesCost = (TextView)findViewById(R.id.joinClassAdditionalYesCost);
        tvTotalPayment = (TextView)findViewById(R.id.joinClassTotalPayment);

        typeFlag = 0; //class type: workshop
        additionalFlag = 0; //no additional cost

        Intent i = getIntent();
        if(i != null){
            classID = i.getStringExtra("classID");
            className = i.getStringExtra("className");
            classDescription = i.getStringExtra("classDescription");
            classSize = i.getStringExtra("classSize");
            date = i.getStringExtra("date");
            location = i.getStringExtra("location");
            locationDetail = i.getStringExtra("locationDetail");
            time = i.getStringExtra("time");
            ageLevel = i.getStringExtra("ageLevel");
            skillLevel = i.getStringExtra("skillLevel");
            cost = i.getStringExtra("cost");
            privateCost = i.getStringExtra("privateCost");
            additionalCost = i.getStringExtra("additionalCost");
            latitude = i.getStringExtra("latitude");
            longitude = i.getStringExtra("longitude");

            totalPayment = Integer.parseInt(cost);
            tvName.setText(className);
            tvDescription.setText(classDescription);
            tvDate.setText(date);
            tvTime.setText(time);
            tvLocation.setText(location);
            tvLocation2.setText(location);
            editLocationDetail.setText(locationDetail);
            tvAge.setText(ageLevel);
            tvSkill.setText(skillLevel);

            Integer costPlainInt = Integer.parseInt(cost);
            Integer privateCostPlainInt = Integer.parseInt(privateCost);
            Integer additionalCostPlainInt = Integer.parseInt(additionalCost);
            tvWorkshopCost.setText("Rp " + String.format("%,d", costPlainInt).replace(',','.'));
            tvPrivateCost.setText("Rp " + String.format("%,d", privateCostPlainInt).replace(',','.'));
            tvAdditionalYesCost.setText("(Rp " + String.format("%,d", additionalCostPlainInt).replace(',','.') + ")");
            tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
        }

        workshopIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                workshopIcon.setText("●");
                privateIcon.setText("○");
                typeFlag = 0;
                tvLocation2.setText(location);
                editLocationDetail.setText(locationDetail);
                tvLocation2.setClickable(false);
                editLocationDetail.setClickable(false);

                if(additionalFlag == 1){
                    totalPayment = Integer.parseInt(cost) + Integer.parseInt(additionalCost);
                    tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                }
                else{
                    totalPayment = Integer.parseInt(cost);
                    tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                }
            }
        });
        workshopIcon2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                workshopIcon.setText("●");
                privateIcon.setText("○");
                typeFlag = 0;
                tvLocation2.setText(location);
                editLocationDetail.setText(locationDetail);
                tvLocation2.setClickable(false);
                editLocationDetail.setClickable(false);

                if(additionalFlag == 1){
                    totalPayment = Integer.parseInt(cost) + Integer.parseInt(additionalCost);
                    tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                }
                else{
                    totalPayment = Integer.parseInt(cost);
                    tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                }
            }
        });
        privateIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!privateCost.isEmpty() || !privateCost.equalsIgnoreCase("0")){
                    privateIcon.setText("●");
                    workshopIcon.setText("○");
                    typeFlag = 1;
                    tvLocation2.setText(""); //empty the location field
                    editLocationDetail.setText("");
                    tvLocation2.setClickable(true);
                    editLocationDetail.setClickable(true);

                    if(additionalFlag == 1){
                        totalPayment = Integer.parseInt(privateCost) + Integer.parseInt(additionalCost);
                        tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                    }
                    else{
                        totalPayment = Integer.parseInt(privateCost);
                        tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                    }
                }
            }
        });
        privateIcon2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!privateCost.isEmpty() || !privateCost.equalsIgnoreCase("0")){
                    privateIcon.setText("●");
                    workshopIcon.setText("○");
                    typeFlag = 1;
                    tvLocation2.setText("");
                    editLocationDetail.setText("");
                    tvLocation2.setClickable(true);
                    editLocationDetail.setClickable(true);

                    if(additionalFlag == 1){
                        totalPayment = Integer.parseInt(privateCost) + Integer.parseInt(additionalCost);
                        tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                    }
                    else{
                        totalPayment = Integer.parseInt(privateCost);
                        tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                    }
                }
            }
        });
        additionalYesIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!additionalCost.isEmpty() || !additionalCost.equalsIgnoreCase("0")){
                    additionalYesIcon.setText("●");
                    additionalNoIcon.setText("○");
                    additionalFlag = 1;

                    if(typeFlag == 1){ //private class
                        totalPayment = Integer.parseInt(privateCost) + Integer.parseInt(additionalCost);
                        tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                    }
                    else{
                        totalPayment = Integer.parseInt(cost) + Integer.parseInt(additionalCost);
                        tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                    }
                }
            }
        });
        additionalYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!additionalCost.isEmpty() || !additionalCost.equalsIgnoreCase("0")){
                    additionalYesIcon.setText("●");
                    additionalNoIcon.setText("○");
                    additionalFlag = 1;

                    if(typeFlag == 1){ //private class
                        totalPayment = Integer.parseInt(privateCost) + Integer.parseInt(additionalCost);
                        tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                    }
                    else{
                        totalPayment = Integer.parseInt(cost) + Integer.parseInt(additionalCost);
                        tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                    }
                }
            }
        });
        additionalNoIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                additionalNoIcon.setText("●");
                additionalYesIcon.setText("○");
                additionalFlag = 0;

                if(typeFlag == 1){ //private class
                    totalPayment = Integer.parseInt(privateCost);
                    tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                }
                else{
                    totalPayment = Integer.parseInt(cost);
                    tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                }
            }
        });
        additionalNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                additionalNoIcon.setText("●");
                additionalYesIcon.setText("○");
                additionalFlag = 0;

                if(typeFlag == 1){ //private class
                    totalPayment = Integer.parseInt(privateCost);
                    tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                }
                else{
                    totalPayment = Integer.parseInt(cost);
                    tvTotalPayment.setText("(Rp " + String.format("%,d", totalPayment).replace(',','.') + ")");
                }
            }
        });

        // set address to Maps API
        tvLocation2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(JoinClass.this, ClassLocation.class);
                startActivityForResult(i, ADDRESS_INTEGER_VALUE);
            }
        });
        // set address to Maps API
        btnLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(JoinClass.this, ClassLocation.class);
                startActivityForResult(i, ADDRESS_INTEGER_VALUE);
            }
        });
        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackJoinClass);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JoinClass.this.finish();
            }
        });
        Button btnReviewRequest = (Button)findViewById(R.id.joinClassCheckOut);
        btnReviewRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(TextUtils.isEmpty(editLocationDetail.getText().toString()))
                    locationDetail = "";
                else
                    locationDetail = editLocationDetail.getText().toString();

                new JoinClass.AsyncJoin().execute(classID,email,date,time,typeFlag.toString(),additionalFlag.toString(),totalPayment.toString(),location,locationDetail);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADDRESS_INTEGER_VALUE && resultCode == SetClass.RESULT_OK) {
            String returnedAddress = data.getStringExtra("returnedAddressParam");
            location = returnedAddress;
            tvLocation2.setText(returnedAddress);
            //Toast.makeText(SetClass.this, returnedAddress, Toast.LENGTH_SHORT).show();
            // TODO Update your TextView.
        }
    }
    //async class to join
    private class AsyncJoin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(JoinClass.this);
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
                url = new URL("http://vidcom.click/admin/android/joinClass.php");

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
                        .appendQueryParameter("classID", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("classDate", params[2])
                        .appendQueryParameter("classTime", params[3])
                        .appendQueryParameter("classType", params[4])
                        .appendQueryParameter("additional", params[5])
                        .appendQueryParameter("totalPayment", params[6])
                        .appendQueryParameter("location", params[7])
                        .appendQueryParameter("locationDetail", params[8]);
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
            Log.v("joinresult",result);

            if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("")){
                Toast.makeText(JoinClass.this, "There is a problem in our server, please try again.", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(JoinClass.this, "Something went wrong, check again your connection.", Toast.LENGTH_LONG).show();
            }
            else if(result.substring(0,1).equalsIgnoreCase("O")){
                Intent i = new Intent(JoinClass.this, PaymentMethod.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("orderID", result);
                mBundle.putString("className", className);
                mBundle.putString("date", date);
                mBundle.putString("location", location);
                mBundle.putString("locationDetail", locationDetail);
                mBundle.putString("time", time);
                mBundle.putString("type", typeFlag.toString());
                mBundle.putString("age", ageLevel);
                mBundle.putString("skill", skillLevel);
                mBundle.putString("cost", totalPayment.toString());
                i.putExtras(mBundle);
                startActivity(i);
            }
        }
    }
}
