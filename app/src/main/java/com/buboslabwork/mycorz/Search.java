package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class Search extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    ArrayList<String> arrayCategory;
    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String CATEGORY_URL = "http://vidcom.click/admin/android/viewCategoryMain.php";
    private static final String DETAIL_URL = "http://vidcom.click/admin/android/viewDetailCategory.php?category=";

    AutoCompleteTextView category;
    String categorySearchResult;
    ImageButton search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //DISPLAYING CATEGORY AUTOCOMPLETE
        arrayCategory = new ArrayList<String>();
        getCategoryJSON(CATEGORY_URL);

        search = (ImageButton)findViewById(R.id.iconSearchSearch);

        ImageButton back = (ImageButton)findViewById(R.id.btnBackSearch);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Search.this.finish();
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
                loading = ProgressDialog.show(Search.this, "Loading...",null,true,true);
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
                    Toast.makeText(Search.this, "Something went wrong with our network", Toast.LENGTH_LONG).show();
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
            category=(AutoCompleteTextView)findViewById(R.id.searchAutocomplete);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayCategory);

            category.setAdapter(adapter);

            search.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(category.getText().toString() != null || !category.getText().toString().equalsIgnoreCase("")) {
                        String stringCategory = category.getText().toString();
                        Intent i = new Intent(getApplicationContext(), ClassEmpty.class);
                        i.putExtra("category",stringCategory);
                        startActivity(i);
                    }
                    else
                        Toast.makeText(Search.this, "You haven't searched any category here", Toast.LENGTH_SHORT).show();
                }
            });
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
    //GETTING CATEGORY DETAIL
    private void getDetailCategoryJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(Search.this);
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
                    Toast.makeText(Search.this, "Class Not Available", Toast.LENGTH_LONG).show();
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
