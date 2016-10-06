package com.buboslabwork.mycorz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PaymentMethod extends AppCompatActivity {
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
        setContentView(R.layout.payment_method);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(PaymentMethod.this); //get user preference
        if(user != null) {
            email = user.email;
        }

        tvName = (TextView)findViewById(R.id.paymentMethodClassName);
        tvDate = (TextView)findViewById(R.id.paymentMethodDate);
        tvTime = (TextView)findViewById(R.id.paymentMethodTime);
        tvLocation = (TextView)findViewById(R.id.paymentMethodLocation);
        tvAge = (TextView)findViewById(R.id.paymentMethodAge);
        tvSkill = (TextView)findViewById(R.id.paymentMethodSkill);
        tvCost = (TextView)findViewById(R.id.paymentMethodCost);
        tvCostTotal = (TextView)findViewById(R.id.paymentMethodTotalCost);
        tvType = (TextView)findViewById(R.id.paymentMethodClassType);
        tvInvoice = (TextView)findViewById(R.id.paymentMethodID);

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

            String[] locationSeparated = Location.split(",");
            tvInvoice.setText(Invoice.toUpperCase());
            tvName.setText(Name);
            tvDate.setText(Date);
            tvTime.setText(Time);
            tvLocation.setText(locationSeparated[0]);
            tvAge.setText(Age);
            tvSkill.setText(Skill);
            tvCost.setText("Rp" + Cost);
            tvCostTotal.setText("Rp" + Cost);
            if(Type.equalsIgnoreCase("1"))
                tvType.setText("Private");
            else
                tvType.setText("Workshop");
        }

        ImageButton paymentBack = (ImageButton)findViewById(R.id.btnBackPaymentMethod);
        paymentBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PaymentMethod.this.finish();
            }
        });

        TextView paymentATM = (TextView)findViewById(R.id.paymentMethodATM);
        paymentATM.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PaymentATM.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("orderID", Invoice);
                mBundle.putString("className", Name);
                mBundle.putString("date", Date);
                mBundle.putString("location", tvLocation.getText().toString());
                mBundle.putString("time", Time);
                mBundle.putString("type", tvType.getText().toString());
                mBundle.putString("age", Age);
                mBundle.putString("skill", Skill);
                mBundle.putString("cost", tvCost.getText().toString());
                i.putExtras(mBundle);
                startActivity(i);
            }
        });
        ImageButton paymentATM2 = (ImageButton)findViewById(R.id.paymentMethodATM2);
        paymentATM2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PaymentATM.class);
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

    }
}
