package com.buboslabwork.mycorz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListNotification extends BaseAdapter{
    Context context;
    ArrayList<String> picture,message,time;
    private static LayoutInflater inflater=null;
    public ListNotification(Notification activity, ArrayList<String> picture,ArrayList<String> message,ArrayList<String> time) {
        // TODO Auto-generated constructor stub
        this.picture = picture;
        this.message = message;
        this.time = time;
        context=activity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return message.size();
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
        TextView tvNotification,tvNotificationTime;
        CircleImageView profileIcon;
        Bitmap bitmap;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_notification, null);
        holder.profileIcon = (de.hdodenhof.circleimageview.CircleImageView)rowView.findViewById(R.id.profileNotif);
        holder.tvNotification=(TextView) rowView.findViewById(R.id.tvNotification);
        holder.tvNotification.setText(message.get(position));
        holder.tvNotificationTime=(TextView) rowView.findViewById(R.id.tvNotificationTime);
        holder.tvNotificationTime.setText(time.get(position));

        byte[] b = Base64.decode(picture.get(position), Base64.DEFAULT);
        holder.bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        holder.profileIcon.setImageBitmap(holder.bitmap);

        return rowView;
    }
}
