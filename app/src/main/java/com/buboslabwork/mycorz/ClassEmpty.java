package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ClassEmpty extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    ArrayList<String> arrayCategory;
    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String CATEGORY_URL = "http://vidcom.click/admin/android/viewCategoryMain.php";
    private static final String DETAIL_URL = "http://vidcom.click/admin/android/viewDetailCategory.php?category=";

    AutoCompleteTextView category;
    String categorySearchResult,paramCategory;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_empty);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Intent i = new Intent();
        if(i!=null){
            paramCategory = i.getStringExtra("category");
            //Log.v("paramcategory",paramCategory);
        }

        //DISPLAYING CATEGORY AUTOCOMPLETE
        arrayCategory = new ArrayList<String>();
        getCategoryJSON(CATEGORY_URL);

        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackClassEmpty);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Category.class);
                startActivity(i);
            }
        });

        Button addWishlist = (Button)findViewById(R.id.addWishlist);
        addWishlist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddWishlist.class);
                if(paramCategory != null)
                    i.putExtra("category",paramCategory);
                startActivity(i);
            }
        });
    }
    //GET CATEGORY LIST VIA ASYNC
    private void getCategoryJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ClassEmpty.this, "Loading...",null,true,true);
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
                    return "exception";
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                myJSONString = s;
                if(s.equalsIgnoreCase("") || s.equalsIgnoreCase("false")){
                    TextView warning = (TextView)findViewById(R.id.warningReviewRequest);
                    warning.setText("There is no available request on your class right now");
                }
                else if(s.equalsIgnoreCase("exception"))
                    Toast.makeText(ClassEmpty.this, "Something went wrong with our network", Toast.LENGTH_LONG).show();
                else{
                    extractJSON(s);
                }
            }
        }
        getCategoryJSON gj = new getCategoryJSON();
        gj.execute(url);
    }
    private void extractJSON(String s){
        try {
            JSONObject jsonObject = new JSONObject(s);
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
                arrayCategory.add(jsonObject.getString("category"));
            }
            category=(AutoCompleteTextView)findViewById(R.id.searchAutocompleteClassEmpty);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayCategory);

            category.setAdapter(adapter);
            category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    categorySearchResult = parent.getItemAtPosition(position).toString();
                    String replaced = categorySearchResult.replace("&", "%26").replace(' ', '_');
                    Log.v("categoryresult",DETAIL_URL+replaced);
                    getDetailCategoryJSON(DETAIL_URL+replaced);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //END GETTING CATEGORY LIST

    //GETTING CATEGORY DETAIL
    private void getDetailCategoryJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(ClassEmpty.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading.setMessage("\tSearching...");
                pdLoading.setCancelable(false);
                pdLoading.show();
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
                pdLoading.dismiss();

                Log.v("categoryresult",s);
                if(s.equalsIgnoreCase("") || s.equalsIgnoreCase("false")){
                    Intent i = new Intent(getApplicationContext(), ClassEmpty.class);
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(getApplicationContext(), MentorList.class);
                    i.putExtra("jsonstring", s);
                    i.putExtra("category", categorySearchResult);
                    startActivity(i);
                }
            }
        }
        getCategoryJSON gj = new getCategoryJSON();
        gj.execute(url);
    }
    //END GETTING CATEGORY DETAIL
}
