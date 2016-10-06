package com.buboslabwork.mycorz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PaymentConfirmation extends AppCompatActivity {
    User user;
    String email;
    Integer flagHiddenView = 0; //0 = invisible , 1 = visible
    LinearLayout hiddenView;
    TextView tvInvoice,tvName,tvDate,tvTime,tvLocation,tvAge,tvSkill,tvType,tvCost,tvCostTotal;
    EditText dariRek,namaPemilikRek,noRek,namaBankPemilik,cabang,jumlahDibayar,keterangan;
    ImageView berkasImg;
    Button btnBerkas,btnConfirm;
    String Invoice,Name,Date,Time,Location,Age,Skill,Type,Cost;
    String rekTujuan,sKeterangan,sCabang;
    String sBerkas = "";

    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_confirmation);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        user=PrefUtils.getCurrentUser(PaymentConfirmation.this); //get user preference
        if(user != null) {
            email = user.email;
        }

        sBerkas = "";
        hiddenView = (LinearLayout)findViewById(R.id.paymentConfirmHiddenView);
        tvName = (TextView)findViewById(R.id.tvPaymentConfirmClassName);
        tvDate = (TextView)findViewById(R.id.tvPaymentConfirmDate);
        tvTime = (TextView)findViewById(R.id.tvPaymentConfirmTime);
        tvLocation = (TextView)findViewById(R.id.tvPaymentConfirmLocation);
        tvAge = (TextView)findViewById(R.id.tvPaymentConfirmAgeLevel);
        tvSkill = (TextView)findViewById(R.id.tvPaymentConfirmSkillLevel);
        tvCost = (TextView)findViewById(R.id.tvPaymentConfirmCost);
        tvCostTotal = (TextView)findViewById(R.id.tvPaymentConfirmTotalCost);
        tvType = (TextView)findViewById(R.id.tvPaymentConfirmClassType);
        tvInvoice = (TextView)findViewById(R.id.tvPaymentConfirmID);

        dariRek = (EditText) findViewById(R.id.paymentConfirmDariRek);
        namaPemilikRek = (EditText) findViewById(R.id.paymentConfirmNamaPemilikRek);
        noRek = (EditText) findViewById(R.id.paymentConfirmNoRek);
        namaBankPemilik = (EditText) findViewById(R.id.paymentConfirmNamaBankPengirim);
        cabang = (EditText) findViewById(R.id.paymentConfirmCabang);
        jumlahDibayar = (EditText) findViewById(R.id.paymentConfirmJumlah);
        keterangan = (EditText) findViewById(R.id.paymentConfirmKeterangan);
        btnBerkas = (Button) findViewById(R.id.paymentConfirmBerkas);
        btnConfirm = (Button) findViewById(R.id.paymentConfirmBtn);
        berkasImg = (ImageView)findViewById(R.id.paymentConfirmBerkasImg);

        jumlahDibayar.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    jumlahDibayar.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp ,]", "");

                    if (cleanString.length() > 0) {
                        double parsed = Double.parseDouble(cleanString);
                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setCurrencySymbol("Rp ");
                        //dfs.setGroupingSeparator('.');
                        //dfs.setMonetaryDecimalSeparator('.');
                        ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
                        formatter.setMaximumFractionDigits(0);
                        current = formatter.format(parsed);
                    } else {
                        current = cleanString;
                    }
                    jumlahDibayar.setText(current);
                    jumlahDibayar.setSelection(current.length());
                    jumlahDibayar.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

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

            tvInvoice.setText(Invoice.toUpperCase());
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

        dariRek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagHiddenView == 0) {
                    flagHiddenView = 1;
                    hiddenView.setVisibility(View.VISIBLE);
                }
                else{
                    flagHiddenView = 0;
                    hiddenView.setVisibility(View.GONE);
                }
            }
        });

        btnBerkas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY);
            }
        });

        //SPINNER FOR REK TUJUAN
        Spinner rekTujuanSpinner = (Spinner) findViewById(R.id.paymentConfirmRekTujuan);

        String[] age_level = new String[] { "Rekening Tujuan", "BCA an. PT MyCorz", "BRI an. PT MyCorz"};

        final ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, age_level){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return true;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rekTujuanSpinner.setAdapter(ageAdapter);

        rekTujuanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    rekTujuan = selectedItemText;
                    ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(PaymentConfirmation.this, R.color.colorTextGreyNormal));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // END SPINNER

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TextUtils.isEmpty(namaBankPemilik.getText().toString()) || TextUtils.isEmpty(namaPemilikRek.getText().toString()) ||
                        TextUtils.isEmpty(noRek.getText().toString()) || TextUtils.isEmpty(rekTujuan) ||
                        TextUtils.isEmpty(jumlahDibayar.getText().toString())) {
                    Toast.makeText(PaymentConfirmation.this, "Please fill in the empty field", Toast.LENGTH_LONG).show();
                }
                else {
                    String sJumlahDibayar = jumlahDibayar.getText().toString();
                    sJumlahDibayar = sJumlahDibayar.replace("Rp ", "");
                    sJumlahDibayar = sJumlahDibayar.replace(",", "");

                    if(TextUtils.isEmpty(keterangan.getText().toString())){
                        sKeterangan = "null";
                    }
                    else
                        sKeterangan = keterangan.getText().toString();

                    if(TextUtils.isEmpty(cabang.getText().toString())){
                        sCabang = "null";
                    }
                    else
                        sCabang = cabang.getText().toString();

                    Log.v("paymentconfirm","namabank: "+namaBankPemilik.getText().toString());
                    Log.v("paymentconfirm","pemilik: "+namaPemilikRek.getText().toString());
                    Log.v("paymentconfirm","rek: "+noRek.getText().toString());
                    Log.v("paymentconfirm","rektujuan: "+rekTujuan);
                    Log.v("paymentconfirm","jumlah: "+jumlahDibayar.getText().toString());
                    Log.v("paymentconfirm","ket: "+sKeterangan);
                    Log.v("paymentconfirm","berkas: "+sBerkas);
                    Log.v("paymentconfirm","invoice: "+Invoice);
                    Log.v("paymentconfirm","cabang: "+sCabang);
                    new PaymentConfirmation.AsyncPayment().execute(namaBankPemilik.getText().toString(),namaPemilikRek.getText().toString(),noRek.getText().toString(),
                            rekTujuan,jumlahDibayar.getText().toString(),sKeterangan,sBerkas,Invoice,sCabang);
                }

            }
        });

        ImageButton imgBtnBack = (ImageButton)findViewById(R.id.btnBackPaymentConfirm);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PaymentConfirmation.this.finish();
            }
        });
        Button imgBtnBack2 = (Button)findViewById(R.id.paymentConfirmBack);
        imgBtnBack2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PaymentConfirmation.this.finish();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_GALLERY) {
            if (resultCode == RESULT_CANCELED) {
                return;
            } else if (resultCode == RESULT_OK) {
                try {
                    berkasImg.setVisibility(View.VISIBLE);
                    Bitmap bm=null;
                    if (data != null) {
                        try {
                            bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    berkasImg.setImageBitmap(bm);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteImage = baos.toByteArray();
                    sBerkas = Base64.encodeToString(byteImage, Base64.DEFAULT);
                } catch (Exception e) {
                    Log.e("uploadpicresult","Error while opening the image file. Please try again.");
                    return;
                }
            } else {
                Log.e("uploadpicresult","Error while opening the image file. Please try again.");
                return;
            }

        }
    }

    // ASYNCTASK FOR SENDING DATA
    private class AsyncPayment extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(PaymentConfirmation.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tSubmitting...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://vidcom.click/admin/android/paymentConfirmation.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("dariRek", params[0])
                        .appendQueryParameter("namaPemilikRek", params[1])
                        .appendQueryParameter("noRek", params[2])
                        .appendQueryParameter("rekTujuan", params[3])
                        .appendQueryParameter("jumlahDibayar", params[4])
                        .appendQueryParameter("keterangan", params[5])
                        .appendQueryParameter("berkas", params[6])
                        .appendQueryParameter("orderID", params[7])
                        .appendQueryParameter("cabang", params[8]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{
                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            Log.v("confirmresult",result);
            if(result.equalsIgnoreCase("true"))
            {
                Toast.makeText(PaymentConfirmation.this, "Your payment confirmation has been submitted", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), PaymentComplete.class);
                startActivity(i);
            }else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("")){
                Toast.makeText(PaymentConfirmation.this, "Can't submit your payment confirmation to our database, please try again", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(PaymentConfirmation.this, "Something went wrong, connection problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}
