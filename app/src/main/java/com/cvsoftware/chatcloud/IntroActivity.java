package com.cvsoftware.chatcloud;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.cvsoftware.chatcloud.utils.AppIntroActivity;

public class IntroActivity extends AppIntroActivity {
    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        addSlide(new Slide(R.string.welcome_to_chatcloud,R.string.welcome_desc,R.drawable.logo_high_res, Color.parseColor("#3a65f3")));
        addSlide(new Slide(R.string.secure,R.string.secure_desc,R.drawable.secure, Color.parseColor("#2E7D32")));
        addSlide(new Slide(R.string.easy_to_use,R.string.easy_desc,R.drawable.tool, Color.parseColor("#0277BD")));
        addSlide(new Slide(R.string.only_one_permission,R.string.permission_desc,R.drawable.permission, Color.parseColor("#E64A19"))
                .setPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE})
                .setTint(Color.parseColor("#FF8A65")));
        addSlide(new Slide(R.string.everythingready,R.string.desc_everything,R.drawable.ic_done_black_24dp,Color.parseColor("#512DA8"))
                .setTint(Color.parseColor("#9575CD"))
                .setTakeTourEnabled(true)
        );


    }



    @Override
    public void onPageChange(int page, int maxPage, ImageButton nextButton){
        if(page == maxPage){
         nextButton.setVisibility(View.GONE);

        }
        else{
            nextButton.setVisibility(View.VISIBLE);
        }
        Log.i("PAGECHANGE",""+page + " : "+maxPage);
    }



}
