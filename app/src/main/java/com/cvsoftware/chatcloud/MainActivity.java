package com.cvsoftware.chatcloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cvsoftware.chatcloud.utils.MainRecyclerAdapter;
import com.cvsoftware.chatcloud.utils.RecyclerObject;
import com.cvsoftware.chatcloud.utils.SQLHelper;
import com.cvsoftware.chatcloud.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    ArrayList<RecyclerObject> objects;
    MainRecyclerAdapter adapter;
    SQLHelper sql;
    AdView mAdView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_main_delete);
        adapter.setDeleteItem(item);
        // This make the delete item invisible
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_main_delete:
                new AlertDialog.Builder(this).setMessage(R.string.areyousure)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.deleteSelected(sql);
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

                break;
            case R.id.menu_main_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download WonderCloud from:");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.cvsoftware.chatcloud");
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share)));
                break;
            case R.id.menu_main_about:
                 Intent i = new Intent(this,AboutActivity.class);
                 startActivity(i);
                 break;
            case R.id.menu_main_translate:
                String url = "https://drive.google.com/open?id=15bUR5av57ctplYmivNyIwZQ-U4GjiiN6";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File folder = new File(Utils.getImagesFolder(this));
        if (!folder.exists()) {
            folder.mkdirs();
        }
        folder = new File(Utils.getImagesFolder(this) + ".thumbs");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File nomediaFile= new File(Utils.getImagesFolder(this) + ".nomedia");
        if(!nomediaFile.exists()){
            try {
                nomediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sql = new SQLHelper(this);
        objects = sql.loadObjects();
        RecyclerView rv = findViewById(R.id.main_recycler);
        rv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                manager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = new MainRecyclerAdapter(objects,findViewById(R.id.empty_view));
        adapter.checkEmpty();
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        SharedPreferences sharedPreferences
                = getSharedPreferences("ChatCloudSharedPref",
                MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("firstTime",false).apply();
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        String[] defaultExcluded = getResources().getStringArray(R.array.defaultexcluded);
        for(int i = 0; i < defaultExcluded.length; i++){
            Log.i("LOCALE",defaultExcluded[i]);
        }
        Log.i("LOCALE",getResources().getString(R.string.colors));
    }

    @Override
    public void onPause() {
        // Pause the AdView.
        mAdView.pause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
        mAdView.destroy();

        super.onDestroy();
    }


    @Override
    public void onResume(){
        super.onResume();
        ArrayList<RecyclerObject> objects2 = sql.loadObjects();
        if(objects2.size() > objects.size()){
            for(int i = objects.size(); i < objects2.size(); i++){
                objects.add(objects2.get(i));
                adapter.notifyItemInserted(i);
            }
        }
        adapter.clearSelected();
        mAdView.resume();
    }
    public void howTo(View v){
        Intent i = new Intent(this,HowToActivity.class);
        startActivity(i);
    }
}


