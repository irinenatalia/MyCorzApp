package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Certificate extends AppCompatActivity {
    String skill,certificate,yearCertificate;
    ArrayList<String> alSkill,alCertificate,alYearCertificate;
    ArrayList<String> alCopySkill,alCopyCertificate,alCopyYearCertificate;

    ListView lvCertificate;
    User user;
    String email;
    public ProgressBar spinner;
    private static final Integer ADD_CERTIFICATE_VALUE = 1;
    private static final Integer UPDATE_CERTIFICATE_VALUE = 2;
    Integer flagAddCert = 1;

    String tempSkill,tempCertificate,tempYearCertificate;
    String selectedCategory,selectedSubcategory;
    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String CERTIFICATE_URL = "http://vidcom.click/admin/android/viewCertificate.php?user=";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(Certificate.this);
        if(user!=null) {
            email = user.email;
        }

        spinner=(ProgressBar)findViewById(R.id.progressBarCertificate);
        spinner.setVisibility(View.GONE);
        lvCertificate = (ListView)findViewById(R.id.listCertificate);
        alSkill = new ArrayList<String>();
        alCertificate = new ArrayList<String>();
        alYearCertificate = new ArrayList<String>();
        alCopyCertificate = new ArrayList<String>();
        alCopyYearCertificate = new ArrayList<String>();

        getJSON(CERTIFICATE_URL+email);

        Intent intent = getIntent();
        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if(extras != null){
            skill = extras.getString("skill");
            certificate = extras.getString("certificate");
            yearCertificate = extras.getString("yearCertificate");

            alSkill.add(skill);
            alCertificate.add(certificate);
            alYearCertificate.add(yearCertificate);


        }
        lvCertificate =(ListView) findViewById(R.id.listCertificate);
        lvCertificate.setAdapter(new ListCertificate(Certificate.this, alSkill,alCertificate,alYearCertificate));
        // Click event for single list row
        lvCertificate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });

        ImageButton btnBack = (ImageButton)findViewById(R.id.btnBackCert);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Certificate.this.finish();
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnAdd = (ImageButton)findViewById(R.id.btnAddCert);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddCertificate.class);
                startActivity(i);
            }
        });
        /*
        ImageButton btnSave = (ImageButton)findViewById(R.id.btnSaveCert);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (alSkill.isEmpty()) {
                    Toast.makeText(Certificate.this, "Your certificate is empty", Toast.LENGTH_LONG).show();
                }
                else {
                    JSONArray installedList = new JSONArray();
                    for (int i = 0; i < alSkill.size(); i++)
                    {
                        String category = alSkill.get(i);
                        String skill = alCopyCertificate.get(i);
                        String year = alCopyYearCertificate.get(i);
                        try{
                            JSONObject installedPackage = new JSONObject();
                            installedPackage.put("email", email);
                            installedPackage.put("category", category);
                            installedPackage.put("skill", skill);
                            installedPackage.put("year", year);
                            installedList.put(installedPackage);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    String dataToSend = installedList.toString();
                    Log.v("savecertificate", dataToSend);
                    new Certificate.AsyncSave().execute(dataToSend);
                }
            }
        });
        */
        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnUpdateCertHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnUpdateCertProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnUpdateCertSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnUpdateCertNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvUpdateCertHistory);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvUpdateCertProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvUpdateCertSetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvUpdateCertNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (requestCode == ADD_CERTIFICATE_VALUE && resultCode == SetClass.RESULT_OK) {
            tempSkill = data.getStringExtra("skill");
            tempCertificate = data.getStringExtra("certificate");
            tempYearCertificate = data.getStringExtra("yearCertificate");

            for(int i=0; i<alSkill.size(); i++){
                if(alSkill.get(i).equalsIgnoreCase(tempSkill)){
                    String newCertificate = alCertificate.get(i) + "\n" + tempCertificate;
                    String newYearCertificate = alYearCertificate.get(i) + "\n" + tempYearCertificate;

                    String newCertificate2 = alCopyCertificate.get(i) + tempCertificate;
                    String newYearCertificate2 = alCopyYearCertificate.get(i) + tempYearCertificate;
                    alCertificate.set(i, newCertificate);
                    alYearCertificate.set(i, newYearCertificate);

                    alCopyCertificate.set(i, newCertificate2);
                    alCopyYearCertificate.set(i, newYearCertificate2);

                    flagAddCert = 0;
                    Log.v("tempcertificate", ""+alCertificate.toString());

                    ((BaseAdapter) lvCertificate.getAdapter()).notifyDataSetChanged();
                }
            }
            if(flagAddCert == 1){
                alSkill.add(tempSkill);
                alCertificate.add(tempCertificate);
                alYearCertificate.add(tempYearCertificate);

                tempCertificate += ";";
                tempYearCertificate += ";";
                alCopyCertificate.add(tempCertificate);
                alCopyYearCertificate.add(tempYearCertificate);
                Log.v("tempcertificate", ""+alCertificate.toString());

                ((BaseAdapter) lvCertificate.getAdapter()).notifyDataSetChanged();
            }
        }*/
        if (requestCode == UPDATE_CERTIFICATE_VALUE && resultCode == SetClass.RESULT_OK) {
            tempSkill = data.getStringExtra("skill");
            tempCertificate = data.getStringExtra("certificate");
            tempYearCertificate = data.getStringExtra("yearCertificate");

            for(int i=0; i<alSkill.size(); i++){
                if(alSkill.get(i).equalsIgnoreCase(tempSkill)) {
                    alCertificate.set(i, tempCertificate);
                    alYearCertificate.set(i, tempYearCertificate);

                    tempCertificate += ";";
                    tempYearCertificate += ";";
                    alCopyCertificate.add(tempCertificate);
                    alCopyYearCertificate.add(tempYearCertificate);

                    ((BaseAdapter) lvCertificate.getAdapter()).notifyDataSetChanged();
                }
            }
        }
    }

    //loading certificate data
    private void getJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                spinner.setVisibility(View.VISIBLE);
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
                spinner.setVisibility(View.GONE);

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

    private void showData(){
        try {
            for(int i=0; i<result.length(); i++) {
                certificate = "";
                yearCertificate = "";
                tempCertificate = "";
                tempYearCertificate = "";
                JSONObject jsonObject = result.getJSONObject(i);

                alSkill.add(jsonObject.getString("category"));
                JSONArray detailJSON = jsonObject.getJSONArray("certificates"); //subcategory is an array that needs to be parsed again

                if(detailJSON.length() > 0) {
                    for (int j = 0; j < detailJSON.length(); j++) {
                        JSONObject jsonObject2 = detailJSON.getJSONObject(j);

                        certificate += jsonObject2.getString("name");
                        certificate += "\n";
                        yearCertificate += jsonObject2.getString("year");
                        yearCertificate += "\n";
                        tempCertificate += jsonObject2.getString("name");
                        tempCertificate += ";";
                        tempYearCertificate += jsonObject2.getString("year");
                        tempYearCertificate += ";";
                        Log.v("detailjson", certificate);
                    }
                    alCertificate.add(certificate);
                    alYearCertificate.add(yearCertificate);
                    alCopyCertificate.add(tempCertificate);
                    alCopyYearCertificate.add(tempYearCertificate);
                }
            }
            lvCertificate.setAdapter(new ListCertificate(Certificate.this, alSkill, alCertificate, alYearCertificate));
            // Click event for single list row
            lvCertificate.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ListCertificate extends BaseAdapter {
        Context context;
        ArrayList<String> skill,certificate,yearCertificate;
        private LayoutInflater inflater=null;
        public ListCertificate(Context activity, ArrayList<String> skill, ArrayList<String> certificate, ArrayList<String> yearCertificate) {
            // TODO Auto-generated constructor stub
            this.skill = skill;
            this.certificate = certificate;
            this.yearCertificate = yearCertificate;
            context=activity;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return skill.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder
        {
            TextView tvSkill,tvCertificate,tvYearCertificate;
            ImageButton btnUpdate;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder mainholder = null;
            if(convertView == null) {
                Holder holder = new Holder();
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                convertView = inflater.inflate(R.layout.list_profile_certificate, parent, false);
                holder.tvSkill = (TextView) convertView.findViewById(R.id.listCertSkill);
                holder.tvCertificate = (TextView) convertView.findViewById(R.id.listCertTitle);
                holder.tvYearCertificate = (TextView) convertView.findViewById(R.id.listCertYear);

                holder.tvSkill.setText(skill.get(position));
                holder.tvCertificate.setText(certificate.get(position));
                holder.tvYearCertificate.setText(yearCertificate.get(position));

                holder.btnUpdate = (ImageButton) convertView.findViewById(R.id.listCertUpdate);

                convertView.setTag(holder);
            }

            mainholder = (Holder) convertView.getTag();
            mainholder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), UpdateCertificate.class);
                    i.putExtra("category",alSkill.get(position));
                    startActivity(i);
                }
            });
            return convertView;
        }
    }
}
