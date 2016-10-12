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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class ListClassHome extends BaseAdapter{
    ArrayList<String> classDate,className,category,ageLevel,picture;
    ImageLoader imageLoader = VolleyAppController.getInstance().getImageLoader();
    Context context;
    private static LayoutInflater inflater=null;

    public ListClassHome(Home activity, ArrayList<String> classDate, ArrayList<String> className, ArrayList<String> category, ArrayList<String> ageLevel, ArrayList<String> picture) {
        // TODO Auto-generated constructor stub
        this.picture = picture;
        this.classDate = classDate;
        this.className = className;
        this.category = category;
        this.ageLevel = ageLevel;
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
        TextView tvClassDate,tvClassName,tvAgeLevel,tvCategory;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_class_home, null);
        holder.tvClassDate=(TextView) rowView.findViewById(R.id.listHomeClassDate);
        holder.tvClassName=(TextView) rowView.findViewById(R.id.listHomeClassName);
        holder.tvCategory=(TextView) rowView.findViewById(R.id.listHomeKategori);
        holder.tvAgeLevel=(TextView) rowView.findViewById(R.id.listHomeAgeLevel);
        if (imageLoader == null)
            imageLoader = VolleyAppController.getInstance().getImageLoader();
        NetworkImageView thumbnail = (NetworkImageView)rowView.findViewById(R.id.listLandingPagePicture);

        thumbnail.setImageUrl(picture.get(position), imageLoader);
        holder.tvClassDate.setText(classDate.get(position));
        holder.tvClassName.setText(className.get(position));
        holder.tvCategory.setText(category.get(position));
        holder.tvAgeLevel.setText(ageLevel.get(position));

        return rowView;
    }
}
