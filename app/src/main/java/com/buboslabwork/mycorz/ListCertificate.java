package com.buboslabwork.mycorz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListCertificate extends BaseAdapter {
    Context context;
    ArrayList<String> skill,certificate,yearCertificate;
    private static LayoutInflater inflater=null;
    public ListCertificate(Certificate activity, ArrayList<String> skill, ArrayList<String> certificate, ArrayList<String> yearCertificate) {
        // TODO Auto-generated constructor stub
        this.skill = skill;
        this.certificate = certificate;
        this.yearCertificate = yearCertificate;
        context=activity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return skill.size();
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
        TextView tvSkill,tvCertificate,tvYearCertificate;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_profile_certificate, null);
        holder.tvSkill=(TextView) rowView.findViewById(R.id.listCertSkill);
        holder.tvCertificate=(TextView) rowView.findViewById(R.id.listCertTitle);
        holder.tvYearCertificate=(TextView) rowView.findViewById(R.id.listCertYear);

        holder.tvSkill.setText(skill.get(position));
        holder.tvCertificate.setText(certificate.get(position));
        holder.tvYearCertificate.setText(yearCertificate.get(position));

        return rowView;
    }
}
