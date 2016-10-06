package com.buboslabwork.mycorz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PaymentATM extends AppCompatActivity {
    User user;
    String email;
    TextView tvInvoice,tvName,tvDate,tvTime,tvLocation,tvAge,tvSkill,tvType,tvCost,tvCostTotal;
    String Invoice,Name,Date,Time,Location,Age,Skill,Type,Cost;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_atm);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(PaymentATM.this); //get user preference
        if(user != null) {
            email = user.email;
        }

        tvName = (TextView)findViewById(R.id.paymentATMClassName);
        tvDate = (TextView)findViewById(R.id.paymentATMDate);
        tvTime = (TextView)findViewById(R.id.paymentATMTime);
        tvLocation = (TextView)findViewById(R.id.paymentATMLocation);
        tvAge = (TextView)findViewById(R.id.paymentATMAge);
        tvSkill = (TextView)findViewById(R.id.paymentATMSkill);
        tvCost = (TextView)findViewById(R.id.paymentATMCost);
        tvCostTotal = (TextView)findViewById(R.id.paymentATMTotalCost);
        tvType = (TextView)findViewById(R.id.paymentATMClassType);
        tvInvoice = (TextView)findViewById(R.id.paymentATMID);

        Intent i = getIntent();
        if(i != null){
            Invoice = i.getStringExtra("orderID");
            Name = i.getStringExtra("className");
            Date = i.getStringExtra("date");
            Time = i.getStringExtra("time");
            Location = i.getStringExtra("location");
            Age = i.getStringExtra("age");
            Skill = i.getStringExtra("skill");
            Type = i.getStringExtra("type");
            Cost = i.getStringExtra("cost");

            tvInvoice.setText(Invoice);
            tvName.setText(Name);
            tvDate.setText(Date);
            tvTime.setText(Time);
            tvLocation.setText(Location);
            tvAge.setText(Age);
            tvSkill.setText(Skill);
            tvCost.setText(Cost);
            tvCostTotal.setText(Cost);
            tvType.setText(Type);
        }

        Button confirmation = (Button)findViewById(R.id.paymentATMKonfirmasi);
        confirmation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PaymentConfirmation.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("orderID", Invoice);
                mBundle.putString("className", Name);
                mBundle.putString("date", Date);
                mBundle.putString("location", Location);
                mBundle.putString("time", Time);
                mBundle.putString("type", tvType.getText().toString());
                mBundle.putString("age", Age);
                mBundle.putString("skill", Skill);
                mBundle.putString("cost", tvCost.getText().toString());
                i.putExtras(mBundle);
                startActivity(i);
            }
        });
        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackPaymentATM);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PaymentATM.this.finish();
            }
        });
    }
}
