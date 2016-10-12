package com.buboslabwork.mycorz;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class CategoryAdapter extends BaseExpandableListAdapter {
    ImageLoader imageLoader = VolleyAppController.getInstance().getImageLoader();
    private Context _context;
    private List<String> _listDataHeader,icon;
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public CategoryAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData, List<String> icon) {
        this._context = context;
        this.icon = icon;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_child_category, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.tvChildCategory);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_category, null);

        }

        ((ImageView) convertView.findViewById(R.id.imageGroupCategory)).setImageResource(isExpanded?R.drawable.icon_update_skill:R.drawable.icon_update_skill_reverse);

        if (imageLoader == null)
            imageLoader = VolleyAppController.getInstance().getImageLoader();
        NetworkImageView thumbnail = (NetworkImageView) convertView.findViewById(R.id.listCategoryPicture);

        thumbnail.setImageUrl(icon.get(groupPosition), imageLoader);
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.tvGroupCategory);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
