package com.buboslabwork.mycorz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class CategoryClass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_class);

        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackCategoryClass);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Category.class);
                startActivity(i);
            }
        });
    }
}
