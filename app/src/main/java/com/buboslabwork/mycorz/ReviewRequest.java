package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReviewRequest extends AppCompatActivity {
    User user;
    ListView lvRequest;
    View rowView;
    private SwipeRefreshLayout swipeRefreshLayout;
    public ProgressBar spinner;

    private static final String REVIEW_URL = "http://vidcom.click/admin/android/reviewRequest.php?mentor=";
    String email;

    int[] studentPicture = {R.drawable.jokowi, R.drawable.jokowi, R.drawable.jokowi};
    ArrayList<String> orderID,name,category,location,className,requestDate,classDate,classTime,type,status,profilePicture;
    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public String classStatus; //class status: PENDING, ACCEPTED, REJECTED, COMPLETED

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_request);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(ReviewRequest.this);
        if(user!=null){
            email = user.email;
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.reviewRequestSwipeRefreshLayout);
        spinner=(ProgressBar)findViewById(R.id.progressBarReviewRequest);
        spinner.setVisibility(View.VISIBLE);

        orderID = new ArrayList<String>();
        name = new ArrayList<String>();
        category = new ArrayList<String>();
        location = new ArrayList<String>();
        className = new ArrayList<String>();
        requestDate = new ArrayList<String>();
        classDate = new ArrayList<String>();
        classTime = new ArrayList<String>();
        type = new ArrayList<String>();
        status = new ArrayList<String>();
        profilePicture = new ArrayList<String>();

        //set refresh gesture when user swipe down
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("swiperesult", "onRefresh called from SwipeRefreshLayout");

                        // Signal SwipeRefreshLayout to start the progress indicator
                        swipeRefreshLayout.setRefreshing(true);

                        orderID.clear();
                        name.clear();
                        category.clear();
                        location.clear();
                        className.clear();
                        requestDate.clear();
                        classDate.clear();
                        classTime.clear();
                        type.clear();
                        status.clear();
                        profilePicture.clear();
                        fetchClass();
                    }
                }
        );

        //fetch class list asynchronously with volley
        fetchClass();

        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackReviewRequest);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Mentor.class);
                startActivity(i);
            }
        });
    }
    private void fetchClass(){
        // Creating volley request obj
        StringRequest reviewReq = new StringRequest(REVIEW_URL+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("volleyresult", response);
                        if(response.equalsIgnoreCase("") || response.equalsIgnoreCase("false")){
                            spinner.setVisibility(View.GONE);
                            TextView warning = (TextView)findViewById(R.id.warningReviewRequest);
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

                                    category.add(jsonObject.getString("category"));
                                    className.add(jsonObject.getString("class_name"));
                                    orderID.add(jsonObject.getString("order_id"));
                                    name.add(jsonObject.getString("complete_name"));
                                    location.add(jsonObject.getString("location"));
                                    requestDate.add(jsonObject.getString("request_date"));
                                    classDate.add(jsonObject.getString("class_date"));
                                    classTime.add(jsonObject.getString("time"));
                                    type.add(jsonObject.getString("type"));
                                    status.add(jsonObject.getString("status"));
                                    profilePicture.add(jsonObject.getString("profile_picture"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);

                            lvRequest = (ListView) findViewById(R.id.listReviewRequest);
                            lvRequest.setAdapter(new ListReviewRequest(ReviewRequest.this, orderID, name, category, className, classDate, location, type, status, requestDate, profilePicture));
                            // Click event for single list row
                            lvRequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        final int position, long id) {

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
        VolleyAppController.getInstance().addToRequestQueue(reviewReq);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        spinner.setVisibility(View.GONE);
    }

    // LISTVIEW ADAPTER
    public class ListReviewRequest extends BaseAdapter {
        ArrayList<String> orderID,name,category,className,datetime,address,type,status,requestTime,profilePicture;
        ImageLoader imageLoader = VolleyAppController.getInstance().getImageLoader();
        Context context;
        private LayoutInflater inflater=null;
        public ListReviewRequest(Context activity, ArrayList<String> orderID, ArrayList<String> name, ArrayList<String> category, ArrayList<String> className, ArrayList<String> datetime, ArrayList<String> address, ArrayList<String> type, ArrayList<String> status, ArrayList<String> requestTime, ArrayList<String> profilePicture) {
            this.orderID = orderID;
            this.name = name;
            this.category = category;
            this.className = className;
            this.datetime = datetime;
            this.address = address;
            this.type = type;
            this.status = status;
            this.requestTime = requestTime;
            this.profilePicture = profilePicture;
            context=activity;
            //inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name.size();
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder mainholder = null;
            if(convertView == null) {
                Holder holder = new Holder();
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                convertView = inflater.inflate(R.layout.list_review_request, parent, false);
                holder.tvName = (TextView) convertView.findViewById(R.id.listReviewName);
                holder.tvCategory = (TextView) convertView.findViewById(R.id.listReviewCategory);
                holder.tvClassName = (TextView) convertView.findViewById(R.id.listReviewClassName);
                holder.tvDatetime = (TextView) convertView.findViewById(R.id.listReviewDateTime);
                holder.tvAddress = (TextView) convertView.findViewById(R.id.listReviewAddress);
                holder.tvType = (TextView) convertView.findViewById(R.id.listReviewType);
                holder.tvReviewRequestTime = (TextView) convertView.findViewById(R.id.listReviewRequestTime);

                if (imageLoader == null)
                    imageLoader = VolleyAppController.getInstance().getImageLoader();
                CircularNetworkImageView thumbnail = (CircularNetworkImageView) convertView
                        .findViewById(R.id.listReviewPicture);

                thumbnail.setImageUrl(profilePicture.get(position), imageLoader);

                holder.tvName.setText(name.get(position));
                holder.tvCategory.setText(category.get(position));
                holder.tvClassName.setText(className.get(position));
                holder.tvDatetime.setText(datetime.get(position));
                holder.tvAddress.setText(address.get(position));
                holder.tvType.setText(type.get(position));
                holder.tvReviewRequestTime.setText(requestTime.get(position));

                holder.accept = (Button) convertView.findViewById(R.id.btnRequestAccept);
                holder.reject = (Button) convertView.findViewById(R.id.btnRequestReject);
                holder.completed = (Button) convertView.findViewById(R.id.btnRequestCompleted);
                if(status.get(position).equalsIgnoreCase("ACCEPTED")){ //if status is accepted
                    holder.accept.setBackgroundResource(R.drawable.pill_rounded_grey_fill);
                    holder.reject.setBackgroundResource(R.drawable.pill_rounded_grey_fill);
                    holder.completed.setBackgroundResource(R.drawable.pill_rounded_tosca_fill);
                    holder.accept.setClickable(false);
                    holder.accept.setEnabled(false);
                    holder.reject.setClickable(false);
                    holder.reject.setEnabled(false);
                    holder.completed.setClickable(true);
                    holder.completed.setEnabled(true);
                }
                if(status.get(position).equalsIgnoreCase("REJECTED")){ //if status is rejected
                    holder.accept.setBackgroundResource(R.drawable.pill_rounded_grey_fill);
                    holder.reject.setBackgroundResource(R.drawable.pill_rounded_grey_fill);
                    holder.completed.setBackgroundResource(R.drawable.pill_rounded_grey_fill);
                    holder.accept.setClickable(false);
                    holder.accept.setEnabled(false);
                    holder.reject.setClickable(false);
                    holder.reject.setEnabled(false);
                    holder.completed.setClickable(false);
                    holder.completed.setEnabled(false);
                }
                convertView.setTag(holder);
            }

            mainholder = (Holder) convertView.getTag();
            mainholder.accept.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(status.get(position).equalsIgnoreCase("PENDING")) { //if status not accepted
                        Intent i = new Intent(getApplicationContext(), AcceptRequest.class);
                        i.putExtra("orderID", orderID.get(position));
                        startActivity(i);
                    }
                }
            });
            mainholder.reject.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(status.get(position).equalsIgnoreCase("PENDING")) { //if status not accepted
                        new AlertDialog.Builder(context)
                                .setTitle("Reject request")
                                .setMessage("Are you sure you want to reject this request?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        classStatus = "REJECTED";
                                        new ReviewRequest.AsyncAction().execute(orderID.get(position), "REJECTED");
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            });
            mainholder.completed.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(status.get(position).equalsIgnoreCase("ACCEPTED")) {
                        Intent i = new Intent(getApplicationContext(), CompletedRequest.class);
                        i.putExtra("orderID", orderID.get(position));
                        i.putExtra("studentName", name.get(position));
                        i.putExtra("studentProfile", profilePicture.get(position));
                        startActivity(i);
                    }
                }
            });
            return convertView;
        }
    }
    public class Holder
    {
        TextView tvName,tvCategory,tvClassName,tvDatetime,tvAddress,tvType,tvReviewRequestTime;
        Button accept,reject,completed;
    }
    private class AsyncAction extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ReviewRequest.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tProcessing data...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("http://vidcom.click/admin/android/reviewRequestAction.php");

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
                        .appendQueryParameter("orderID", params[0])
                        .appendQueryParameter("action", params[1]);
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
            Log.v("reviewresult",""+result);
            if(result.equalsIgnoreCase("true"))
            {
                //Toast.makeText(getApplicationContext(), "Thank you. We will inform your password by email.", Toast.LENGTH_LONG).show();
                if(classStatus.equalsIgnoreCase("ACCEPTED")) {
                    Intent i = new Intent(getApplicationContext(), AcceptRequest.class);
                    startActivity(i);
                }
                else if(classStatus.equalsIgnoreCase("REJECTED")) {
                    Intent i = new Intent(getApplicationContext(), RejectRequest.class);
                    startActivity(i);
                }
                else if(classStatus.equalsIgnoreCase("COMPLETED")) {
                    Intent i = new Intent(getApplicationContext(), CompletedRequest.class);
                    startActivity(i);
                }
            }else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("")){
                // If username and password does not match display a error message
                Toast.makeText(getApplicationContext(), "Something went wrong when processing your data, please try again.", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(getApplicationContext(), "Something went wrong, connection problem.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
