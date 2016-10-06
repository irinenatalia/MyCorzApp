package com.buboslabwork.mycorz;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ListRequestClass extends BaseAdapter{
    ArrayList<String> title,requestby,category,ageLevel,skillLevel,classSize,date,time;
    Context context;
    private static LayoutInflater inflater=null;
    public ListRequestClass(RequestClass activity, ArrayList<String> title,ArrayList<String> category, ArrayList<String> ageLevel, ArrayList<String> skillLevel, ArrayList<String> classSize,ArrayList<String> date, ArrayList<String> time,ArrayList<String> requestby) {
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
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_request_class, null);
        holder.tvRequestBy=(TextView) rowView.findViewById(R.id.request_requestedBy);
        holder.tvTitle=(TextView) rowView.findViewById(R.id.request_title);
        holder.tvCategory=(TextView) rowView.findViewById(R.id.request_category);
        holder.tvAgeLevel=(TextView) rowView.findViewById(R.id.request_ageLevel);
        holder.tvSkillLevel=(TextView) rowView.findViewById(R.id.request_skillLevel);
        holder.tvClassSize=(TextView) rowView.findViewById(R.id.request_classSize);
        holder.tvDate=(TextView) rowView.findViewById(R.id.request_date);
        holder.tvTime=(TextView) rowView.findViewById(R.id.request_time);

        holder.tvRequestBy.setText(requestby.get(position));
        holder.tvTitle.setText(title.get(position));
        holder.tvCategory.setText(category.get(position));
        holder.tvAgeLevel.setText(ageLevel.get(position));
        holder.tvSkillLevel.setText(skillLevel.get(position));
        holder.tvClassSize.setText(classSize.get(position));
        holder.tvDate.setText(date.get(position));
        holder.tvTime.setText(time.get(position));

        return rowView;
    }
}

