package com.buboslabwork.mycorz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Setting extends AppCompatActivity {
    TextView logout;
    User user;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_setting);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(Setting.this);

        logout = (TextView) findViewById(R.id.setting_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null && user.method.equalsIgnoreCase("facebook")) {
                    // We can logout from facebook by calling following method
                    LoginManager.getInstance().logOut();
                }
                PrefUtils.clearCurrentUser(Setting.this);
                Intent i= new Intent(Setting.this,Login.class);
                startActivity(i);
                finish();
            }
        });

        ImageButton btnHome = (ImageButton)findViewById(R.id.btnSettingBackToHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }
        });

        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnSettingHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnSettingProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnSettingNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvSettingHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvSettingProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvSettingNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
    }
}
