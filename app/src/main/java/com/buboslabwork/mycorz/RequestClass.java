package com.buboslabwork.mycorz;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.buboslabwork.mycorz.ui.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RequestClass extends AppCompatActivity {
    User user;
    private SwipeRefreshLayout swipeRefreshLayout;
    public ProgressBar spinner;
    ListView lvRequestClass;
    ArrayList<String> requestID,requestby,category,title,ageLevel,skillLevel,date,time,classSize;

    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";

    private static final String REQUEST_URL = "http://vidcom.click/admin/android/viewRequestNewClass.php?mentor=";
    String email;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_class);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        requestID = new ArrayList<String>();
        requestby = new ArrayList<String>();
        category = new ArrayList<String>();
        title = new ArrayList<String>();
        ageLevel = new ArrayList<String>();
        skillLevel = new ArrayList<String>();
        date = new ArrayList<String>();
        time = new ArrayList<String>();
        classSize = new ArrayList<String>();

        user=PrefUtils.getCurrentUser(RequestClass.this);
        if(user!=null){
            email = user.email;
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.requestClassSwipeRefreshLayout);
        spinner=(ProgressBar)findViewById(R.id.progressBarRequestClass);
        spinner.setVisibility(View.VISIBLE);

        //set refresh gesture when user swipe down
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("swipehomeresult", "onRefresh called from SwipeRefreshLayout");

                        // Signal SwipeRefreshLayout to start the progress indicator
                        swipeRefreshLayout.setRefreshing(true);

                        requestID.clear();
                        requestby.clear();
                        category.clear();
                        title.clear();
                        ageLevel.clear();
                        skillLevel.clear();
                        date.clear();
                        time.clear();
                        classSize.clear();
                        fetchClass();
                    }
                }
        );

        //fetch class list asynchronously with volley
        fetchClass();

        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackRequestClass);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Mentor.class);
                startActivity(i);
            }
        });
    }
    private void fetchClass(){
        // Creating volley request obj
        StringRequest movieReq = new StringRequest(REQUEST_URL+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("homevolleyresult", response);
                        if(response.equalsIgnoreCase("") || response.equalsIgnoreCase("false")){
                            spinner.setVisibility(View.GONE);
                            TextView warning = (TextView)findViewById(R.id.warningRequestClass);
                            warning.setText("There is no available class request right now");
                        }
                        else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                result = jsonObject.getJSONArray(JSON_ARRAY);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            spinner.setVisibility(View.GONE);

                            Log.d("volleyresult", String.valueOf(result.length()));
                            // Parsing json
                            for (int i = 0; i < result.length(); i++) {
                                try {
                                    JSONObject jsonObject = result.getJSONObject(i);

                                    requestID.add(jsonObject.getString("request_id"));
                                    requestby.add(jsonObject.getString("requested_by"));
                                    ageLevel.add(jsonObject.getString("age_level"));
                                    category.add(jsonObject.getString("category"));
                                    title.add(jsonObject.getString("title"));
                                    skillLevel.add(jsonObject.getString("skill_level"));
                                    date.add(jsonObject.getString("date"));
                                    time.add(jsonObject.getString("time"));
                                    classSize.add(jsonObject.getString("class_size"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);

                            lvRequestClass = (ListView) findViewById(R.id.listRequestClass);
                            lvRequestClass.setAdapter(new ListRequestClass(RequestClass.this, title, category, ageLevel, skillLevel, classSize, date, time, requestby));
                            //lvRequestClass.setItemsCanFocus(true);
                            // Click event for single list row
                            lvRequestClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {

                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse errorRes = error.networkResponse;
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                Log.e("VolleyError",errorRes.toString());
                if(errorRes != null && errorRes.data != null){
                    Log.e("VolleyError",errorRes.toString());
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

    public class ListRequestClass extends BaseAdapter {
        ArrayList<String> title,requestby,category,ageLevel,skillLevel,classSize,date,time;
        Context context;
        private LayoutInflater inflater=null;
        public ListRequestClass(Context activity, ArrayList<String> title, ArrayList<String> category, ArrayList<String> ageLevel, ArrayList<String> skillLevel, ArrayList<String> classSize, ArrayList<String> date, ArrayList<String> time, ArrayList<String> requestby) {
            // TODO Auto-generated constructor stub
            this.title = title;
            this.requestby = requestby;
            this.category = category;
            this.ageLevel = ageLevel;
            this.skillLevel = skillLevel;
            this.classSize = classSize;
            this.date = date;
            this.time = time;
            context=activity;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return category.size();
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
            TextView tvRequestBy,tvTitle,tvCategory,tvAgeLevel,tvSkillLevel,tvClassSize,tvDate,tvTime;
            ImageButton btnMessage,btnSetClass;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder mainholder = null;
            if(convertView == null) {
                Holder holder=new Holder();
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                convertView = inflater.inflate(R.layout.list_request_class, parent, false);

                holder.tvRequestBy=(TextView) convertView.findViewById(R.id.request_requestedBy);
                holder.tvTitle=(TextView) convertView.findViewById(R.id.request_title);
                holder.tvCategory=(TextView) convertView.findViewById(R.id.request_category);
                holder.tvAgeLevel=(TextView) convertView.findViewById(R.id.request_ageLevel);
                holder.tvSkillLevel=(TextView) convertView.findViewById(R.id.request_skillLevel);
                holder.tvClassSize=(TextView) convertView.findViewById(R.id.request_classSize);
                holder.tvDate=(TextView) convertView.findViewById(R.id.request_date);
                holder.tvTime=(TextView) convertView.findViewById(R.id.request_time);
                holder.btnMessage=(ImageButton) convertView.findViewById(R.id.btn_request_message);
                holder.btnSetClass=(ImageButton) convertView.findViewById(R.id.btn_request_setclass);

                holder.tvRequestBy.setText(requestby.get(position));
                holder.tvTitle.setText(title.get(position));
                holder.tvCategory.setText(category.get(position));
                holder.tvAgeLevel.setText(ageLevel.get(position));
                holder.tvSkillLevel.setText(skillLevel.get(position));
                holder.tvClassSize.setText(classSize.get(position));
                holder.tvDate.setText(date.get(position));
                holder.tvTime.setText(time.get(position));

                convertView.setTag(holder);
            }
            mainholder = (Holder) convertView.getTag();
            mainholder.btnMessage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                    i.putExtra("tujuan",requestby.get(position));
                    startActivity(i);
                }
            });
            mainholder.btnSetClass.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), SetClass.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("age_level", ageLevel.get(position));
                    mBundle.putString("category", category.get(position));
                    mBundle.putString("title", title.get(position));
                    mBundle.putString("skill_level", skillLevel.get(position));
                    mBundle.putString("date", date.get(position));
                    mBundle.putString("time", time.get(position));
                    mBundle.putString("class_size", classSize.get(position));
                    mBundle.putString("pageSource", "RequestClass");
                    i.putExtras(mBundle);
                    startActivity(i);
                }
            });

            return convertView;
        }
    }
}
