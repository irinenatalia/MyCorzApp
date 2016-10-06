package com.buboslabwork.mycorz;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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

public class MentorDetailClassAsMentor extends AppCompatActivity {
    User user;
    ListView lvDetailClass;
    ArrayList<String> category,classID,className,classDescription,classSize,date,location,locationDetail,time;
    ArrayList<String> ageLevel,skillLevel,cost,privateCost,additionalCost,additionalCostFor,latitude,longitude,classPicture;
    String profile,complete_name;
    String mentorUsername,pageSource;
    String email;
    String bitmapProfileEncoded;

    ProgressDialog loading;

    public JSONArray result = null;
    public String myJSONString;
    private static final String JSON_ARRAY ="result";
    private static final String CLASS_URL = "http://vidcom.click/admin/android/mentorDetailClass.php?mentor=";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    CircleImageView displayProfile;
    TextView displayName;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mentor_detail_class_as_mentor);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Intent i = getIntent();
        if(i != null){
            mentorUsername = i.getStringExtra("mentorUsername");
            pageSource = i.getStringExtra("pageSource");

            Log.v("mentordetailclass", mentorUsername);

            loading = ProgressDialog.show(MentorDetailClassAsMentor.this, "Loading...",null,true,true);
            fetchClass();
        }

        user=PrefUtils.getCurrentUser(MentorDetailClassAsMentor.this);
        if(user != null){
            email = user.email;
        }

        displayProfile = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.mentorDetailAsMentorPicture);
        displayName = (TextView)findViewById(R.id.nameMentorDetailAsMentor);

        category = new ArrayList<String>();
        classID = new ArrayList<String>();
        className = new ArrayList<String>();
        classDescription = new ArrayList<String>();
        classSize = new ArrayList<String>();
        date = new ArrayList<String>();
        location = new ArrayList<String>();
        locationDetail = new ArrayList<String>();
        ageLevel = new ArrayList<String>();
        skillLevel = new ArrayList<String>();
        time = new ArrayList<String>();
        cost = new ArrayList<String>();
        privateCost = new ArrayList<String>();
        additionalCost = new ArrayList<String>();
        additionalCostFor = new ArrayList<String>();
        latitude = new ArrayList<String>();
        longitude = new ArrayList<String>();
        classPicture = new ArrayList<String>();

        ImageButton certificate = (ImageButton)findViewById(R.id.mentorDetailAsMentorCert);
        certificate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("mentordetail","mentor detail cert clicked");
                Intent i = new Intent(getApplicationContext(), MentorDetailData.class);
                i.putExtra("mentorUsername", mentorUsername);
                i.putExtra("completeName", complete_name);
                i.putExtra("profile", bitmapProfileEncoded);
                i.putExtra("pageSource", pageSource);

                Log.v("mentordetail","username: "+mentorUsername);
                Log.v("mentordetail","name: "+complete_name);
                Log.v("mentordetail","profile: "+bitmapProfileEncoded);
                Log.v("mentordetail","page source: "+pageSource);
                startActivity(i);
            }
        });
        ImageButton testimonial = (ImageButton)findViewById(R.id.mentorDetailAsMentorTesti);
        testimonial.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("mentordetail","mentor detail testi clicked");
                Intent i = new Intent(getApplicationContext(), MentorDetailTesti.class);
                i.putExtra("mentorUsername", mentorUsername);
                i.putExtra("completeName", complete_name);
                i.putExtra("profile", bitmapProfileEncoded);
                i.putExtra("pageSource", pageSource);
                startActivity(i);
            }
        });
        ImageButton chat = (ImageButton)findViewById(R.id.mentorDetailAsMentorChat);
        chat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MentorDetailChat.class);
                i.putExtra("mentorUsername", mentorUsername);
                i.putExtra("pageSource", pageSource);
                i.putExtra("completeName", complete_name);
                i.putExtra("profile", bitmapProfileEncoded);
                startActivity(i);
            }
        });
        ImageButton back = (ImageButton)findViewById(R.id.btnBackMentorDetailAsMentor);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(pageSource.equalsIgnoreCase("Home")) {
                    Intent i = new Intent(getApplicationContext(), Home.class);
                    startActivity(i);
                }
                else if(pageSource.equalsIgnoreCase("MentorList")) {
                    Intent i = new Intent(getApplicationContext(), Home.class);
                    startActivity(i);
                }
                else if(pageSource.equalsIgnoreCase("Mentor")) {
                    Intent i = new Intent(getApplicationContext(), Mentor.class);
                    startActivity(i);
                }
            }
        });
    }
    private void fetchClass(){
        // Creating volley request obj
        StringRequest movieReq = new StringRequest(CLASS_URL+mentorUsername,
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
                        loading.dismiss();

                        Log.d("homevolleyresult", String.valueOf(result.length()));
                        // Parsing json
                        try {
                            for(int i=0; i<result.length(); i++) {
                                JSONObject jsonObject = result.getJSONObject(i);

                                profile = jsonObject.getString("profile");
                                complete_name = jsonObject.getString("complete_name");
                                category.add(jsonObject.getString("category"));
                                classID.add(jsonObject.getString("id"));
                                className.add(jsonObject.getString("class_name"));
                                classDescription.add(jsonObject.getString("class_description"));
                                classSize.add(jsonObject.getString("class_size"));
                                ageLevel.add(jsonObject.getString("age_level"));
                                skillLevel.add(jsonObject.getString("skill_level"));
                                date.add(jsonObject.getString("date"));
                                time.add(jsonObject.getString("time"));
                                location.add(jsonObject.getString("location"));
                                locationDetail.add(jsonObject.getString("location_detail"));
                                cost.add(jsonObject.getString("cost"));
                                privateCost.add(jsonObject.getString("private_cost"));
                                additionalCost.add(jsonObject.getString("additional_cost"));
                                additionalCostFor.add(jsonObject.getString("additional_cost_for"));
                                latitude.add(jsonObject.getString("latitude"));
                                longitude.add(jsonObject.getString("longitude"));
                                classPicture.add(jsonObject.getString("classPicture"));
                            }

                            displayName.setText(complete_name);
                            if(!profile.isEmpty() || !profile.equalsIgnoreCase("")){
                                getImage(profile);
                            }

                            lvDetailClass =(ListView) findViewById(R.id.lvMentorDetailAsMentor);
                            LayoutInflater inflater = getLayoutInflater();

                            lvDetailClass.setAdapter(new ListMentorDetailClass(MentorDetailClassAsMentor.this, category,classID,className,classDescription,classSize,date,time,ageLevel,skillLevel,location,locationDetail,cost,privateCost,additionalCost,additionalCostFor,latitude,longitude));
                            // Click event for single list row
                            lvDetailClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    //when listview is clicked, do nothing
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
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

                loading.dismiss();
            }
        });

        // Adding request to request queue
        VolleyAppController.getInstance().addToRequestQueue(movieReq);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        loading.dismiss();
    }


    //get mentor profile picture
    private void getImage(String profile) {
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MentorDetailClassAsMentor.this, "", null,true,true);
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

                bitmapProfileEncoded = Base64.encodeToString(byteImage, Base64.DEFAULT);
                displayProfile.setImageBitmap(b);
            }
        }

        GetImage gi = new GetImage();
        gi.execute(profile);
    }

    // LISTVIEW ADAPTER
    public class ListMentorDetailClass extends BaseAdapter {
        ArrayList<String> category,classID,name,description,classSize,ageLevel,skillLevel,date,time;
        ArrayList<String> location,locationDetail,cost,privateCost,additionalCost,additionalCostFor,latitude,longitude;
        Context context;
        private LayoutInflater inflater=null;
        public ListMentorDetailClass(Context activity, ArrayList<String> category, ArrayList<String> classID, ArrayList<String> name, ArrayList<String> description, ArrayList<String> classSize, ArrayList<String> date, ArrayList<String> time, ArrayList<String> ageLevel, ArrayList<String> skillLevel, ArrayList<String> location, ArrayList<String> locationDetail, ArrayList<String> cost, ArrayList<String> privateCost, ArrayList<String> additionalCost, ArrayList<String> additionalCostFor, ArrayList<String> latitude, ArrayList<String> longitude) {
            this.category = category;
            this.classID = classID;
            this.name = name;
            this.description = description;
            this.classSize = classSize;
            this.date = date;
            this.time = time;
            this.ageLevel = ageLevel;
            this.skillLevel = skillLevel;
            this.location = location;
            this.locationDetail = locationDetail;
            this.cost = cost;
            this.privateCost = privateCost;
            this.additionalCost = additionalCost;
            this.additionalCostFor = additionalCostFor;
            this.latitude = latitude;
            this.longitude = longitude;
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
                convertView = inflater.inflate(R.layout.list_mentor_detail_as_mentor, parent, false);
                holder.tvName = (TextView) convertView.findViewById(R.id.listMDAsMentorName);
                holder.tvAgeLevel = (TextView) convertView.findViewById(R.id.listMDAsMentorAge);
                holder.tvSkillLevel = (TextView) convertView.findViewById(R.id.listMDAsMentorSkill);
                holder.tvDate = (TextView) convertView.findViewById(R.id.listMDAsMentorDate);
                holder.tvTime = (TextView) convertView.findViewById(R.id.listMDAsMentorTime);
                holder.tvLocation = (TextView) convertView.findViewById(R.id.listMDAsMentorLocation);
                holder.tvCost = (TextView) convertView.findViewById(R.id.listMDAsMentorCost);

                holder.tvName.setText(name.get(position));
                holder.tvAgeLevel.setText(ageLevel.get(position));
                holder.tvSkillLevel.setText(skillLevel.get(position));
                holder.tvDate.setText(date.get(position));
                holder.tvTime.setText(time.get(position));
                holder.tvLocation.setText(location.get(position));
                holder.tvCost.setText(cost.get(position));

                holder.join = (ImageButton) convertView.findViewById(R.id.mentorDetailAsMentorUpdate);
                convertView.setTag(holder);
            }

            mainholder = (Holder) convertView.getTag();
            mainholder.join.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), SetClass.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("category", category.get(position));
                    mBundle.putString("classID", classID.get(position));
                    mBundle.putString("className", className.get(position));
                    mBundle.putString("classDescription", classDescription.get(position));
                    mBundle.putString("classSize", classSize.get(position));
                    mBundle.putString("ageLevel", ageLevel.get(position));
                    mBundle.putString("skillLevel", skillLevel.get(position));
                    mBundle.putString("date", date.get(position));
                    mBundle.putString("time", time.get(position));
                    mBundle.putString("location", location.get(position));
                    mBundle.putString("locationDetail", locationDetail.get(position));
                    mBundle.putString("cost", cost.get(position));
                    mBundle.putString("privateCost", privateCost.get(position));
                    mBundle.putString("additionalCost", additionalCost.get(position));
                    mBundle.putString("additionalCostFor", additionalCostFor.get(position));
                    mBundle.putString("latitude", latitude.get(position));
                    mBundle.putString("longitude", longitude.get(position));
                    mBundle.putString("classPicture", classPicture.get(position));
                    mBundle.putString("pageSource", "Mentor");
                    i.putExtras(mBundle);
                    startActivity(i);
                }
            });
            return convertView;
        }
    }
    public class Holder
    {
        TextView tvName,tvAgeLevel,tvSkillLevel,tvDate,tvTime,tvLocation,tvCost;
        ImageButton join;
    }
}
