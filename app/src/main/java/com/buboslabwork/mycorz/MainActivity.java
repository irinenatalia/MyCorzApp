package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    User user;
    TextView name;
    String sUsername;
    Bitmap bitmap;
    CircleImageView picture;

    private static final String CATEGORY_URL = "http://vidcom.click/admin/android/viewCategory.php";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        picture = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.mainProfileImage);
        name = (TextView)findViewById(R.id.main_name);
        user=PrefUtils.getCurrentUser(MainActivity.this);
        if(user != null){
            String[] splitUsername = user.name.split("\\s+");
            if(splitUsername.length > 2){
                sUsername = splitUsername[0] + " " + splitUsername[1] + " ";
                for(int i=2; i<splitUsername.length; i++){
                    sUsername += splitUsername[i].substring(0,1);
                }
            }
            else{
                sUsername = user.name;
            }
            name.setText(sUsername);

            byte[] b = Base64.decode(user.picture, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            picture.setImageBitmap(bitmap);
        }

        ImageButton btnHome = (ImageButton)findViewById(R.id.btnMainBackToHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }
        });

        ImageButton btnStudent = (ImageButton)findViewById(R.id.btnStudent);
        btnStudent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getCategoryJSON(CATEGORY_URL);
            }
        });
        ImageButton btnMentor = (ImageButton)findViewById(R.id.btnMentor);
        btnMentor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Mentor.class);
                startActivity(i);
            }
        });
        ImageButton btnHistory = (ImageButton)findViewById(R.id.btnMainHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        ImageButton btnProfile = (ImageButton)findViewById(R.id.btnMainProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        ImageButton btnSetting = (ImageButton)findViewById(R.id.btnMainSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        ImageButton btnNotif = (ImageButton)findViewById(R.id.btnMainNotification);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
        TextView tvHistory = (TextView)findViewById(R.id.tvMainHistory);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        TextView tvProfile = (TextView)findViewById(R.id.tvMainProfile);
        tvProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                startActivity(i);
            }
        });
        TextView tvSetting = (TextView)findViewById(R.id.tvMainSetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
            }
        });
        TextView tvNotif = (TextView)findViewById(R.id.tvMainNotification);
        tvNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Notification.class);
                startActivity(i);
            }
        });
    }
    private void getCategoryJSON(String url) {
        class getCategoryJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Loading...",null,true,true);
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(PreMainActivity.this, s, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), Category.class);
                i.putExtra("CATEGORY_JSON", s);
                startActivity(i);
            }
        }
        getCategoryJSON gj = new getCategoryJSON();
        gj.execute(url);
    }
}
