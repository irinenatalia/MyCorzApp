package com.buboslabwork.mycorz.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.buboslabwork.mycorz.PrefUtils;
import com.buboslabwork.mycorz.Profile;
import com.buboslabwork.mycorz.R;
import com.buboslabwork.mycorz.User;
import com.buboslabwork.mycorz.model.InboxModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Acer on 5/16/2016.
 */
public class InboxAdapter extends BaseAdapter {
    Context context;
    private static LayoutInflater inflater=null;
    private List<InboxModel> listData;
    Activity activity;

    public InboxAdapter(Activity mainActivity, List<InboxModel> listData) {
        // TODO Auto-generated constructor stub
        this.activity = mainActivity;
        this.context=mainActivity;
        this.listData = listData;
        this.inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listData.size();
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
        TextView senderName;
        TextView senderMessage;
        TextView receiverName;
        TextView receiverMessage;
        LinearLayout LayoutSender;
        LinearLayout LayoutReceiver;
        CircleImageView profileReceiver;
        CircleImageView profileSender;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final InboxModel data = listData.get(position);
        final Holder holder=new Holder();
        View rowView;

        User user= PrefUtils.getCurrentUser(activity);

        rowView = inflater.inflate(R.layout.item_chat, null);
        holder.senderName=(TextView) rowView.findViewById(R.id.senderName);
        holder.senderMessage=(TextView) rowView.findViewById(R.id.senderMessage);
        holder.receiverName=(TextView) rowView.findViewById(R.id.receiverName);
        holder.receiverMessage=(TextView) rowView.findViewById(R.id.receiverMessage);
        holder.LayoutSender=(LinearLayout) rowView.findViewById(R.id.LayoutSender);
        holder.LayoutReceiver=(LinearLayout) rowView.findViewById(R.id.LayoutReceiver);
        holder.profileReceiver=(CircleImageView) rowView.findViewById(R.id.profileReceiver);
        holder.profileSender=(CircleImageView) rowView.findViewById(R.id.profileSender);

        if(data.getsender_userid().equals(user.email)){
            holder.LayoutSender.setVisibility(View.GONE);
            holder.LayoutReceiver.setVisibility(View.VISIBLE);
            holder.receiverName.setText(data.getsender_name());
            holder.receiverMessage.setText(data.getmessage());
            Picasso.with(activity)
                    .load(data.getprofileSender())
                    .into(holder.profileReceiver);
        }else{
            holder.LayoutSender.setVisibility(View.VISIBLE);
            holder.LayoutReceiver.setVisibility(View.GONE);
            holder.senderName.setText(data.getsender_namatoko());
            holder.senderMessage.setText(data.getmessage());
            Picasso.with(activity)
                    .load(data.getprofileSender())
                    .into(holder.profileSender);
        }

        return rowView;
    }

}
