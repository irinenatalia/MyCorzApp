package com.buboslabwork.mycorz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntroSplash extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{
    private SliderLayout mDemoSlider;
    private int SLIDER_COUNT=0;
    private int previousPosition = 0;
    Button nextbtn;

    List<String> listImage = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_splash);

        nextbtn = (Button)findViewById(R.id.enterToLogin);
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
        nextbtn.setVisibility(View.GONE);
        AndroidNetworking.get("http://vidcom.click/admin/android/slider.php")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                           JSONArray result = response.getJSONArray("result");
                            for(int i = 0; i < result.length(); i++){
                                JSONObject c = result.getJSONObject(i);
                                listImage.add(c.getString("images"));
                                DefaultSliderView textSliderView = new DefaultSliderView(IntroSplash.this);
                                // initialize a SliderLayout
                                Log.v("url : ", "http://vidcom.click/" + c.getString("images"));
                                textSliderView
                                        .image("http://vidcom.click/" + c.getString("images"))
                                        .setScaleType(BaseSliderView.ScaleType.Fit)
                                        .setOnSliderClickListener(IntroSplash.this);

                                textSliderView.bundle(new Bundle());

                                mDemoSlider.addSlider(textSliderView);
                            }
                        }catch (JSONException e){

                        }

                        if(listImage.size() == 1){
                            nextbtn.setVisibility(View.VISIBLE);
                        }else{
                            nextbtn.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.stopAutoCycle();
        mDemoSlider.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.v("position", position + "");
                Log.v("listImage.size()", listImage.size() + "");
                if(position == listImage.size() - 1){
                    nextbtn.setVisibility(View.VISIBLE);
                }else{
                    nextbtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        nextbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });
    }
    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }
    @Override
    public void onSliderClick(BaseSliderView slider) {
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        /*
        if(position == SLIDER_COUNT){
            nextbtn.setVisibility(View.VISIBLE);
        }
        else{
            nextbtn.setVisibility(View.GONE);
        }
        if(previousPosition == SLIDER_COUNT && position == 0) {

            mDemoSlider.movePrevPosition();
            return;
        } else if(previousPosition == 0 && position == SLIDER_COUNT) {
            mDemoSlider.moveNextPosition();
            return;
        }

        previousPosition = position;
        */
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}
