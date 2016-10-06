package com.buboslabwork.mycorz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListMentorDetailTesti extends BaseAdapter{
    ArrayList<String> studentUsername,studentTesti,studentRating;
    Context context;
    private static LayoutInflater inflater=null;
    public ListMentorDetailTesti(MentorDetailTesti activity, ArrayList<String> studentUsername, ArrayList<String> studentTesti, ArrayList<String> studentRating) {
        // TODO Auto-generated constructor stub
        this.studentUsername = studentUsername;
        this.studentTesti = studentTesti;
        this.studentRating = studentRating;
        context=activity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return studentUsername.size();
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
        TextView tvName,tvReview,tvRating;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_mentor_detail_testi, null);
        holder.tvName=(TextView) rowView.findViewById(R.id.listMentorTestiName);
        holder.tvReview=(TextView) rowView.findViewById(R.id.listMentorTestiReview);

        holder.tvName.setText(studentUsername.get(position));
        holder.tvReview.setText(studentTesti.get(position));

        return rowView;
    }
}
