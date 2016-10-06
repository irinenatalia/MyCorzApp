package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;

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
import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RequestClassForm extends AppCompatActivity {
    EditText editCategory,editClassDesc,editClassDate,editTime;
    String email,mentorUsername;
    String category,description,date,skill,time,age,classSize;
    TimePickerDialog mTimePicker;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    ArrayList<String> arrayCategory;
    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String REQUEST_URL = "http://vidcom.click/admin/android/requestClassForm.php";
    User user;

    private static final Integer CATEGORY_INTEGER_VALUE = 1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_class_form);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //email as identifier when user submit request form
        user=PrefUtils.getCurrentUser(RequestClassForm.this);
        if(user != null){
            email = user.email;
        }

        //get mentor username to send back to MentorDetailClass if user submit request form
        Intent i = getIntent();
        if(i != null){
            mentorUsername = i.getStringExtra("mentorUsername");
        }

        editCategory = (EditText)findViewById(R.id.requestFormCategory);
        editCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TempCategory.class);
                startActivityForResult(i, CATEGORY_INTEGER_VALUE);
            }
        });
        editClassDesc = (EditText)findViewById(R.id.requestFormClassDesc);

        //change class time
        editTime = (EditText)findViewById(R.id.requestFormTime);
        editTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                mTimePicker = new TimePickerDialog(RequestClassForm.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);//24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        //change class date
        editClassDate = (EditText)findViewById(R.id.requestFormDate);
        editClassDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //SPINNER FOR CLASS SIZE
        Spinner classSizeSpinner = (Spinner) findViewById(R.id.requestFormClassSize);

        String[] class_size = new String[] { "Class Size", "1-5 person", "6-10 person", "More than 10" };

        final ArrayAdapter<String> classSizeAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, class_size){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return true;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                return view;
            }
        };

        classSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSizeSpinner.setAdapter(classSizeAdapter);

        classSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position >= 0){
                    // Notify the selected item text
                    classSize = selectedItemText;
                    ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(RequestClassForm.this, R.color.colorTextGreyNormal));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // END SPINNER

        //SPINNER FOR SKILL LEVEL
        Spinner skillSpinner = (Spinner) findViewById(R.id.requestFormSkillLevel);

        String[] skill_level = new String[] { "Level", "Beginner", "Intermediate", "Advanced" };

        final ArrayAdapter<String> skillAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, skill_level){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return true;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                return view;
            }
        };

        skillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillSpinner.setAdapter(skillAdapter);

        skillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position >= 0){
                    // Notify the selected item text
                    skill = selectedItemText;
                    ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(RequestClassForm.this, R.color.colorTextGreyNormal));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // END SPINNER

        //SPINNER FOR AGE LEVEL
        Spinner ageSpinner = (Spinner) findViewById(R.id.requestFormAgeLevel);

        String[] age_level = new String[] { "Age", "Child", "Teen", "Adult" };

        final ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, age_level){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return true;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                return view;
            }
        };

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position >= 0){
                    // Notify the selected item text
                    age = selectedItemText;
                    ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(RequestClassForm.this, R.color.colorTextGreyNormal));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // END SPINNER

        ImageButton btnSubmit = (ImageButton)findViewById(R.id.requestFormSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (TextUtils.isEmpty(editCategory.getText().toString()) || TextUtils.isEmpty(editClassDate.getText().toString()) ||
                        TextUtils.isEmpty(classSize) || TextUtils.isEmpty(skill) ||
                        TextUtils.isEmpty(editTime.getText().toString()) ||
                        TextUtils.isEmpty(age) || TextUtils.isEmpty(email)) {
                    Toast.makeText(RequestClassForm.this, "Please fill in the empty field", Toast.LENGTH_LONG).show();
                }
                else {
                    if (TextUtils.isEmpty(editClassDesc.getText().toString())){
                        description = "kosong";
                    }
                    else
                        description = editClassDesc.getText().toString();

                    category = editCategory.getText().toString();
                    date = editClassDate.getText().toString();
                    time = editTime.getText().toString();

                    Log.v("requestform","category " + category);
                    Log.v("requestform","title " + description);
                    Log.v("requestform","date " + date);
                    Log.v("requestform","time " + time);

                    new RequestClassForm.AsyncRequest().execute(category,description,date,classSize,skill,time,age,email,mentorUsername);

                }

            }
        });

        ImageButton back = (ImageButton)findViewById(R.id.btnBackRequestForm);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RequestClassForm.this.finish();
            }
        });

        // FOOTER MENU
        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnRequestFormHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnRequestFormProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnRequestFormNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnRequestFormSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvRequestFormHistory);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvRequestFormProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvRequestFormNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvRequestFormSetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATEGORY_INTEGER_VALUE && resultCode == SetClass.RESULT_OK) {
            category = data.getStringExtra("returnedCategoryParam");
            editCategory.setText(category);
            //Toast.makeText(SetClass.this, returnedAddress, Toast.LENGTH_SHORT).show();
            // TODO Update your TextView.
        }
    }

    public void showDatePickerDialog() { //set class date
        DialogFragment newFragment = new RequestFormDatePicker();
        newFragment.show(getSupportFragmentManager(), "Set Class Date");
    }

    // ASYNCTASK FOR SENDING DATA
    private class AsyncRequest extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(RequestClassForm.this);
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
                url = new URL(REQUEST_URL);

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
                        .appendQueryParameter("category", params[0])
                        .appendQueryParameter("description", params[1])
                        .appendQueryParameter("date", params[2])
                        .appendQueryParameter("classSize", params[3])
                        .appendQueryParameter("skill", params[4])
                        .appendQueryParameter("time", params[5])
                        .appendQueryParameter("age", params[6])
                        .appendQueryParameter("email", params[7])
                        .appendQueryParameter("mentor", params[8]);
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
            Log.v("requestformresult",result);

            if(result.equalsIgnoreCase("true"))
            {
                Toast.makeText(RequestClassForm.this, "Your request has been submitted", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), MentorDetailClass.class);
                i.putExtra("mentorUsername", mentorUsername);
                startActivity(i);
            }else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("")){
                // If username and password does not match display a error message
                Toast.makeText(RequestClassForm.this, "Can't submit your request to our database, please try again", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(RequestClassForm.this, "Something went wrong, connection problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}
