package com.buboslabwork.mycorz;

import android.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.internal.Utility;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.DefaultDayViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.widget.Toast.LENGTH_SHORT;

public class SetClass extends AppCompatActivity{
    private AlertDialog theDialog;
    private CalendarPickerView dialogView;
    String testDate;
    TimePickerDialog mTimePicker;
    EditText editCategory,className,classDescription,editKeyword,setdate,time,location,locationDetail;
    EditText cost,privateCost,additionalCost,additionalCostFor;
    ImageView uploadPic;
    AutoCompleteTextView category;
    public String returnedCategory2 = "";
    public String returnedAddress = "";
    public String returnedLat = "";
    public String returnedLong = "";
    public String returnedCategory = "";
    public String sClassPic = "";
    Integer updateClassFlag = 0; // 0 inactive, 1 active
    private static final Integer ADDRESS_INTEGER_VALUE = 0;
    private static final Integer CATEGORY_INTEGER_VALUE = 11;

    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    private String mImagePath;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    ArrayList<String> arrayCategory;
    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String CATEGORY_URL = "http://vidcom.click/admin/android/viewCategoryMain.php";

    User user;
    String sClassID = "";
    String sCategory,sTitle,sDescription,sKeyword,age,skill,date,sTime,sClassSize,sLocationDetail;
    String sCost,sPrivateCost,sAdditionalCost,sLocation,sAdditionalCostFor,email;
    String sClassPicture,pageSource;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_class);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(SetClass.this);
        if(user != null){
            Log.v("setclass",user.email);
            email = user.email;
        }

        date = "";
        uploadPic = (ImageView)findViewById(R.id.setclass_uploadPicture);
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY);
            }
        });
        editCategory = (EditText)findViewById(R.id.setclass_category);
        editCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TempCategory.class);
                startActivityForResult(i, CATEGORY_INTEGER_VALUE);
            }
        });
        className = (EditText)findViewById(R.id.setclass_name);
        classDescription = (EditText)findViewById(R.id.setclass_description);
        editKeyword = (EditText)findViewById(R.id.setclass_keyword);
        location = (EditText) findViewById(R.id.setclass_location);
        locationDetail = (EditText)findViewById(R.id.setclass_location_detail);
        setdate = (EditText) findViewById(R.id.setclass_date);
        time = (EditText) findViewById(R.id.setclass_time);
        cost = (EditText)findViewById(R.id.setclass_cost);
        privateCost = (EditText)findViewById(R.id.setclass_privateCost);
        additionalCost = (EditText)findViewById(R.id.setclass_additionalCost);
        additionalCostFor = (EditText)findViewById(R.id.setclass_additionalCostFor);

        cost.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    cost.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp ,]", "");

                    if (cleanString.length() > 0) {
                        double parsed = Double.parseDouble(cleanString);
                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setCurrencySymbol("Rp ");
                        //dfs.setGroupingSeparator('.');
                        //dfs.setMonetaryDecimalSeparator('.');
                        ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
                        formatter.setMaximumFractionDigits(0);
                        current = formatter.format(parsed);
                    } else {
                        current = cleanString;
                    }
                    cost.setText(current);
                    cost.setSelection(current.length());
                    cost.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
        privateCost.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    privateCost.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp ,]", "");

                    if (cleanString.length() > 0) {
                        double parsed = Double.parseDouble(cleanString);
                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setCurrencySymbol("Rp ");
                        //dfs.setGroupingSeparator('.');
                        //dfs.setMonetaryDecimalSeparator('.');
                        ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
                        formatter.setMaximumFractionDigits(0);
                        current = formatter.format(parsed);
                    } else {
                        current = cleanString;
                    }
                    privateCost.setText(current);
                    privateCost.setSelection(current.length());
                    privateCost.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
        additionalCost.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    additionalCost.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp ,]", "");

                    if (cleanString.length() > 0) {
                        double parsed = Double.parseDouble(cleanString);
                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setCurrencySymbol("Rp ");
                        //dfs.setGroupingSeparator('.');
                        //dfs.setMonetaryDecimalSeparator('.');
                        ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
                        formatter.setMaximumFractionDigits(0);
                        current = formatter.format(parsed);
                    } else {
                        current = cleanString;
                    }
                    additionalCost.setText(current);
                    additionalCost.setSelection(current.length());
                    additionalCost.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        Intent intent = getIntent();
        if (intent != null) { //Null Checking
            pageSource = intent.getStringExtra("pageSource");
            if(pageSource.equalsIgnoreCase("RequestClass")){
                age = intent.getStringExtra("age_level");
                skill = intent.getStringExtra("skill_level");
                sClassSize = intent.getStringExtra("class_size");
                date = intent.getStringExtra("date");
                sTime = intent.getStringExtra("time");
                sCategory = intent.getStringExtra("category");
                sTitle = intent.getStringExtra("title");

                returnedCategory = sCategory;
                editCategory.setText(sCategory);
                className.setText(sTitle);
                setdate.setText(date);
                time.setText(sTime);

                Log.v("requestclass", age);
                Log.v("requestclass", skill);
                Log.v("requestclass", sClassSize);
            }
            else if (pageSource.equalsIgnoreCase("Mentor")){
                updateClassFlag = 1;

                sClassID = intent.getStringExtra("classID");
                sCategory = intent.getStringExtra("category");
                sTitle = intent.getStringExtra("className");
                sDescription = intent.getStringExtra("classDescription");
                sClassSize = intent.getStringExtra("classSize");
                age = intent.getStringExtra("ageLevel");
                skill = intent.getStringExtra("skillLevel");
                date = intent.getStringExtra("date");
                sTime = intent.getStringExtra("time");
                returnedAddress = intent.getStringExtra("location");
                sLocationDetail = intent.getStringExtra("locationDetail");
                sCost = intent.getStringExtra("cost");
                sPrivateCost = intent.getStringExtra("privateCost");
                sAdditionalCost = intent.getStringExtra("additionalCost");
                sAdditionalCostFor = intent.getStringExtra("additionalCostFor");
                returnedLat = intent.getStringExtra("latitude");
                returnedLong = intent.getStringExtra("longitude");
                pageSource = intent.getStringExtra("pageSource");
                sClassPicture = intent.getStringExtra("classPicture");

                returnedCategory = sCategory;
                editCategory.setText(sCategory);
                className.setText(sTitle);
                classDescription.setText(sDescription);
                setdate.setText(date);
                time.setText(sTime);
                location.setText(returnedAddress);
                locationDetail.setText(sLocationDetail);
                cost.setText(sCost);
                privateCost.setText(sPrivateCost);
                additionalCost.setText(sAdditionalCost);
                additionalCostFor.setText(sAdditionalCostFor);

                if(!sClassPicture.isEmpty() || !sClassPicture.equalsIgnoreCase("")){
                    getImage(sClassPicture);
                }
                Log.v("requestclass", returnedLat);
                Log.v("requestclass", returnedLong);
                Log.v("requestclass", sClassPicture);
            }
        }

        //SPINNER FOR AGE LEVEL
        Spinner ageSpinner = (Spinner) findViewById(R.id.age_level_spinner);

        String[] age_level = new String[] { "Age Level", "Child", "Teen", "Adult" };

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
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);
        ageSpinner.setPrompt("Age Level");

        if(pageSource.equalsIgnoreCase("RequestClass") || pageSource.equalsIgnoreCase("Mentor")) {
            Log.v("requestclass", "get into age spinner");
            //set default position from intent value
            ArrayAdapter myAdap = (ArrayAdapter) ageSpinner.getAdapter();
            int ageSpinnerPosition = myAdap.getPosition(age);
            Log.v("requestclass", "Pos: " + ageSpinnerPosition);
            //set the default according to value
            ageSpinner.setSelection(ageSpinnerPosition);
        }

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position >= 0){
                    // Notify the selected item text
                    age = selectedItemText;
                    ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(SetClass.this, R.color.colorTextGreyNormal));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // END SPINNER

        // SPINNER FOR SKILL LEVEL
        Spinner skillSpinner = (Spinner) findViewById(R.id.skill_level_spinner);

        String[] skill_level = new String[] { "Skill Level", "Beginner", "Intermediate", "Advanced" };

        final ArrayAdapter<String> skillAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, skill_level){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
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
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        skillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillSpinner.setAdapter(skillAdapter);
        if(pageSource.equalsIgnoreCase("RequestClass") || pageSource.equalsIgnoreCase("Mentor")) {
            //set default position from intent value
            ArrayAdapter skillAdap = (ArrayAdapter) skillSpinner.getAdapter();
            int skillSpinnerPosition = skillAdap.getPosition(skill);
            Log.v("requestclass", "Pos: " + skillSpinnerPosition);
            //set the default according to value
            skillSpinner.setSelection(skillSpinnerPosition);
        }

        skillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position >= 0){
                    // Notify the selected item text
                    skill = selectedItemText;
                    ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(SetClass.this, R.color.colorTextGreyNormal));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //END SPINNER

        // SPINNER FOR CLASS SIZE
        Spinner classSizeSpinner = (Spinner) findViewById(R.id.classsize_spinner);

        String[] classSizeArray = new String[] { "Class Size", "1-5 person", "6-10 person", "More than 10" };

        final ArrayAdapter<String> classSizeAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, classSizeArray){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
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
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        classSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSizeSpinner.setAdapter(classSizeAdapter);
        if(pageSource.equalsIgnoreCase("RequestClass") || pageSource.equalsIgnoreCase("Mentor")) {
            //set default position from intent value
            ArrayAdapter classSizeAdap = (ArrayAdapter) classSizeSpinner.getAdapter();
            int classSizeSpinnerPosition = classSizeAdap.getPosition(sClassSize);
            Log.v("requestclass", "Pos: " + classSizeSpinnerPosition);
            //set the default according to value
            classSizeSpinner.setSelection(classSizeSpinnerPosition);
        }

        classSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position >= 0){
                    // Notify the selected item text
                    sClassSize = selectedItemText;
                    ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(SetClass.this, R.color.colorTextGreyNormal));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //END SPINNER

        // Initialize calendar
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        initButtonListeners(nextYear, lastYear);
        // End initialize calendar

        location.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ClassLocation.class);
                startActivityForResult(i, ADDRESS_INTEGER_VALUE);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                mTimePicker = new TimePickerDialog(SetClass.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        //SUBMIT CLASS TO DATABASE
        ImageButton imgBtnSave = (ImageButton)findViewById(R.id.setclass_imgButtonSave);
        imgBtnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TextUtils.isEmpty(returnedCategory) || returnedCategory.equalsIgnoreCase("")){
                    Toast.makeText(SetClass.this, "Please fill in the category", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(className.getText().toString()) || TextUtils.isEmpty(returnedAddress) ||
                        TextUtils.isEmpty(classDescription.getText().toString()) || TextUtils.isEmpty(age) ||
                        TextUtils.isEmpty(skill) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time.getText().toString()) ||
                        TextUtils.isEmpty(sClassSize) || TextUtils.isEmpty(cost.getText().toString())) {

                    Toast.makeText(SetClass.this, "Please fill in the empty field", Toast.LENGTH_LONG).show();
                }
                else {
                    sCost = cost.getText().toString();
                    sCost = sCost.replace("Rp ", "");
                    sCost = sCost.replace(",", "");

                    if(TextUtils.isEmpty(editKeyword.getText().toString())){
                        sKeyword = "";
                    }
                    else
                        sKeyword = editKeyword.getText().toString();
                    sLocation = returnedAddress;
                    if(TextUtils.isEmpty(locationDetail.getText().toString())){
                        sLocationDetail = "(empty)";
                    }
                    else
                        sLocationDetail = locationDetail.getText().toString();
                    if(TextUtils.isEmpty(privateCost.getText().toString())){
                        sPrivateCost = "0";
                    }
                    else {
                        sPrivateCost = privateCost.getText().toString();
                        sPrivateCost = sPrivateCost.replace("Rp ", "");
                        sPrivateCost = sPrivateCost.replace(",", "");
                    }
                    if(TextUtils.isEmpty(additionalCost.getText().toString())){
                        sAdditionalCost = "0";
                    }
                    else {
                        sAdditionalCost = additionalCost.getText().toString();
                        sAdditionalCost = sAdditionalCost.replace("Rp ", "");
                        sAdditionalCost = sAdditionalCost.replace(",", "");
                    }
                    if(TextUtils.isEmpty(additionalCostFor.getText().toString())){
                        sAdditionalCostFor = "(empty)";
                    }
                    else
                        sAdditionalCostFor = additionalCostFor.getText().toString();

                    Log.v("setclass",""+returnedCategory);
                    Log.v("setclass",sLocation);
                    Log.v("setclass",email);
                    Log.v("setclass",sCost);

                    new SetClass.AsyncLogin().execute(returnedCategory,className.getText().toString(),classDescription.getText().toString(),
                            age,skill,date,time.getText().toString(),sClassSize,sCost,
                            sPrivateCost,sAdditionalCost,sAdditionalCostFor,sLocation,sLocationDetail,email,sClassPic,
                            returnedLat,returnedLong,updateClassFlag.toString(),sClassID,sKeyword);

                }
            }
        });
        //SUBMIT CLASS TO DATABASE
        Button btnSave = (Button)findViewById(R.id.setclass_btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TextUtils.isEmpty(returnedCategory) || returnedCategory.equalsIgnoreCase("")){
                    Toast.makeText(SetClass.this, "Please fill in the category", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(className.getText().toString()) || TextUtils.isEmpty(returnedAddress) ||
                        TextUtils.isEmpty(classDescription.getText().toString()) || TextUtils.isEmpty(age) ||
                        TextUtils.isEmpty(skill) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time.getText().toString()) ||
                        TextUtils.isEmpty(sClassSize) || TextUtils.isEmpty(cost.getText().toString())) {
                    Toast.makeText(SetClass.this, "Please fill in the empty field", Toast.LENGTH_LONG).show();
                }
                else {
                    sCost = cost.getText().toString();
                    sCost = sCost.replace("Rp ", "");
                    sCost = sCost.replace(",", "");

                    if(TextUtils.isEmpty(editKeyword.getText().toString())){
                        sKeyword = "";
                    }
                    else
                        sKeyword = editKeyword.getText().toString();
                    sLocation = returnedAddress;
                    if(TextUtils.isEmpty(locationDetail.getText().toString())){
                        sLocationDetail = "(empty)";
                    }
                    else
                        sLocationDetail = locationDetail.getText().toString();
                    if(TextUtils.isEmpty(privateCost.getText().toString())){
                        sPrivateCost = "0";
                    }
                    else {
                        sPrivateCost = privateCost.getText().toString();
                        sPrivateCost = sPrivateCost.replace("Rp ", "");
                        sPrivateCost = sPrivateCost.replace(",", "");
                    }
                    if(TextUtils.isEmpty(additionalCost.getText().toString())){
                        sAdditionalCost = "0";
                    }
                    else {
                        sAdditionalCost = additionalCost.getText().toString();
                        sAdditionalCost = sAdditionalCost.replace("Rp ", "");
                        sAdditionalCost = sAdditionalCost.replace(",", "");
                    }
                    if(TextUtils.isEmpty(additionalCostFor.getText().toString())){
                        sAdditionalCostFor = "(empty)";
                    }
                    else
                        sAdditionalCostFor = additionalCostFor.getText().toString();

                    Log.v("setclass",""+returnedCategory);
                    Log.v("setclass",sLocation);
                    Log.v("setclass",email);
                    Log.v("setclass",sCost);

                    new SetClass.AsyncLogin().execute(returnedCategory,className.getText().toString(),classDescription.getText().toString(),
                            age,skill,date,time.getText().toString(),sClassSize,sCost,
                            sPrivateCost,sAdditionalCost,sAdditionalCostFor,sLocation,sLocationDetail,email,sClassPic,
                            returnedLat,returnedLong,updateClassFlag.toString(),sClassID,sKeyword);

                }

            }
        });
        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackSetClass);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Mentor.class);
                startActivity(i);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADDRESS_INTEGER_VALUE && resultCode == SetClass.RESULT_OK) {
            returnedAddress = data.getStringExtra("returnedAddressParam");
            returnedLat = data.getStringExtra("returnedLatParam");
            returnedLong = data.getStringExtra("returnedLongParam");

            location.setText(returnedAddress);
            Log.v("mapsresult","Return "+returnedLat);
            Log.v("mapsresult","Return "+returnedLong);
        }
        if (requestCode == CATEGORY_INTEGER_VALUE && resultCode == SetClass.RESULT_OK) {
            returnedCategory = data.getStringExtra("returnedCategoryParam");
            editCategory.setText(returnedCategory);
        }
        if (requestCode == REQUEST_CODE_PICK_GALLERY) {
            if (resultCode == RESULT_CANCELED) {
                return;
            } else if (resultCode == RESULT_OK) {
                try {
                    Bitmap bm=null;
                    if (data != null) {
                        try {
                            bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    uploadPic.setImageBitmap(bm);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteImage = baos.toByteArray();
                    sClassPic = Base64.encodeToString(byteImage, Base64.DEFAULT);
                } catch (Exception e) {
                    Log.e("uploadpicresult","Error while opening the image file. Please try again.");
                    return;
                }
            } else {
                Log.e("uploadpicresult","Error while opening the image file. Please try again.");
                return;
            }

        }
    }

    /* image uploading function */

    @SuppressLint("InlinedApi")
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1234);
        }
    }
    /* image uploading function - end */

    private void initButtonListeners(final Calendar nextYear, final Calendar lastYear) {
        setdate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String title = "Select date";
                showCalendarInDialog(title, R.layout.calendar_dialog);
                dialogView.setCustomDayView(new DefaultDayViewAdapter());
                dialogView.setDecorators(Collections.<CalendarCellDecorator>emptyList());
                dialogView.init(lastYear.getTime(), nextYear.getTime()) //
                        .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                        .withSelectedDate(new Date());
            }
        });
    }
    private void showCalendarInDialog(String title, int layoutResId) {
        testDate = "";
        dialogView = (CalendarPickerView) getLayoutInflater().inflate(layoutResId, null, false);
        theDialog = new AlertDialog.Builder(this) //
                .setTitle(title)
                .setView(dialogView)
                .setNeutralButton("Set", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        ArrayList<Date> selectedDates = (ArrayList<Date>)dialogView.getSelectedDates();
                        //date = selectedDates.toString();
                        //setdate.setText(date);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                        for(int iterateDate = 0; iterateDate < selectedDates.size(); iterateDate++)
                        {
                            Date tempDate = selectedDates.get(iterateDate);
                            String formattedDate = sdf.format(tempDate);
                            testDate += formattedDate;
                            //Following if is added to avoid adding comma after the last date.
                            if(iterateDate != selectedDates.size() -1)
                            {
                                testDate += ", ";
                            }
                            Log.v("testdate", testDate);
                            date = testDate;
                            setdate.setText(testDate);
                        }
                    }
                })
                .create();
        theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                Log.d("TAG", "onShow: fix the dimens!");
                dialogView.fixDialogDimens();
            }
        });
        theDialog.show();
    }

    // ASYNCTASK FOR SENDING DATA
    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SetClass.this);
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
                url = new URL("http://vidcom.click/admin/android/setClass.php");


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
                Log.v("setclassresult","data processed on async");
                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("category", params[0])
                        .appendQueryParameter("className", params[1])
                        .appendQueryParameter("classDescription", params[2])
                        .appendQueryParameter("age", params[3])
                        .appendQueryParameter("skill", params[4])
                        .appendQueryParameter("date", params[5])
                        .appendQueryParameter("time", params[6])
                        .appendQueryParameter("classSize", params[7])
                        .appendQueryParameter("cost", params[8])
                        .appendQueryParameter("privateCost", params[9])
                        .appendQueryParameter("additionalCost", params[10])
                        .appendQueryParameter("additionalCostFor", params[11])
                        .appendQueryParameter("location", params[12])
                        .appendQueryParameter("locationDetail", params[13])
                        .appendQueryParameter("email", params[14])
                        .appendQueryParameter("picture", params[15])
                        .appendQueryParameter("latitude", params[16])
                        .appendQueryParameter("longitude", params[17])
                        .appendQueryParameter("updateFlag", params[18])
                        .appendQueryParameter("classID", params[19])
                        .appendQueryParameter("keyword", params[20]);
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
            Log.v("setclassresult","post: "+result);
            //Toast.makeText(SetClass.this, result, Toast.LENGTH_SHORT).show();
            if(result.equalsIgnoreCase("true"))
            {
                Toast.makeText(SetClass.this, "Your class has been submitted", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), Mentor.class);
                startActivity(i);
            }else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("")){
                Toast.makeText(SetClass.this, "Can't submit your class to our database, please try again", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(SetClass.this, "Something went wrong, connection problem.", Toast.LENGTH_LONG).show();

            }
        }
    }
    //get class picture
    private void getImage(String profile) {
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SetClass.this, "", null,true,true);
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

                sClassPic = Base64.encodeToString(byteImage, Base64.DEFAULT);
                uploadPic.setImageBitmap(b);
            }
        }

        GetImage gi = new GetImage();
        gi.execute(profile);
    }
}
