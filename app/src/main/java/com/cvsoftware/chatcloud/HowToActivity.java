package com.cvsoftware.chatcloud;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;

import com.cvsoftware.chatcloud.utils.AppIntroActivity;

public class HowToActivity extends AppIntroActivity {
    boolean launch = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(new Slide(R.string.step1,R.string.step1desc,R.drawable.adim0, Color.parseColor("#00695C")));
        addSlide(new Slide(R.string.step2,R.string.step2desc,R.drawable.adim1, Color.parseColor("#2E7D32")));
        addSlide(new Slide(R.string.step3,R.string.step3desc,R.drawable.adim2, Color.parseColor("#0277BD")));
        addSlide(new Slide(R.string.step4,R.string.step4desc,R.drawable.adim3, Color.parseColor("#E64A19")));
        addSlide(new Slide(R.string.step5,R.string.step5desc,R.drawable.adim4,Color.parseColor("#512DA8")));
        addSlide(new Slide(R.string.step6,R.string.step6desc,R.drawable.adim5,Color.parseColor("#512DA8")));
        launch = getIntent().getBooleanExtra("launch",false);
        SharedPreferences sharedPreferences
                = getSharedPreferences("ChatCloudSharedPref",
                MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("firstTime",false).apply();
    }

    @Override
    public void onNextPage(){
        if(currentPage == adapter.getCount()-1){
            if(launch){
                Intent i = new Intent(HowToActivity.this, MainActivity.class);
                startActivity(i);
            }
            finish();
        }
    }


    @Override
    public void onPageChange(int page, int maxPage, ImageButton nextButton){
        if(page == maxPage){
            nextButton.setImageResource(R.drawable.ic_done_black_24dp);
        }else{
            nextButton.setImageResource(R.drawable.ic_navigate_next_black_24dp);
        }
    }
}
