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

public class ListWishlist extends BaseAdapter{
    Context context;
    ArrayList<String> username,category,subcategory;
    private static LayoutInflater inflater=null;
    public ListWishlist(Wishlist activity, ArrayList<String> username, ArrayList<String> category, ArrayList<String> subcategory) {
        // TODO Auto-generated constructor stub
        this.username = username;
        this.category = category;
        this.subcategory = subcategory;
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
        TextView tvRequestBy,tvCategory,tvSubCategory;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_wishlist, null);
        holder.tvRequestBy=(TextView) rowView.findViewById(R.id.wishlist_requestedby);
        holder.tvCategory=(TextView) rowView.findViewById(R.id.wishlist_category);
        holder.tvSubCategory=(TextView) rowView.findViewById(R.id.wishlist_subcategory);

        holder.tvRequestBy.setText(username.get(position));
        holder.tvCategory.setText(category.get(position));
        holder.tvSubCategory.setText(subcategory.get(position));

        return rowView;
    }
}
