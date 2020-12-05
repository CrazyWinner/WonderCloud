package com.cvsoftware.chatcloud.utils;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cvsoftware.chatcloud.HowToActivity;
import com.cvsoftware.chatcloud.MainActivity;
import com.cvsoftware.chatcloud.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

public class AppIntroActivity extends AppCompatActivity {
    private ViewPager mPager;
    public int currentPage=0;
    public CustomPagerAdapter adapter;
    public void addSlide(Slide slide){
        adapter.addSlide(slide);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mPager = findViewById(R.id.viewPager);
        adapter = new CustomPagerAdapter(this,findViewById(R.id.introBackground));

        /*
        adapter.addSlide(new Slide(R.string.wip,R.string.wip,R.drawable.adim1, Color.parseColor("#00BCD4")));
        adapter.addSlide(new Slide(R.string.wip,R.string.wip,R.drawable.adim2, Color.parseColor("#4CAF50")));
        adapter.addSlide(new Slide(R.string.wip,R.string.wip,R.drawable.adim3, Color.parseColor("#FDD835")));
        adapter.addSlide(new Slide(R.string.wip,R.string.wip,R.drawable.adim4, Color.parseColor("#FDD835")));

         */
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position<adapter.getCount()-1){
                    int color = ColorUtils.blendARGB(adapter.getColorOf(position),adapter.getColorOf(position+1),positionOffset);
                    adapter.setColor(color);
                }

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChange(currentPage,adapter.getCount()-1,(ImageButton)findViewById(R.id.nextPageButton));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        DotsIndicator indicator = findViewById(R.id.dots_indicator);
        indicator.setViewPager(mPager);
        findViewById(R.id.nextPageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextPage();
                nextPage(v);
            }
        });
    }

    public void onNextPage(){

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if(requestCode == 0){
            for(int i = 0; i < grantResults.length; i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    return;
                }
            }
            adapter.calculateCount();
            adapter.notifyDataSetChanged();
            onPageChange(currentPage,adapter.getCount()-1,(ImageButton) findViewById(R.id.nextPageButton));
        }
    }
    public class CustomPagerAdapter extends FragmentStatePagerAdapter {
        int currentCount = 0;
        ArrayList<Slide> fragments;
        View introBackground;
        AppCompatActivity activity;
        public CustomPagerAdapter(AppCompatActivity activity,View introBackground) {
            super(activity.getSupportFragmentManager());
            fragments=new ArrayList<>();
            this.introBackground = introBackground;
            this.activity = activity;

        }


        public int getItemPosition(Object object){
            return POSITION_NONE;
        }

        public int getColorOf(int position){
            return fragments.get(position).getMyColor();
        }

        public void addSlide(Slide fragment){
            fragments.add(fragment);
            calculateCount();
            notifyDataSetChanged();
            onPageChange(currentPage,adapter.getCount()-1,(ImageButton) findViewById(R.id.nextPageButton));
        }

        public void setColor(int color){
            introBackground.setBackgroundColor(color);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return IntroFragment.newInstance(fragments.get(position));
        }
        public void calculateCount(){
            for(int i = 0; i < fragments.size(); i++){
                if(fragments.get(i).permissions!=null){
                    for(int j = 0; j < fragments.get(i).permissions.length; j++) {
                        if (ContextCompat.checkSelfPermission(activity, fragments.get(i).permissions[j])
                                != PackageManager.PERMISSION_GRANTED) {
                            currentCount = i + 1;
                            return;
                        }
                    }
                    fragments.get(i).permissionGranted = true;
                }
            }
            currentCount = fragments.size();
        }
        @Override
        public int getCount() {
            return currentCount;
        }
    }


    public class Slide{
        private int tint;
        private boolean tintEnabled=false;
        private int title;
        private int myColor;
        private int myImage;
        public boolean permissionGranted = false;
        private int desc;
        private String[] permissions;
        private boolean takeTour = false;
        public Slide (@StringRes int title, @StringRes int desc, @DrawableRes int image, int color) {
            this.title=title;
            this.desc=desc;
            this.myColor=color;
            this.myImage=image;

        }
        public Slide setTakeTourEnabled(boolean takeTour){
            this.takeTour = takeTour;
            return this;
        }
        public Slide setTint(@ColorInt int color){
            tintEnabled = true;
            tint = color;
            return this;
        }
        public Slide setPermissions(String[] permissions){
            this.permissions = permissions;
            return this;

        }


        public int getMyColor() {
            return myColor;
        }



        public int getMyImage() {
            return myImage;
        }

        public String[] getPermissions() {
            return permissions;
        }

        public int getTitle() {
            return title;
        }

        public int getDesc() {
            return desc;
        }
    }

    public static class IntroFragment extends Fragment {
        // Store instance variables
        private int tint=0;
        private boolean tintEnabled=false;
        private int title;
        private int myColor;
        private int myImage;
        private int desc;
        View myView;
        private boolean takeTour=false;
        String[] permissions;
        boolean permissionGranted;

        private void askForPermissions(){
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    0);

        }

        // newInstance constructor for creating fragment with arguments
        public static IntroFragment newInstance(Slide slide) {

            IntroFragment fragmentFirst = new IntroFragment();
            Bundle args = new Bundle();
            args.putInt("title", slide.getTitle());
            args.putInt("image",slide.getMyImage());
            args.putInt("desc",slide.getDesc());
            args.putInt("color",slide.getMyColor());
            args.putBoolean("tintEnabled",slide.tintEnabled);
            args.putInt("tint",slide.tint);
            args.putBoolean("takeTour",slide.takeTour);
            args.putBoolean("permissionGranted",slide.permissionGranted);
            if(slide.getPermissions()!=null)
                args.putStringArray("permissions",slide.getPermissions());
            fragmentFirst.setArguments(args);
            return fragmentFirst;
        }

        // Store instance variables based on arguments passed
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            title = getArguments().getInt("title");

            myColor = getArguments().getInt("color");
            myImage = getArguments().getInt("image");
            desc = getArguments().getInt("desc");
            permissions = getArguments().getStringArray("permissions");
            tintEnabled = getArguments().getBoolean("tintEnabled");
            tint = getArguments().getInt("tint");
            permissionGranted = getArguments().getBoolean("permissionGranted");
            takeTour = getArguments().getBoolean("takeTour");

        }

        // Inflate the view for the fragment based on layout XML
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            myView = inflater.inflate(R.layout.fragmentlayout, container, false);
            TextView titleView = myView.findViewById(R.id.title);
            TextView descView = myView.findViewById(R.id.description);
            ImageView imageView = myView.findViewById(R.id.image);

            titleView.setText(title);
            descView.setText(desc);
            imageView.setImageResource(myImage);
            if(tintEnabled){
                imageView.setImageTintList(ColorStateList.valueOf(tint));
                Log.i("TINT",""+tint);
            }
            Button permissionButton = myView.findViewById(R.id.givePermissionButton);

            permissionButton.setTextColor(ColorUtils.blendARGB(myColor,Color.BLACK,0.1f));
            if(permissions != null){
                permissionButton.setVisibility(View.VISIBLE);
                permissionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askForPermissions();
                    }
                });
                permissionButton.setEnabled(!permissionGranted);
                if(permissionGranted){
                    permissionButton.setText(R.string.permissiongranted);
                    permissionButton.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));
                }
            }else if(takeTour){
                permissionButton.setVisibility(View.VISIBLE);
                permissionButton.setText(R.string.takeatour);
                permissionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), HowToActivity.class);
                        i.putExtra("launch",true);
                        getActivity().startActivity(i);
                        getActivity().finish();
                    }
                });
                permissionButton.setEnabled(!permissionGranted);
                if(permissionGranted){
                    permissionButton.setText(R.string.permissiongranted);
                    permissionButton.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));
                }
                TextView noThanks = myView.findViewById(R.id.noThanks);
                noThanks.setVisibility(View.VISIBLE);
                noThanks.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(i);
                        getActivity().finish();
                    }
                });


            }

            return myView;
        }
    }


    public void nextPage(View v){
        if(currentPage == adapter.getCount()-1){
            SharedPreferences sharedPreferences
                    = getSharedPreferences("ChatCloudSharedPref",
                    MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("firstTime",false).apply();
        }else{
            mPager.arrowScroll(View.FOCUS_RIGHT);}
    }

    public void onPageChange(int page,int maxPage,ImageButton nextButton){
    }
    @Override
    public void onBackPressed(){
        if(currentPage!=0){
            prevPage(null);
        }else{
            finish();
        }

    }
    public void prevPage(View v){
        mPager.arrowScroll(View.FOCUS_LEFT);
    }

}
