package com.buboslabwork.mycorz;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.ArrayList;

public class ListUpdateCertificate extends BaseAdapter{
    Context context;
    ArrayList<String> skill,certificate,year;
    private static LayoutInflater inflater=null;
    public ListUpdateCertificate(UpdateCertificate activity, ArrayList<String> skill,ArrayList<String> certificate,ArrayList<String> year) {
        // TODO Auto-generated constructor stub
        this.skill = skill;
        this.certificate = certificate;
        this.year = year;
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
        EditText editSkill,editCertificate,editYear;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_update_certificate, null);
        holder.editSkill=(EditText) rowView.findViewById(R.id.updateSkillCategory);
        holder.editSkill.setText(skill.get(position));
        holder.editCertificate=(EditText) rowView.findViewById(R.id.updateCertName);
        holder.editCertificate.setText(certificate.get(position));
        holder.editYear=(EditText) rowView.findViewById(R.id.updateCertYear);
        holder.editYear.setText(year.get(position));

        holder.editCertificate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final EditText Caption = (EditText) v;
                    certificate.set(position, Caption.getText().toString());
                    Log.v("updatecert", Caption.getText().toString());
                }
            }
        });
        holder.editYear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final EditText Caption = (EditText) v;
                    year.set(position, Caption.getText().toString());
                    Log.v("updatecert", Caption.getText().toString());
                }
            }
        });


        return rowView;
    }
}
