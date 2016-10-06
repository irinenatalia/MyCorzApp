package com.buboslabwork.mycorz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddCertificate extends AppCompatActivity {
    EditText skill;
    String sSkill;
    EditText certificate,yearCertificate;
    private static final Integer CATEGORY_INTEGER_VALUE = 11;
    String returnedCategory="";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificate);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        skill = (EditText)findViewById(R.id.addSkillCategory);
        certificate = (EditText) findViewById(R.id.addCertName);
        yearCertificate = (EditText) findViewById(R.id.addCertYear);

        Intent i = getIntent();
        if(i!=null){
            sSkill = i.getStringExtra("category");
            if(!TextUtils.isEmpty(sSkill)){
                skill.setText(sSkill);
            }
        }

        skill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TempCategory.class);
                startActivityForResult(i, CATEGORY_INTEGER_VALUE);
            }
        });

        ImageButton btnBack = (ImageButton)findViewById(R.id.btnBackAddCert);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddCertificate.this.finish();
            }
        });

        ImageButton btnSave = (ImageButton)findViewById(R.id.btnAddCertSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!skill.getText().toString().isEmpty() && !certificate.getText().toString().isEmpty() && !yearCertificate.getText().toString().isEmpty()){
                    Intent i = new Intent(getApplicationContext(), UpdateCertificate.class);
                    i.putExtra("skill", skill.getText().toString());
                    i.putExtra("certificate", certificate.getText().toString());
                    i.putExtra("yearCertificate", yearCertificate.getText().toString());
                    startActivity(i);
                }
                else{
                    Toast.makeText(AddCertificate.this, "Please fill the empty field", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(AddCertificate.this, "Please fill the empty field", Toast.LENGTH_SHORT).show();
            }
        });
        Button btnSave2 = (Button)findViewById(R.id.btnAddCertSave2);
        btnSave2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!skill.getText().toString().isEmpty() && !certificate.getText().toString().isEmpty() && !yearCertificate.getText().toString().isEmpty()){
                    Intent i = new Intent();
                    i.putExtra("skill", skill.getText().toString());
                    i.putExtra("certificate", certificate.getText().toString());
                    i.putExtra("yearCertificate", yearCertificate.getText().toString());
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
                else{
                    Toast.makeText(AddCertificate.this, "Please fill the empty field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATEGORY_INTEGER_VALUE && resultCode == SetClass.RESULT_OK) {
            returnedCategory = data.getStringExtra("returnedCategoryParam");
            skill.setText(returnedCategory);
        }
    }
}
