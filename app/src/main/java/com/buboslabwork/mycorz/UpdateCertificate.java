package com.buboslabwork.mycorz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UpdateCertificate extends AppCompatActivity {
    String skill,certificate,yearCertificate;
    ArrayList<String> alSkill,alCertificate,alYearCertificate;
    ListView lvCertificate;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_certificate);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        alSkill = new ArrayList<String>();
        alCertificate = new ArrayList<String>();
        alYearCertificate = new ArrayList<String>();

        Intent intent = getIntent();
        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if(extras != null){
            skill = extras.getString("skill");
            certificate = extras.getString("certificate");
            yearCertificate = extras.getString("yearCertificate");

            alSkill.add(skill);
            alCertificate.add(certificate);
            alYearCertificate.add(yearCertificate);

            lvCertificate =(ListView) findViewById(R.id.listCertificate);
            //lvCertificate.setAdapter(new ListCertificate(this, alSkill,alCertificate,alYearCertificate));
            // Click event for single list row
            lvCertificate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                }
            });
        }

        ImageButton btnBack = (ImageButton)findViewById(R.id.btnBackCert);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnAdd = (ImageButton)findViewById(R.id.btnAddCert);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddCertificate.class);
                startActivity(i);
            }
        });
        ImageButton btnSave = (ImageButton)findViewById(R.id.btnSaveCert);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
    }
}
