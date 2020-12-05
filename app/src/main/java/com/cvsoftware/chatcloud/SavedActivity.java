package com.cvsoftware.chatcloud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.cvsoftware.chatcloud.utils.ColorSelectorView;
import com.cvsoftware.chatcloud.utils.ExpandableCardView;
import com.cvsoftware.chatcloud.utils.FontSelectorView;
import com.cvsoftware.chatcloud.utils.LoadingDialog;
import com.cvsoftware.chatcloud.utils.QualitySelectorView;
import com.cvsoftware.chatcloud.utils.RecyclerObject;
import com.cvsoftware.chatcloud.utils.SQLHelper;
import com.cvsoftware.chatcloud.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class SavedActivity extends AppCompatActivity implements ColorPickerDialogListener{
    RecyclerObject object;
    ColorSelectorView colorSelector;
    FontSelectorView fontSelector;
    QualitySelectorView qualitySelector;
    AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        final ScrollView scrollView = findViewById(R.id.scrollView);
        long id = getIntent().getLongExtra("id",-1);
        SQLHelper sql = new SQLHelper(this);
        if(id!=-1){
            object = sql.loadObject(id);
            makeImage(Utils.getImagesFolder(SavedActivity.this) + ".thumbs/" + object.getName() +".png",false);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeImage(Utils.getImagesFolder(SavedActivity.this) + object.getName() +".png",true);
            }
        },500);
        qualitySelector = new QualitySelectorView(this, new ExpandableCardView.AsyncCreateFinishedListener() {
            @Override
            public void onAsyncCreateFinish() {
                ((LinearLayout)scrollView.findViewById(R.id.nestedScrollContent)).addView(qualitySelector);
                qualitySelector.setValue(object.quality);
                qualitySelector.attachToScrollView(scrollView);
            }
        });
        colorSelector = new ColorSelectorView(this, new ExpandableCardView.AsyncCreateFinishedListener() {
            @Override
            public void onAsyncCreateFinish() {
                ((LinearLayout)scrollView.findViewById(R.id.nestedScrollContent)).addView(colorSelector);
                colorSelector.attachToScrollView(scrollView);
                colorSelector.setColorModes(object.colorModes,false);
                colorSelector.setColors(object.colors,true);
                Log.i("COLORLOG","colorModes:"  + object.colorModes[0] + " : "+object.colorModes[1]);

            }
        });
        fontSelector = new FontSelectorView(this,new ExpandableCardView.AsyncCreateFinishedListener(){
            @Override
            public void onAsyncCreateFinish(){
                ((LinearLayout)scrollView.findViewById(R.id.nestedScrollContent)).addView(fontSelector);
                fontSelector.setSelectedFont(object.font);
                fontSelector.attachToScrollView(scrollView);
            }

        });



        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);






    }
    @Override
    public void onResume() {
        super.onResume();

        // Resume the AdView.
        mAdView.resume();
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

    public void makeImage(String path, boolean differentThread){
        final File imgFile = new  File(path);

        if(imgFile.exists()){
            if(differentThread){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((ImageView)findViewById(R.id.image)).setImageBitmap(myBitmap);
                            }
                        });
                    }
                }).start();
            }else{
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ((ImageView)findViewById(R.id.image)).setImageBitmap(myBitmap);
            }
        }else{

            ((ImageView)findViewById(R.id.image)).setImageResource(R.drawable.ic_priority_high_black_24dp);

        }
    }

    public void redoCloud(View v){
        boolean recreate = qualitySelector.getValue() != object.quality || fontSelector.getSelectedFont() != object.font;
        object.quality = qualitySelector.getValue();
        object.font = fontSelector.getSelectedFont();
        object.colorModes = colorSelector.getColorModes();
        object.colors = colorSelector.getColors();
        final Sketch sketch = new Sketch(this,object,recreate);
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        sketch.setProgressListener(new Sketch.ProgressListener() {
            @Override
            public void onProgress(final Sketch.Status status, final long progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(status == Sketch.Status.CREATING_ARRAY){
                            float a = 15f * progress / object.getWords().size();
                            loadingDialog.setProgress((int)a);
                        }else if(status == Sketch.Status.CREATING_CLOUD){
                            float a = 15 + 80f * progress / object.getWords().size();
                            loadingDialog.setProgress((int)a);
                        }else if(status == Sketch.Status.WRITING_TO_FILE){
                            loadingDialog.setProgress(95);
                        }
                        else if(status == Sketch.Status.FINISHED){
                            loadingDialog.cancel();
                            makeImage(Utils.getImagesFolder(SavedActivity.this) + object.getName() +".png",true);
                        }

                    }
                });

            }
        });
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                sketch.start();
            }
        });
        t.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.saved_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");
        File imageFile = new File(Utils.getImagesFolder(this) + object.getName() + ".png");
        Uri imageUri = FileProvider.getUriForFile(
                this,
                "com.cvsoftware.chatcloud.fileprovider", //(use your app signature + ".provider" )
                imageFile);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Select"));
        return true;
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        colorSelector.onColorSelected(dialogId,color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        colorSelector.applyColorToViews();
    }


}
