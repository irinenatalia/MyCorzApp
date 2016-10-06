package com.buboslabwork.mycorz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

public class ListAvailableMentor extends BaseAdapter{
    Context context;
    ArrayList<String> category,username,completeName,rating,profilePicture;
    ImageLoader imageLoader = VolleyAppController.getInstance().getImageLoader();
    private static LayoutInflater inflater=null;
    public ListAvailableMentor(MentorList activity, ArrayList<String> category, ArrayList<String> username, ArrayList<String> completeName, ArrayList<String> rating, ArrayList<String> profilePicture) {
        // TODO Auto-generated constructor stub
        this.category = category;
        this.username = username;
        this.completeName = completeName;
        this.rating = rating;
        this.profilePicture = profilePicture;
        context=activity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return username.size();
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
        TextView tvName,tvCategory;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_available_mentor, null);
        holder.tvName=(TextView) rowView.findViewById(R.id.listMentorName);
        holder.tvCategory=(TextView) rowView.findViewById(R.id.listMentorCategory);
        holder.tvName.setText(completeName.get(position));
        holder.tvCategory.setText(category.get(position));

        if (imageLoader == null)
            imageLoader = VolleyAppController.getInstance().getImageLoader();
        CircularNetworkImageView thumbnail = (CircularNetworkImageView) rowView
                .findViewById(R.id.listAvailableMentorPicture);

        thumbnail.setImageUrl(profilePicture.get(position), imageLoader);

        return rowView;
    }
}
