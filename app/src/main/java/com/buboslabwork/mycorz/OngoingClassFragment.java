package com.buboslabwork.mycorz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingClassFragment extends Fragment{
    public ListView lvOngoingClass;
    ArrayList<String> order_id,student_username,alMentorUsername,date,time,location,locationDetail,ageLevel,className,paymentTime;

    public JSONArray result = null;
    public String myJSONString,mentor_username,email;
    private static final String JSON_ARRAY ="result";

    User user;

    public OngoingClassFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ongoing_class, container, false);

        user=PrefUtils.getCurrentUser(getActivity());
        if(user != null){
            email = user.email;
        }

        lvOngoingClass =(ListView) view.findViewById(R.id.fragmentOngoingListview);
        mentor_username = getArguments().getString("mentor");
        myJSONString = getArguments().getString("ONGOING_JSON");
        if(myJSONString.equalsIgnoreCase("") || myJSONString.equalsIgnoreCase("false")){
            TextView warning = (TextView)view.findViewById(R.id.warningOngoingClass);
            warning.setText("You have no ongoing class right now");
        }
        else{
            order_id = new ArrayList<String>();
            student_username = new ArrayList<String>();
            alMentorUsername = new ArrayList<String>();
            date = new ArrayList<String>();
            time = new ArrayList<String>();
            location = new ArrayList<String>();
            locationDetail = new ArrayList<String>();
            ageLevel = new ArrayList<String>();
            className = new ArrayList<String>();
            paymentTime = new ArrayList<String>();

            // Parse JSON data to Listview
            extractJSON();
            showData(view);
        }

        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    private void extractJSON(){
        try {
            JSONObject jsonObject = new JSONObject(myJSONString);
            result = jsonObject.getJSONArray(JSON_ARRAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData(View view){
        try {
            for(int i=0; i<result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);

                order_id.add(jsonObject.getString("order_id"));
                student_username.add(jsonObject.getString("student_username"));
                alMentorUsername.add(jsonObject.getString("mentor_username"));
                date.add(jsonObject.getString("class_date"));
                time.add(jsonObject.getString("class_time"));
                ageLevel.add(jsonObject.getString("age_level"));
                location.add(jsonObject.getString("location"));
                locationDetail.add(jsonObject.getString("location_detail"));
                className.add(jsonObject.getString("class_name"));
                paymentTime.add(jsonObject.getString("payment_time"));
            }

            lvOngoingClass.setAdapter(new ListOngoingClass(getActivity().getBaseContext(), order_id, student_username, date, time, location, ageLevel, className));
            // Click event for single list row
            lvOngoingClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if(student_username.get(position).equalsIgnoreCase(email)) { //is student
                        Intent i = new Intent(view.getContext(), SummaryStudentProgress.class);
                        Bundle extras = new Bundle();
                        extras.putString("id",order_id.get(position));
                        extras.putString("date",date.get(position));
                        extras.putString("time",time.get(position));
                        extras.putString("ageLevel",ageLevel.get(position));
                        extras.putString("location",location.get(position));
                        extras.putString("locationDetail",locationDetail.get(position));
                        extras.putString("className",className.get(position));
                        extras.putString("paymentTime",paymentTime.get(position));
                        i.putExtras(extras);
                        startActivity(i);
                    }
                    else if(alMentorUsername.get(position).equalsIgnoreCase(email)){ //is mentor
                        Intent i = new Intent(view.getContext(), SummaryMentorProgress.class);
                        Bundle extras = new Bundle();
                        extras.putString("id",order_id.get(position));
                        extras.putString("studentUsername",student_username.get(position));
                        extras.putString("date",date.get(position));
                        extras.putString("time",time.get(position));
                        extras.putString("ageLevel",ageLevel.get(position));
                        extras.putString("location",location.get(position));
                        extras.putString("locationDetail",locationDetail.get(position));
                        extras.putString("className",className.get(position));
                        i.putExtras(extras);
                        startActivity(i);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public class ListOngoingClass extends BaseAdapter {
        ArrayList<String> order_id,student_username,name,date,time,location,ageLevel;
        Context context;
        private LayoutInflater inflater=null;
        public ListOngoingClass(Context activity, ArrayList<String> order_id, ArrayList<String> student_username, ArrayList<String> date, ArrayList<String> time, ArrayList<String> location, ArrayList<String> ageLevel, ArrayList<String> name) {
            this.order_id = order_id;
            this.student_username = student_username;
            this.name = name;
            this.date = date;
            this.ageLevel = ageLevel;
            this.time = time;
            this.location = location;
            context=activity;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        public class Holder
        {
            TextView tvUserName,tvDate,tvTime,tvLocation,tvAgeLevel,tvName;
            CircleImageView ivProfile;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.list_ongoing_class, null);
            holder.tvUserName=(TextView) rowView.findViewById(R.id.listOngoingName);
            holder.tvDate=(TextView) rowView.findViewById(R.id.ongoingDate);
            holder.tvTime=(TextView) rowView.findViewById(R.id.ongoingTime);
            holder.tvAgeLevel=(TextView) rowView.findViewById(R.id.ongoingAge);
            holder.tvLocation=(TextView) rowView.findViewById(R.id.ongoingLocation);
            holder.tvName=(TextView) rowView.findViewById(R.id.ongoingClass);
            holder.ivProfile = (de.hdodenhof.circleimageview.CircleImageView)rowView.findViewById(R.id.listOngoingPicture);

            if(student_username.get(position).equalsIgnoreCase(email)){
                holder.tvUserName.setText("I'm a student");
                holder.ivProfile.setImageResource(R.drawable.icon_student);
                //holder.ivProfile.setBackgroundResource(R.drawable.icon_student);
            }
            else if(alMentorUsername.get(position).equalsIgnoreCase(email)){
                holder.tvUserName.setText("I'm a mentor");
                holder.ivProfile.setImageResource(R.drawable.icon_mentor);
            }
            holder.tvDate.setText(date.get(position));
            holder.tvTime.setText(time.get(position));
            holder.tvAgeLevel.setText(ageLevel.get(position));
            holder.tvLocation.setText(location.get(position));
            holder.tvName.setText(name.get(position));

            return rowView;
        }
    }
}
