package com.buboslabwork.mycorz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TempCategory extends AppCompatActivity {
    CategoryAdapter listAdapter;
    ExpandableListView expListView;
    HashMap<String, List<String>> listDataChild;
    public ProgressBar spinner;

    ArrayList<String> category,detail,categoryIcon;
    String selectedCategory,selectedSubcategory;
    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String CATEGORY_URL = "http://vidcom.click/admin/android/viewCategory.php";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_category);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        spinner=(ProgressBar)findViewById(R.id.progressBarTempCategory);
        spinner.setVisibility(View.VISIBLE);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.tempCategoryListview);

        category = new ArrayList<String>();
        detail = new ArrayList<String>();
        categoryIcon = new ArrayList<String>();
        fetchCategory();

        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackTempCategory);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TempCategory.this.finish();
            }
        });
    }
    //GETTING CATEGORY LIST
    private void fetchCategory(){
        // Creating volley request obj
        StringRequest movieReq = new StringRequest(CATEGORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("homevolleyresult", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            result = jsonObject.getJSONArray(JSON_ARRAY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spinner.setVisibility(View.GONE);

                        Log.d("homevolleyresult", String.valueOf(result.length()));
                        // Parsing json
                        listDataChild = new HashMap<String, List<String>>();

                        for(int i=0; i<result.length(); i++) {
                            try{
                                JSONObject jsonObject = result.getJSONObject(i);

                                category.add(jsonObject.getString("category"));
                                categoryIcon.add(jsonObject.getString("category_icon"));
                                detail.add(jsonObject.getString("subcategory"));
                                JSONArray detailJSON = jsonObject.getJSONArray("subcategory"); //subcategory is an array that needs to be parsed again

                                List<String> listDetailJSON = new ArrayList<String>(); //so that detailJSON can be put into listDataChild
                                if(detailJSON.length() > 0) {
                                    for (int j = 0; j < detailJSON.length(); j++) {
                                        listDetailJSON.add(detailJSON.get(j).toString());
                                        //Log.v("detailjson", detailJSON.optString(j));
                                    }
                                }
                                listDataChild.put(category.get(i), listDetailJSON);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        listAdapter = new CategoryAdapter(TempCategory.this, category, listDataChild, categoryIcon);
                        // setting list adapter
                        expListView.setAdapter(listAdapter);

                        // Listview Group click listener
                        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v,
                                                        int groupPosition, long id) {
                                // Toast.makeText(getApplicationContext(),
                                // "Group Clicked " + listDataHeader.get(groupPosition),
                                // Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });

                        // Listview Group expanded listener
                        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                            @Override
                            public void onGroupExpand(int groupPosition) {
                                int len = listAdapter.getGroupCount();
                                for (int i = 0; i < len; i++) {
                                    if (i != groupPosition) {
                                        expListView.collapseGroup(i);
                                    }
                                }
                            }
                        });

                        // Listview Group collasped listener
                        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                            @Override
                            public void onGroupCollapse(int groupPosition) {

                            }
                        });

                        // Listview on child click listener
                        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v,
                                                        int groupPosition, int childPosition, long id) {
                                selectedCategory = category.get(groupPosition);
                                selectedSubcategory = listDataChild.get(category.get(groupPosition)).get(childPosition).toString();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("returnedCategoryParam", selectedSubcategory);
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                                return false;
                            }
                        });
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
    //END GETTING CATEGORY
}
