package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
    private static final String KEYWORD_URL = "http://vidcom.click/admin/android/searchCategory.php?keyword=";
    private static final String DETAIL_URL = "http://vidcom.click/admin/android/viewDetailCategory.php?category=";

    ArrayAdapter<String> adapter;
    AutoCompleteTextView category;
    String categorySearchResult;
    ImageButton search;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        arrayCategory = new ArrayList<String>();
        spinner = (ProgressBar)findViewById(R.id.progressBarSearch);
        spinner.setVisibility(View.GONE);
        category=(AutoCompleteTextView)findViewById(R.id.searchAutocomplete);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayCategory);
        adapter.setNotifyOnChange(true);
        category.setAdapter(adapter);
        category.setThreshold(3);

        final TextWatcher textChecker = new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(category.getText().toString().length() > 2) {
                    Log.v("searchresult", "" + category.getText().toString());
                    spinner.setVisibility(View.VISIBLE);
                    fetchClass();
                }
            }
        };

        //SET LISTENER EVERYTIME USER TYPE
        category.addTextChangedListener(textChecker);

        //THIS IS NOT USED, AS WE ALREADY USE TEXTWATCHER TO FETCH JSON DATA
        //THIS IS ONLY FOR DISPLAYING THE FIRST TIME - USE IT AS A TEST ONLY
        //DISPLAYING CATEGORY AUTOCOMPLETE
        //getCategoryJSON(CATEGORY_URL);

        search = (ImageButton)findViewById(R.id.iconSearchSearch);

        ImageButton back = (ImageButton)findViewById(R.id.btnBackSearch);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Search.this.finish();
            }
        });
    }

    //FETCH CATEGORY RESULT BY TEXTWATCHER
    private void fetchClass(){
        // Creating volley request obj
        StringRequest movieReq = new StringRequest(KEYWORD_URL+category.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("searchresult", "json: "+response);
                        if(response.equalsIgnoreCase("") || response.equalsIgnoreCase("false")){
                            spinner.setVisibility(View.GONE);
                        }
                        else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                result = jsonObject.getJSONArray(JSON_ARRAY);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            spinner.setVisibility(View.GONE);

                            //Log.d("searchresult", "length: "+String.valueOf(result.length()));
                            // Parsing json
                            for (int i = 0; i < result.length(); i++) {
                                try {
                                    JSONObject jsonObject = result.getJSONObject(i);
                                    arrayCategory.add(jsonObject.getString("subcategory"));
                                    Log.v("searchresult","sub: "+arrayCategory.get(i));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //notify adapter to update on autocompletetextview
                            adapter.notifyDataSetChanged();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse errorRes = error.networkResponse;
                //Log.e("VolleyError",errorRes.toString());
                if(errorRes != null && errorRes.data != null){
                    //Log.e("VolleyError",errorRes.toString());
                }

                spinner.setVisibility(View.GONE);
            }
        });

        // Adding request to request queue
        VolleyAppController.getInstance().addToRequestQueue(movieReq);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        spinner.setVisibility(View.GONE);
    }

    //GET CATEGORY LIST VIA ASYNC
    //THIS IS NOT USED - FETCHING DATA ALREADY DONE WITH VOLLEY
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
    //THIS IS NOT USED - FETCHING DATA ALREADY DONE WITH VOLLEY
    private void extractJSON(String s){
        try {
            JSONObject jsonObject = new JSONObject(s);
            result = jsonObject.getJSONArray(JSON_ARRAY);
            showData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //THIS IS NOT USED - FETCHING DATA ALREADY DONE WITH VOLLEY
    private void showData(){
        try {
            for(int i=0; i<result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);
                arrayCategory.add(jsonObject.getString("category"));
            }
            //category=(AutoCompleteTextView)findViewById(R.id.searchAutocomplete);
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayCategory);

            //category.setAdapter(adapter);
            adapter.notifyDataSetChanged();

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
