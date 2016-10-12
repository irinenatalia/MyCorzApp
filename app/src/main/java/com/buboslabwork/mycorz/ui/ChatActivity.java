package com.buboslabwork.mycorz.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.buboslabwork.mycorz.Category;
import com.buboslabwork.mycorz.MainActivity;
import com.buboslabwork.mycorz.PrefUtils;
import com.buboslabwork.mycorz.Profile;
import com.buboslabwork.mycorz.R;
import com.buboslabwork.mycorz.UpdateCertificate;
import com.buboslabwork.mycorz.User;
import com.buboslabwork.mycorz.adapter.InboxAdapter;
import com.buboslabwork.mycorz.model.InboxModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    ListView viewInbox;
    EditText etMessage;
    TextView btnSend;

    List<InboxModel> listChat = new ArrayList<>();
    InboxAdapter inboxAdapter;

    Bundle dataIntent;
    String tujuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dataIntent = getIntent().getExtras();
        tujuan = dataIntent.getString("tujuan");

        viewInbox = (ListView) findViewById(R.id.viewInbox);
        etMessage = (EditText) findViewById(R.id.etMessage);
        btnSend = (TextView) findViewById(R.id.btnSend);

        inboxAdapter = new InboxAdapter(this, listChat);
        viewInbox.setAdapter(inboxAdapter);

        loadData();

        ImageButton btnBack = (ImageButton)findViewById(R.id.btnBackChat);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChatActivity.this.finish();
            }
        });
    }

    void loadData(){
        User user= PrefUtils.getCurrentUser(this);
        AndroidNetworking.post("http://vidcom.click/admin/android/listmessage.php")
            .addBodyParameter("saya",user.email)
            .addBodyParameter("tujuan",tujuan)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        JSONArray result = response.getJSONArray("result");
                        for(int i = 0; i < result.length(); i++){
                            JSONObject c = result.getJSONObject(i);
                            InboxModel setData = new InboxModel();
                            setData.setid(c.getString("id"));
                            setData.setsender_userid(c.getString("sender_userid"));
                            setData.setreceived_userid(c.getString("received_userid"));
                            setData.setsender_name(c.getJSONObject("user_pengirim").getString("complete_name"));
                            setData.setprofileSender(c.getJSONObject("user_pengirim").getString("profile_picture"));
                            setData.setreceived_name(c.getJSONObject("user_tujuan").getString("complete_name"));
                            setData.setsender_namatoko(c.getJSONObject("user_pengirim").getString("complete_name"));
                            setData.setprofileReceiver(c.getJSONObject("user_pengirim").getString("profile_picture"));
                            setData.setreceived_namatoko(c.getJSONObject("user_tujuan").getString("complete_name"));
                            setData.setmessage(c.getString("message"));
                            setData.settanggal(c.getString("created_at"));
                            listChat.add(setData);
                        }

                        inboxAdapter.notifyDataSetChanged();

                    }catch (JSONException e){

                    }
                }

                @Override
                public void onError(ANError anError) {

                }
            });
    }
}
